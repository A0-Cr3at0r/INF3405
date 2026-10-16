package server.chat;

import server.auth.AuthService;
import server.console.ServerConsole;
import common.model.ChatMessage;
import server.network.ClientConnection;
import server.persistence.HistoryManager;
import server.protocol.*;
import common.validator.InputValidator;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.time.Instant;

public class ChatCoordinator {
	private final AuthService authService;
    private final HistoryManager historyManager;
    private final ServerConsole console;

    private final Map<String, ClientConnection> activeUsers = new HashMap<>();
    
    public ChatCoordinator(
    		AuthService authService, 
    		HistoryManager historyManager,
    		ServerConsole serverConsole) {
    	this.authService = authService;
        this.historyManager = historyManager;
        this.console = serverConsole;
    }
    
    private synchronized void authenticateClient(
            String username,
            String password,
            ClientConnection connection) throws IOException {

        if (activeUsers.containsKey(username)) {
            throw new IllegalArgumentException("Cet utilisateur est déjà connecté.");
        }

        boolean valid = authService.authenticateOrRegister(username, password);

        if (!valid) {
            throw new IllegalArgumentException("Mot de passe incorrect.");
        }

        activeUsers.put(username, connection);
    }
    
    public synchronized void connectClient(
            String username,
            String password,
            ClientConnection connection) throws IOException {

        authenticateClient(username, password, connection);

        try {
            connection.sendPacket(new AuthSuccessPacket());
            connection.sendPacket(new HistoryPacket(historyManager.getRecentMessages()));
        } catch (IOException e) {
            disconnectClient(username, connection);
            throw e;
        }
    }
    
    public synchronized void disconnectClient(String username, ClientConnection connection) {
        if (activeUsers.get(username) == connection) {
            activeUsers.remove(username);
        }
    }
    
    public synchronized void handleMessage(
    		String username, 
    		ClientConnection sender, 
    		String content) throws IOException {
    	
    	if(activeUsers.get(username) != sender) {
    		throw new IllegalArgumentException("Cette connection n'est pas authentifiée.");
    	}
    	
    	InputValidator.validateMessage(content);
    	
    	ChatMessage message = new ChatMessage(
    			username,
    			sender.getClientIp(),
    			sender.getClientPort(),
    			Instant.now(),
    			content);
    	
    	historyManager.saveMessage(message);
    	console.showMessage(message);
    	
    	broadcastMessage(sender, message);
    }
    
    private void broadcastMessage(ClientConnection sender, ChatMessage message) {
    	ChatMessagePacket packet = new ChatMessagePacket(message);
    	
    	Map<String, ClientConnection> clients = new HashMap<>(activeUsers);
    	
    	for(Map.Entry<String, ClientConnection> entry : clients.entrySet()) {
    		ClientConnection client = entry.getValue();
    		
    		if(client == sender) {
    			continue;
    		}
    		
    		try {
    			client.sendPacket(packet);
    		} catch (IOException e) {
    			disconnectClient(entry.getKey(), client);
    			
    			closeConnection(client);
    		}
    	}
    }
    
    private void closeConnection(ClientConnection connection) {
        try {
            connection.close();
        } catch (IOException e) {
            System.err.println("Erreur de fermeture : " + e.getMessage());
        }
    }
}

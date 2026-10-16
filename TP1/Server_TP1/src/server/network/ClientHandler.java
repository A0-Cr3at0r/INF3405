package server.network;

import java.io.IOException;

import server.chat.ChatCoordinator;
import server.protocol.*;

public class ClientHandler implements Runnable {
	private final ClientConnection connection;
	private final ChatCoordinator chatCoordinator;
	
	public ClientHandler(ClientConnection connection, ChatCoordinator chatCoordinator) {
		this.connection = connection;
		this.chatCoordinator = chatCoordinator;
	}

	private String authenticate() throws IOException {
		Packet packet = connection.readPacket();
		
		if(!(packet instanceof AuthRequestPacket)) {
			throw new IOException("Le premier paquet doit etre une demande d'authentification");
		}
		
		AuthRequestPacket request = (AuthRequestPacket) packet;
		
		try {
			String username = request.getUsername();
			String password = request.getPassword();
			chatCoordinator.connectClient(username, password, connection);
		} catch (IllegalArgumentException e) {
			connection.sendPacket(new AuthFailurePacket(e.getMessage()));
			return null;
		}
		
		return request.getUsername();
	}
	
	private void receiveMessages(String username) throws IOException {
		while (true) {
			Packet packet = connection.readPacket();
			
			switch (packet.getType()) {
			case CHAT_SEND:
				ChatSendPacket message = (ChatSendPacket) packet;
				
				try {
					chatCoordinator.handleMessage(username, connection, message.getContent());
				} catch (IllegalArgumentException e) {
					connection.sendPacket(new ErrorPacket(e.getMessage()));
				}
				break;

			case DISCONNECT:
				return;
				
			default:
				throw new IOException("Paquet inconnu");
			}
		}
	}
	
	@Override
	public void run() {
		String username = null;
		
		try {
			username = authenticate();
			
			if (username == null) {
				return;
			}
			
			receiveMessages(username);
		} catch (IOException e) {
			System.err.println("Erreur de connexion : " + e.getMessage());
		} finally {
			closeConnection();
			
			if (username != null) {
				chatCoordinator.disconnectClient(username, connection);
			}
		}
	}
	
	private void closeConnection() {
	    try {
	        connection.close();
	    } catch (IOException e) {
	        System.err.println("Erreur de fermeture : " + e.getMessage());
	    }
	}
}

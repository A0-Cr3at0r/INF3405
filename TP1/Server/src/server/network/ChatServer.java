package server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import server.chat.ChatCoordinator;

public class ChatServer {
	private final String ip;
	private final int port;
	private ServerSocket serverSocket;
	private final ChatCoordinator chatCoordinator;
	
	private final Set<Socket> clients = new HashSet<>();
	
	private volatile boolean running = false;
	
	public ChatServer(String ip, int port, ChatCoordinator chatCoordinator) {
		this.ip = ip;
		this.port = port;
		this.chatCoordinator = chatCoordinator;
	}
	
	public void start() throws IOException {
		serverSocket = new ServerSocket();
		
		try {
			serverSocket.bind(new InetSocketAddress(ip, port));
			running = true;
		} catch (IOException | RuntimeException e) {
			closeServerSocket();
			throw e; //A voir si pas meilleur manière de lever l,ERREUR
		}
	}
	
	private void closeServerSocket() {
		if (serverSocket == null) {
			return;
		}
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Erreur de fermeture du serveur : " + e.getMessage());
		}
	}
	
	private void startClientHandler(Socket client) {
		try {
			ClientConnection connection = new ClientConnection(client);
			ClientHandler handler = new ClientHandler(connection, chatCoordinator);
			
			Thread thread = new Thread(() -> {
				try {
					handler.run();
				} finally {
					unregisterClient(client);
				}
			});
			
			thread.start();
		} catch (IOException | RuntimeException e) {
			closeSocket(client);
			unregisterClient(client);
			System.err.println("Impossible de demarrer la connexion : " + e.getMessage());
		}
	}
	
	private void closeSocket(Socket socket) {
	    try {
	        socket.close();
	    } catch (IOException e) {
	        System.err.println("Erreur de fermeture : " + e.getMessage());
	    }
	}
	
	public void acceptClients() throws IOException {
		while (running) {
			Socket client;
			
			try {
				client = serverSocket.accept();
			} catch (IOException e) {
				if (!running) {
					return;
				}
				
				throw e;
			}
			
			if(registerClient(client)) {
				startClientHandler(client);				
			} else {
				closeSocket(client);
			}
		}
	}
	
	private synchronized boolean registerClient(Socket client) {
	    if(!running) {
	    	return false;
	    }
		
		clients.add(client);
	    return true;
	}

	private synchronized void unregisterClient(Socket client) {
	    clients.remove(client);
	}
	
	public void stop() {
		Set<Socket> clientsToClose;
		
		//helped by AI
		synchronized (this) {
			running = false;
			clientsToClose = new HashSet<>(clients);
			clients.clear();
		}
		
		closeServerSocket();
		
		for(Socket client : clientsToClose) {
			closeSocket(client);
		}
	}
}

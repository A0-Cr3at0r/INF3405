package server;

import server.auth.AuthService;
import server.chat.ChatCoordinator;
import server.console.ServerConsole;
import server.network.ChatServer;
import server.persistence.HistoryManager;
import server.persistence.UserAccountManager;

import java.io.IOException;

public class ServerMain {
	
	public static void main(String[] args) {
		ServerConsole console = new ServerConsole();
		
		String ip = console.askIpAddress();
		int port = console.askPort();
		
		try {
			UserAccountManager accountManager = new UserAccountManager();
			HistoryManager historyManager = new HistoryManager();
			
			AuthService authService = new AuthService(accountManager);
			
			ChatCoordinator chatCoordinator = new ChatCoordinator(authService, historyManager, console);
			
			ChatServer server = new ChatServer(ip, port, chatCoordinator);
			
			//made with the help of AI
			Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
			
			try {
				server.start();
				console.showServerStarted(ip, port);
				server.acceptClients();
			} finally {
				server.stop();
			}
		} catch (IOException e) {
			System.err.println("Erreur du serveur : " + e.getMessage());
		}
	}
}

package server;

import server.auth.AuthService;
import server.chat.ChatCoordinator;
import common.console.Console;
import server.network.ChatServer;
import server.persistence.HistoryManager;
import server.persistence.UserAccountManager;

import java.io.IOException;
import java.util.Scanner;

public class ServerMain {
	
	public static void main(String[] args) {
		try(Scanner scanner = new Scanner(System.in)){
		
			Console console = new Console(scanner);
			
			String ip = console.askIpAddress();
			int port = console.askPort();

			UserAccountManager accountManager = new UserAccountManager();
			HistoryManager historyManager = new HistoryManager();
			
			AuthService authService = new AuthService(accountManager);
			
			ChatCoordinator chatCoordinator = new ChatCoordinator(authService, historyManager, console);
			
			ChatServer server = new ChatServer(ip, port, chatCoordinator);
			
			//made with the help of AI
			Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
			
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

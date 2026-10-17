package client;

import java.net.*;
import java.util.Scanner;
import java.io.*;

import authentication.ClientAuthenticator;
import chatCoordinator.ChatCoordinator;
import  common.console.Console;
import common.protocol.PacketDecoder;
import common.protocol.PacketEncoder;

public class Client {
    public static void main(String[] args) {
        PacketEncoder packetEncoder =  new PacketEncoder();
        PacketDecoder packetDecoder = new PacketDecoder();

        
        try(Scanner scanner = new Scanner(System.in)) {
        	Console console = new Console(scanner);
        	        	
        	String ip = console.askIpAddress();
        	int port = console.askPort();
     
        	while(true) {
		        try (Socket socket = new Socket(ip, port);
		             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		             DataInputStream in = new DataInputStream(socket.getInputStream())) {
		
		            ClientAuthenticator clientAuthenticator =
		                    new ClientAuthenticator(
		                            console,
		                            packetEncoder,
		                            packetDecoder,
		                            scanner
		                    );
		
		            String username = clientAuthenticator.authenticateMe(in, out);
		
		            ChatCoordinator chatCoordinator =
		                    new ChatCoordinator(
		                            username,
		                            console,
		                            packetDecoder,
		                            packetEncoder,
		                            socket,
		                            scanner
		                    );
		
		            chatCoordinator.launchChat(in, out);
		            chatCoordinator.waitForChatToEnd();
		            break;
		            
		        } catch (IOException e) {
		            System.err.println("Impossible de se connecter au serveur : " + e.getMessage());
		            break;
		        } 
        	}
    	} catch (InterruptedException e) {
        	Thread.currentThread().interrupt();
        }
    }
}
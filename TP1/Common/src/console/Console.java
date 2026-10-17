package common.console;

import java.util.Scanner;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import common.model.ChatMessage;
import common.validator.InputValidator;

public class Console {
	private final Scanner scanner = new Scanner(System.in);
	
	//was made with the help of AI
	private final DateTimeFormatter messageDateFormat =
		    DateTimeFormatter.ofPattern("uuuu-MM-dd@HH:mm:ss")
		                     .withZone(ZoneId.systemDefault());
	
	public String askIpAddress() {
		boolean invalidIp = true;
		String ip = "";
		
		while(invalidIp) {
			System.out.print("Adresse IP du serveur : ");
			ip = scanner.nextLine();
			
			try {
				InputValidator.validateIp(ip);
				invalidIp = false;
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		}
		return ip;
	}
	
	public int askPort() {
		String port = "";
		
		while(true) {
			System.out.print("Port du serveur : ");
			port = scanner.nextLine();
			
			try {
				return InputValidator.validatePort(port);
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void showServerStarted(String ip, int port) {

		System.out.println("Serveur en écoute sur " + ip + ":" + port);
	}
	
	public void showMessage(ChatMessage message) {
	    String date = messageDateFormat.format(message.getTimestamp());

	    System.out.println(
	        "[" + message.getUsername()
	        + " - " + message.getClientIp()
	        + ":" + message.getClientPort()
	        + " - " + date
	        + "] : " + message.getContent()
	    );
	}
	
	public static void showError(String message) {

		return;
	}
}

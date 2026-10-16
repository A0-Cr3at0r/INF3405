package server.persistence;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import server.model.ChatMessage;

public class HistoryManager {
	private final Path filePath = Paths.get("data/history.txt");
	private final List<ChatMessage> messages = new ArrayList<>();
	
	public HistoryManager() throws IOException {
		loadHistory();
	}
	
	public synchronized List<ChatMessage> getRecentMessages() {
		return new ArrayList<>(messages);
	}
	
	private void loadHistory() throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(
				filePath, 
				StandardCharsets.UTF_8)) {
			String line;
			int lineNumber = 0;
			
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				
				try {
					ChatMessage message = parseMessage(line);
					addRecentMessage(message);
				} catch (IllegalArgumentException e) {
					throw new IOException("Historique invalide a la ligne " + lineNumber, e);
				}
			}
		}
	}
	
	private ChatMessage parseMessage(String line) {
		String[] parts = line.split(";", 5);
		
		if (parts.length != 5) {
			throw new IllegalArgumentException("La ligne doit contenir 5 champs");
		}
		
		String username = parts[0];
		String clientIp = parts[1];
		int clientPort = Integer.parseInt(parts[2]);
		Instant timestamp = Instant.ofEpochMilli(Long.parseLong(parts[3]));
		String content = parts[4];
		
		return new ChatMessage(username, clientIp, clientPort, timestamp, content);
	}
	
	private void addRecentMessage(ChatMessage message) {
	    messages.add(message);

	    if (messages.size() > 15) {
	        messages.remove(0);
	    }
	}
	
	public synchronized void saveMessage(ChatMessage message) throws IOException {
		String content = message.getContent();
		
		if (content.contains("\n") || content.contains("\r")) {
			throw new IllegalArgumentException("Le message ne doit pas contenir de saut de ligne");
		}
		
		try (BufferedWriter writer = Files.newBufferedWriter(
				filePath,
				StandardCharsets.UTF_8,
				StandardOpenOption.APPEND)) {
			writer.write(message.getUsername() + ";"
					+ message.getClientIp() + ";"
					+ message.getClientPort() + ";"
					+ message.getTimestamp().toEpochMilli() + ";"
		            + content);
			writer.newLine();
		}
		
		addRecentMessage(message);
	}
}

package server.persistence;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.BufferedWriter;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;

import server.model.UserAccount;

public class UserAccountManager {
	private final Path filePath = Paths.get("data/users.txt");
    private final Map<String, UserAccount> accounts = new HashMap<>();

    public UserAccountManager() throws IOException {
        loadAccount();
    }

    public synchronized UserAccount findByUsername(String username) {
        return accounts.get(username);
    }
    
    private void loadAccount() throws IOException {
    	try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
    		String line;
    		int lineNumber = 0;
    		
    		while ((line = reader.readLine()) != null) {
    			lineNumber++;
    			
    			String[] parts = line.split(";", -1);
    			
    			if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
    				throw new IOException("Compte invalide a la ligne : " + lineNumber);
    			}
    			
                String username = parts[0];
                String password = parts[1];
                
                if (accounts.containsKey(username)) {
                    throw new IOException(
                        "Compte dupliqué à la ligne " + lineNumber
                    );
                }
                
                accounts.put(username, new UserAccount(username, password));
    		}
    	}
    }
    
    public synchronized void saveAccount(UserAccount account) throws IOException {
    	String username = account.getUsername();
        String password = account.getPassword();

        validateField(username);
        validateField(password);

        if (accounts.containsKey(username)) {
            throw new IllegalArgumentException("Ce compte existe déjà.");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(username + ";" + password);
            writer.newLine();
        }

        accounts.put(username, account);
    }
    
    private void validateField(String value) {
        if (value == null 
        		|| value.isEmpty()
                || value.contains(";")
                || value.contains("\n")
                || value.contains("\r")) {
            throw new IllegalArgumentException(
                "Les identifiants doivent être non vides, sans point-virgule ni saut de ligne."
            );
        }
    }
}

package serveur;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Serveur {
	// TODO: Déclarer ici une structure de données (ex: liste, HashMap) pour conserver la base de données des utilisateurs et mots de passe[cite: 16].
    // TODO: Déclarer ici une structure pour maintenir la liste de tous les flux clients actuellement connectés (pour pouvoir diffuser les messages)[cite: 16].
	
    public static void main(String[] args) {
    	
    	// TODO: Charger la base de données des utilisateurs et l'historique des conversations depuis des fichiers sur le disque afin d'assurer la persistance[cite: 17].
    	
        Scanner scanner = new Scanner(System.in);
        int port = 0;
        String ip = "";

        // Validation Regex pour IPV4
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        
        while (true) {
            System.out.print("Entrez l'adresse IP du serveur : ");
            ip = scanner.nextLine();
            if (ip.matches(ipRegex)) {
                break;
            } else {
                System.out.println("Erreur : Le format de l'adresse IP est invalide (ex attendu: 192.168.1.10).");
            }
        }

        while (port < 5000 || port > 5050) {
            System.out.print("Entrez le port d'écoute (entre 5000 et 5050) : ");
            if (scanner.hasNextInt()) {
                port = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Erreur : Veuillez entrer un nombre valide.");
                scanner.nextLine();
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip))) {
            System.out.println("Serveur démarré sur " + ip + ":" + port + ". En attente de connexions...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouvelle tentative de connexion depuis : " + clientSocket.getInetAddress());
                
                // TODO: Passer l'accès aux listes (utilisateurs, clients actifs, historique) au ClientHandler pour qu'il puisse les utiliser et les modifier.
                ClientHandler clientThread = new ClientHandler(clientSocket);
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        } finally {
        	// TODO: Lors de l'arrêt du serveur, s'assurer de fermer correctement toutes les connexions ouvertes et enregistrer les fichiers[cite: 16].
            scanner.close();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
            String username = in.readUTF();
            String password = in.readUTF();
            
            System.out.println("Demande d'authentification reçue pour l'utilisateur : " + username);
            
            boolean estAuthentifie = true; // Remplacer par vraie logique vérification
            
            // TODO: Implémenter la logique d'authentification complète
            
            if (estAuthentifie) {
                out.writeUTF("Connexion acceptée");
                out.flush();
                // Logique pour recevoir et diffuser les messages de ce client doit etre ecrite là
            } else {
                out.writeUTF("Connexion refusée");
                out.flush();
            }
            
        } catch (IOException e) {
            System.out.println("Connexion interrompue avec un client : " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
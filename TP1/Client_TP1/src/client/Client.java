package client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int port = 0;

        System.out.print("Entrez l'adresse IP du serveur : ");
        String ip = scanner.nextLine();
        
        // TODO: Valider le format de l'adresse IP saisie (quatre octets) avant la connexion, comme vous l'avez fait pour le serveur[cite: 16, 17].

        // Validation format du port
        while (true) {
            System.out.print("Entrez le port du serveur (entre 5000 et 5050) : ");
            try {
                port = Integer.parseInt(scanner.nextLine());
                if (port >= 5000 && port <= 5050) {
                    break;
                } else {
                    System.out.println("Erreur : Le port doit être compris entre 5000 et 5050.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur : Veuillez entrer un numéro de port valide composé uniquement de chiffres.");
            }
        }

        System.out.print("Entrez votre nom d'utilisateur : ");
        String username = scanner.nextLine();

        System.out.print("Entrez votre mot de passe : ");
        String password = scanner.nextLine();

        try (Socket socket = new Socket(ip, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            System.out.println("Connecté au serveur. Envoi des informations d'authentification...");
            
            // Envoi username et password au serveur
            out.writeUTF(username);
            out.writeUTF(password);
            out.flush();
            
            // Lecture de la réponse serveur
            String reponse = in.readUTF();
            System.out.println("Réponse du serveur : " + reponse);
            
            if (reponse.equals("Connexion acceptée")) {
                System.out.println("Authentification réussie. Vous pouvez maintenant clavarder !");
                // Logique pour envoyer et recevoir les messages en continu devra etre codé ici
                
                // TODO: Recevoir et afficher automatiquement les 15 messages les plus récents de l'historique fournis par le serveur[cite: 17].
                
                // TODO: Démarrer un nouveau Thread ici côté client pour écouter en continu (in.readUTF()) les messages diffusés par le serveur sans bloquer la saisie utilisateur[cite: 17].
                
                // TODO: Implémenter une boucle de saisie (scanner.nextLine()) permettant à l'utilisateur de taper et d'envoyer des messages (out.writeUTF())[cite: 17].
                // TODO: S'assurer de valider que la longueur du message saisi ne dépasse pas 200 caractères avant de l'envoyer[cite: 17].
                // TODO: Gérer la commande de déconnexion pour fermer proprement la session[cite: 17].
            } else {
                System.out.println("Authentification refusée par le serveur. Fin de la connexion.");
            }
            
        } catch (IOException e) {
            System.out.println("Impossible de se connecter au serveur : " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
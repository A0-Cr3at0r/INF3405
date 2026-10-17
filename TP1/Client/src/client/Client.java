package client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

import authentication.ClientAuthenticator;
import  common.console.Console;
import common.protocol.PacketDecoder;
import common.protocol.PacketEncoder;
import common.validator.InputValidator;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Console console = new Console();
        PacketEncoder packetEncoder =  new PacketEncoder();
        PacketDecoder packetDecoder = new PacketDecoder();

        String ip = console.askIpAddress();
        int port = console.askPort();


        try (Socket socket = new Socket(ip, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            ClientAuthenticator clientAuthenticator =
                    new ClientAuthenticator(
                            console,
                            packetEncoder,
                            packetDecoder
                    );

            clientAuthenticator.authenticateMe(in, out);

                // Logique pour envoyer et recevoir les messages en continu devra etre codé ici
                
                // TODO: Recevoir et afficher automatiquement les 15 messages les plus récents de l'historique fournis par le serveur[cite: 17].
                
                // TODO: Démarrer un nouveau Thread ici côté client pour écouter en continu (in.readUTF()) les messages diffusés par le serveur sans bloquer la saisie utilisateur[cite: 17].
                
                // TODO: Implémenter une boucle de saisie (scanner.nextLine()) permettant à l'utilisateur de taper et d'envoyer des messages (out.writeUTF())[cite: 17].
                // TODO: S'assurer de valider que la longueur du message saisi ne dépasse pas 200 caractères avant de l'envoyer[cite: 17].
                // TODO: Gérer la commande de déconnexion pour fermer proprement la session[cite: 17].

            
        } catch (IOException e) {
            System.err.println("Impossible de se connecter au serveur : " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
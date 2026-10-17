package client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

import authentication.ClientAuthenticator;
import chatCoordinator.ChatCoordinator;
import  common.console.Console;
import common.protocol.PacketDecoder;
import common.protocol.PacketEncoder;
import common.validator.InputValidator;

public class Client {
    public static void main(String[] args) {
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

            String username = clientAuthenticator.authenticateMe(in, out);

            ChatCoordinator chatCoordinator =
                    new ChatCoordinator(
                            username,
                            console,
                            packetDecoder,
                            packetEncoder,
                            socket
                    );

            chatCoordinator.launchChat(in, out);
            
        } catch (IOException e) {
            System.err.println("Impossible de se connecter au serveur : " + e.getMessage());
        }
    }
}
package authentication;

import common.protocol.*;
import common.console.Console;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class ClientAuthenticator {
     private final PacketDecoder packetDecoder;
     private final PacketEncoder packetEncoder;
     private final Console console;

     public ClientAuthenticator(Console console,
                                 PacketEncoder packetEncoder,
                                 PacketDecoder packetDecoder) {
        this.console = console;
        this.packetEncoder = packetEncoder;
        this.packetDecoder = packetDecoder;
     }


    public String authenticateMe(
            DataInputStream socketInput,
            DataOutputStream socketOutput) {
        String username;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Entrez votre nom d'utilisateur : ");
            username = scanner.nextLine();

            System.out.print("Entrez votre mot de passe : ");
            String password = scanner.nextLine();

            try {
                packetEncoder.write(
                        new AuthRequestPacket(username, password),
                        socketOutput
                );

                Packet packet = packetDecoder.read(socketInput);

                //This part was patially made with AI
                if (packet instanceof AuthSuccessPacket) {
                    console.showAuthSuccess();
                    break;

                } else if (packet instanceof AuthFailurePacket failure) {
                    console.showError(failure.getReason());

                } else {
                    throw new IOException(
                            "Paquet inattendu lors de l'authentification : "
                                    + packet.getClass().getSimpleName()
                    );
                }

            } catch (IOException e) {
                console.showError(e.getMessage());
                return username;
            }
        }

        scanner.close();

        return username;
    }

}

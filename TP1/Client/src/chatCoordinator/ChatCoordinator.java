        package chatCoordinator;

import common.console.Console;
import common.model.ChatMessage;
import common.protocol.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.util.Objects;
import java.util.Scanner;

//The code was refactored with AI
public class ChatCoordinator {

    private final String username;
    private final PacketDecoder packetDecoder;
    private final PacketEncoder packetEncoder;
    private final Console console;
    private final Socket socket;

    private volatile boolean receiverFlag = true;
    private volatile boolean emitterFlag = true;

    private Thread receiverThread;
    private Thread emitterThread;

    public ChatCoordinator(
            String username,
            Console console,
            PacketDecoder packetDecoder,
            PacketEncoder packetEncoder,
            Socket socket) {

        this.username = username;
        this.console = console;
        this.packetDecoder = packetDecoder;
        this.packetEncoder = packetEncoder;
        this.socket = socket;
    }

    public void launchChat(
            DataInputStream socketInput,
            DataOutputStream socketOutput) {

        if (!beginChat(socketInput)) {
            disconnect();
            return;
        }

        receiverFlag = true;
        emitterFlag = true;

        //Partially Made with AI
        receiverThread = new Thread(() -> {
            try {
                while (receiverFlag) {
                    Packet packet = packetDecoder.read(socketInput);
                    printMessage(packet);
                }
            } catch (IOException e) {
                if (receiverFlag) {
                    console.showError(
                            "Connexion interrompue : " + e.getMessage()
                    );
                }
            } finally {
                disconnect();
            }
        });

        //Partially Made with AI
        emitterThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (emitterFlag) {
                    System.out.print("Saisissez un message : ");

                    if (!scanner.hasNextLine()) {
                        disconnect();
                        break;
                    }

                    String message = scanner.nextLine();

                    if (!emitterFlag) {
                        break;
                    }

                    // Envoyer le message au serveur.
                    packetEncoder.write(
                            new ChatSendPacket(message),
                            socketOutput
                    );

                    console.showMessage(
                            new ChatMessage(
                                    username,
                                    socket.getLocalAddress().getHostAddress(),
                                    socket.getLocalPort(),
                                    Instant.now(),
                                    message
                            ),
                            true
                    );
                }
            } catch (IOException e) {
                if (emitterFlag) {
                    console.showError(
                            "Erreur lors de l'envoi : " + e.getMessage()
                    );
                }
            } finally {
                emitterFlag = false;
            }
        });

        receiverThread.start();
        emitterThread.start();
    }

    private boolean beginChat(DataInputStream socketInput) {
        try {
            Packet packet = packetDecoder.read(socketInput);

            if (packet instanceof HistoryPacket history) {
                for (ChatMessage message : history.getMessages()) {
                    console.showMessage(
                            message,
                            Objects.equals(
                                    username,
                                    message.getUsername()
                            )
                    );
                }

                console.beginNewChat();
                return true;
            }

            printMessage(packet);
            return true;

        } catch (IOException e) {
            console.showError(
                    "Impossible de récupérer l'historique : "
                            + e.getMessage()
            );
            return false;
        }
    }

    private void printMessage(Packet packet) throws IOException {
        if (packet instanceof ChatMessagePacket chatPacket) {
            ChatMessage message = chatPacket.getMessage();

            console.showMessage(
                    message,
                    Objects.equals(username, message.getUsername())
            );

        } else if (packet instanceof ErrorPacket errorPacket) {
            console.showError(errorPacket.getReason());

        } else if (packet instanceof DisconnectPacket) {
            console.showDisconnect();
            disconnect();

        } else {
            throw new IOException(
                    "Paquet inattendu dans le clavardage : "
                            + packet.getClass().getSimpleName()
            );
        }
    }

    private void disconnect() {
        receiverFlag = false;
        emitterFlag = false;

        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            console.showError(
                    "Erreur lors de la fermeture : " + e.getMessage()
            );
        }
    }
}

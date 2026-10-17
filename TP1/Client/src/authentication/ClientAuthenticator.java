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
	private final Scanner scanner;

	public ClientAuthenticator(Console console, PacketEncoder packetEncoder, PacketDecoder packetDecoder,
			Scanner scanner) {
		this.console = console;
		this.packetEncoder = packetEncoder;
		this.packetDecoder = packetDecoder;
		this.scanner = scanner;
	}

	public String authenticateMe(DataInputStream socketInput, DataOutputStream socketOutput) throws IOException {
		String username;

		System.out.print("Entrez votre nom d'utilisateur : ");
		username = scanner.nextLine();

		System.out.print("Entrez votre mot de passe : ");
		String password = scanner.nextLine();

		packetEncoder.write(new AuthRequestPacket(username, password), socketOutput);

		Packet packet = packetDecoder.read(socketInput);

		// This part was patially made with AI
		if (packet instanceof AuthSuccessPacket) {
			console.showAuthSuccess();
			return username;

		} else if (packet instanceof AuthFailurePacket failure) {
			throw new IOException(failure.getReason());

		} else {
			throw new IOException("Paquet inattendu lors de l'authentification : " + packet.getClass().getSimpleName());
		}
	}
}

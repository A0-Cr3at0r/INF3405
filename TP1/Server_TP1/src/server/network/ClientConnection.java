package server.network;

import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import server.protocol.PacketDecoder;
import server.protocol.PacketEncoder;
import server.protocol.Packet;

public class ClientConnection {
	private final Socket socket;
	
	private final DataInputStream input;
	private final DataOutputStream output;
	
	private final PacketDecoder decoder = new PacketDecoder();
	private final PacketEncoder encoder = new PacketEncoder();
	
	public ClientConnection(Socket socket) throws IOException {
		this.socket = socket;
	    this.input = new DataInputStream(socket.getInputStream());
	    this.output = new DataOutputStream(socket.getOutputStream());
	}
	
	public void close() throws IOException {
		socket.close();
	}
	
	public Packet readPacket() throws IOException {
	    return decoder.read(input);
	}

	public synchronized void sendPacket(Packet packet) throws IOException {
	    encoder.write(packet, output);
	}
	
	public String getClientIp() {
	    return socket.getInetAddress().getHostAddress();
	}

	public int getClientPort() {
	    return socket.getPort();
	}
}

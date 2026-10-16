package server.protocol;

public class AuthSuccessPacket extends Packet {
	public AuthSuccessPacket() {
        super(PacketType.AUTH_SUCCESS);
    }
}

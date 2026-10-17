package common.protocol;

public class DisconnectPacket extends Packet {
    public DisconnectPacket() {
        super(PacketType.DISCONNECT);
    }
}

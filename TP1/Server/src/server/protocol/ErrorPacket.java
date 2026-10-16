package server.protocol;

public class ErrorPacket extends Packet {
    private final String reason;

    public ErrorPacket(String reason) {
        super(PacketType.ERROR);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

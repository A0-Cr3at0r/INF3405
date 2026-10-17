package common.protocol;

public class AuthFailurePacket extends Packet {
	private final String reason;
	
	public AuthFailurePacket(String reason) {
		super(PacketType.AUTH_FAILURE);
		this.reason = reason;
	}
	
	public String getReason() {
		return reason;
	}
}

package common.protocol;

public class AuthRequestPacket extends Packet {
	private final String username;
	private final String password;
	
	public AuthRequestPacket(String username, String password) {
		super(PacketType.AUTH_REQUEST);
		this.username = username;
		this.password = password;
	}
	
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}

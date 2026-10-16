package server.protocol;


//client -> server
public class ChatSendPacket extends Packet {
	private final String content;
	
	public ChatSendPacket(String content) {
		super(PacketType.CHAT_SEND);
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
}

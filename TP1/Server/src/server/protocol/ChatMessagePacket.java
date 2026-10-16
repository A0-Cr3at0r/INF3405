package server.protocol;

import common.model.ChatMessage;


//server -> client 
public class ChatMessagePacket extends Packet {
	private final ChatMessage message;
	
	public ChatMessagePacket(ChatMessage message) {
		super(PacketType.CHAT_MESSAGE);
		this.message = message;
	}
	
	public ChatMessage getMessage() {
		return message;
	}
}

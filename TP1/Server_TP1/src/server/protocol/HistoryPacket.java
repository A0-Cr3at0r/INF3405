package server.protocol;

import java.util.List;
import java.util.ArrayList;
import server.model.ChatMessage;

public class HistoryPacket extends Packet {
	private final List<ChatMessage> messages;
	
	public HistoryPacket(List<ChatMessage> messages) throws IllegalArgumentException {
		super(PacketType.HISTORY);
		this.messages = new ArrayList<ChatMessage>(messages);
		
		if (messages.size() > 15) {
		    throw new IllegalArgumentException(
		        "Un paquet d’historique ne peut pas contenir plus de 15 messages."
		    );
		}
	}
	
	public List<ChatMessage> getMessages() {
		return messages;
	}

}

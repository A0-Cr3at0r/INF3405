package common.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import common.model.ChatMessage;

public class PacketDecoder {
	public Packet read(DataInputStream input) throws IOException {
		String typeName = input.readUTF();
		PacketType type;
		
		try {
			type = PacketType.valueOf(typeName);
		} catch (IllegalArgumentException e) {
			throw new IOException("Type de paquet inconnu : " + typeName, e);
		}
		
		return this.readPayload(type, input);
	}
	
	private Packet readPayload(PacketType type, DataInputStream input) throws IOException {
		switch(type) {
		case AUTH_REQUEST:
			String username = input.readUTF();
			String password = input.readUTF();
			return new AuthRequestPacket(username, password);
			
        case AUTH_SUCCESS:
            return new AuthSuccessPacket();

        case AUTH_FAILURE:
            String reason = input.readUTF();
            return new AuthFailurePacket(reason);
            
        case CHAT_SEND:
            return new ChatSendPacket(input.readUTF());

        case ERROR:
            return new ErrorPacket(input.readUTF());

        case DISCONNECT:
            return new DisconnectPacket();
            
        case CHAT_MESSAGE:
            return new ChatMessagePacket(readChatMessage(input));
            
        case HISTORY:
            return readHistory(input);
            
		default:
			throw new IOException("Type de packet inconnu pour : " + type);
		}
	}
	
	private ChatMessage readChatMessage(DataInputStream input) throws IOException {
	    String username = input.readUTF();
	    String clientIp = input.readUTF();
	    int clientPort = input.readInt();
	    Instant timestamp = Instant.ofEpochMilli(input.readLong());
	    String content = input.readUTF();

	    return new ChatMessage(
	        username, clientIp, clientPort, timestamp, content
	    );
	}
	
	private HistoryPacket readHistory(DataInputStream input) throws IOException {
	    int count = input.readInt();

	    if (count < 0 || count > 15) {
	        throw new IOException(
	            "Le nombre de messages d’historique doit être entre 0 et 15."
	        );
	    }

	    List<ChatMessage> messages = new ArrayList<>();

	    for (int i = 0; i < count; i++) {
	        messages.add(readChatMessage(input));
	    }

	    return new HistoryPacket(messages);
	}
}

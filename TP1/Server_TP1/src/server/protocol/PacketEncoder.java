package server.protocol;

import java.io.DataOutputStream;
import java.io.IOException;

import server.model.ChatMessage;

public class PacketEncoder {
	public void write(Packet packet, DataOutputStream output) throws IOException {
		output.writeUTF(packet.getType().name());
		writePayload(packet, output);
		output.flush();
	}
	
	private void writePayload(Packet packet, DataOutputStream output) throws IOException {
		switch (packet.getType()) {
			case AUTH_REQUEST:
				AuthRequestPacket request = (AuthRequestPacket) packet;
				output.writeUTF(request.getUsername());
				output.writeUTF(request.getPassword());
				break;
				
            case AUTH_SUCCESS:
                break;

            case AUTH_FAILURE:
                AuthFailurePacket failure = (AuthFailurePacket) packet;
                output.writeUTF(failure.getReason());
                break;
                
            case CHAT_SEND:
                ChatSendPacket chatSend = (ChatSendPacket) packet;
                output.writeUTF(chatSend.getContent());
                break;

            case CHAT_MESSAGE:
                ChatMessagePacket chatMessage = (ChatMessagePacket) packet;
                writeChatMessage(chatMessage.getMessage(), output);
                break;

            case HISTORY:
                HistoryPacket history = (HistoryPacket) packet;
                output.writeInt(history.getMessages().size());

                for (ChatMessage message : history.getMessages()) {
                    writeChatMessage(message, output);
                }
                break;

            case ERROR:
                ErrorPacket error = (ErrorPacket) packet;
                output.writeUTF(error.getReason());
                break;

            case DISCONNECT:
                break;
                
            default:
            	throw new IOException("Type de packet inconnu pour : " + packet.getType());
		}
	}
	
	private void writeChatMessage(ChatMessage message, DataOutputStream output) throws IOException {
	    output.writeUTF(message.getUsername());
	    output.writeUTF(message.getClientIp());
	    output.writeInt(message.getClientPort());
	    output.writeLong(message.getTimestamp().toEpochMilli());
	    output.writeUTF(message.getContent());
	}
}

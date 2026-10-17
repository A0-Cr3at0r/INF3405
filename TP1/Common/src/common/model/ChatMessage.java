package common.model;

import java.time.Instant;

public final class ChatMessage {
	private final String username;
	private final String clientIp;
	private final int clientPort;
	private final Instant timestamp;
	private final String content;
	
	public ChatMessage(String username, String clientIp, int clientPort, Instant timestamp, String content) {
		this.username = username;
		this.clientIp = clientIp;
        this.clientPort = clientPort;
        this.timestamp = timestamp;
	    this.content = content;
	}
	
    public String getUsername() {
        return username;
    }

    public String getClientIp() {
        return clientIp;
    }

    public int getClientPort() {
        return clientPort;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }
}

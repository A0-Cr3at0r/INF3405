package server.protocol;

public enum PacketType {
    AUTH_REQUEST,
    AUTH_SUCCESS,
    AUTH_FAILURE,
    HISTORY,
    CHAT_SEND,
    CHAT_MESSAGE,
    ERROR,
    DISCONNECT
}

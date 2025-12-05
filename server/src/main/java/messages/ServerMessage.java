package messages;

public class ServerMessage {

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION,
        PLAYER_JOINED,
        PLAYER_LEFT
    }

    public ServerMessageType serverMessageType;

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }
}



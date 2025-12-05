package messages;


public class NotificationMessage extends ServerMessage {

    public String message;

    public NotificationMessage(ServerMessageType type, String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}

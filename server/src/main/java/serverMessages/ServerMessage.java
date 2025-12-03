package serverMessages;

public class ServerMessage {
    public String serverMessageType;
    public Object data;

    public ServerMessage(String type, Object data) {
        this.serverMessageType = type;
        this.data = data;
    }
}

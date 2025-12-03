package clientMessages;

public class ConnectResponse {
    public String username;
    public int gameID;
    public String assignedRole;

    public ConnectResponse(String username, int gameID, String role) {
        this.username = username;
        this.gameID = gameID;
        this.assignedRole = role;
    }
}

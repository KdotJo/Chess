package request;

public record JoinGameRequest(String authToken, String whiteUsername, String blackUsername, int gameID) {
}

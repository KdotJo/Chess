package clientMessages;

import chess.ChessMove;

public class WebSocketMessage {
    public String commandType;
    public String authToken;
    public int gameID;
    public ChessMove move;
}

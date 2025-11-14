package request;

import chess.ChessGame;
import dataaccess.interfaces.HttpFacadeRequest;

public record JoinGameRequest(String authToken, String playerColor, int gameID) implements HttpFacadeRequest {

    public static String methodName = "PUT";
    public static String pathName = "/game";

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getPathName() {
        return pathName;
    }
}

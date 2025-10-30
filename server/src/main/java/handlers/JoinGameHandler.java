package handlers;

import dataaccess.DataAccessException;
import request.JoinGameRequest;
import result.JoinGameResult;
import service.GameService;

import io.javalin.http.Context;

import java.util.Map;

public class JoinGameHandler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    public void handleJoinGame (Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);
            if (authToken == null) {
                ctx.status(401).json(Map.of("message", "Error: Missing Auth Token"));
                return;
            }
            if (request.gameID() <= 0) {
                ctx.status(400).json(Map.of("message", "Error: Bad Game Id"));
                return;
            }
            if (request.playerColor() == null || request.playerColor().isEmpty()) {
                ctx.status(400).json(Map.of("message", "Error: Missing Team Color"));
                return;
            }
            if (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK")) {
                ctx.status(400).json(Map.of("message", "Error: Invalid Team Color"));
                return;
            }
            JoinGameResult result = gameService.join(authToken, request);
            ctx.status(200).json(Map.of());
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Team Already Taken")) {
                ctx.status(403).json(Map.of("message", e.getMessage()));
            } else if (e.getMessage().contains("Unauthorized")) {
                ctx.status(401).json(Map.of("message", e.getMessage()));
            } else {
                ctx.status(401).json(Map.of("message", e.getMessage()));
            }
        }
    }
}

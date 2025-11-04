package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.GameService;

import java.util.Map;

public class CreateGameHandler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    public void handleCreateGame (Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
            if (authToken == null || authToken.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Error: Unauthorized"));
                return;
            }
            if (request.gameName() == null || request.gameName().isEmpty()) {
                ctx.status(400).json(Map.of("message", "Error: Game name is missing"));
                return;
            }
            CreateGameResult result = gameService.create(authToken, request);
            ctx.status(200).json(result);
        } catch (DataAccessException e) {
            if ("failed to get connection".equals(e.getMessage())) {
                ctx.status(500).json(Map.of("message", e.getMessage().contains("Error") ? e.getMessage() : "Error: " + e.getMessage()));
            } else {
                ctx.status(401).json(Map.of("message", e.getMessage().contains("Error") ? e.getMessage() : "Error: " + e.getMessage()));
            }
        }
    }
}

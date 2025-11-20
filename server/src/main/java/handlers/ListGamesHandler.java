package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import result.ListGamesResult;
import service.GameService;

import java.util.Map;

public class ListGamesHandler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handleListGames (Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("Authorization");
            if (authToken == null || authToken.isEmpty()) {
                ctx.status(401).json(Map.of("message", "Error: Unauthorized"));
                return;
            }
            ListGamesResult result = gameService.list(authToken);
            ctx.status(200).json(result);
        } catch (DataAccessException e) {
            String message = e.getMessage().contains("Error") ? e.getMessage() : "Error: " + e.getMessage();
            int statusCode = "failed to get connection".equals(e.getMessage()) ? 500 : 401;
            ctx.status(statusCode).json(Map.of("message", message));
        }
    }

}

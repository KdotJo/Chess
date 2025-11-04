package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

import java.util.Map;

public class DatabaseHandler {
    private final GameService gameService;
    private final UserService userService;

    public DatabaseHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    public void clearDB (Context ctx) throws DataAccessException {
        try {
            gameService.clear();
            userService.clear();
            ctx.status(200).json(Map.of());
        } catch (DataAccessException e) {
            ctx.status(500).json(Map.of("message", e.getMessage().contains("Error") ? e.getMessage() : "Error: " + e.getMessage()));
        }
    }
}

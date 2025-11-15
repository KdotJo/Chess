package handlers;

import chess.ChessBoard;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameData;
import request.GetGameRequest;
import result.GetGameResult;
import service.GameService;

import java.util.Map;

public class GetGameHandler {
    private final GameService gameService;

    public GetGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handleGetGame(Context ctx) throws DataAccessException {
        try {
            int gameId = Integer.parseInt(ctx.pathParam("id"));
            GetGameRequest request = new GetGameRequest(gameId);
            GetGameResult result = gameService.get(request);
            ctx.json(result);
        } catch (DataAccessException e) {
            ctx.status(401).json(Map.of("message", e.getMessage().contains("Error") ? e.getMessage() : "Error: " + e.getMessage()));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("message", "Invalid game id."));
        }
    }
}

package handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDataAccess;
import dataaccess.interfaces.GameDataAccess;
import io.javalin.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clientMessages.ConnectMessage;
import clientMessages.WebSocketMessage;
import serverMessages.*;
import websocket.commands.UserGameCommand;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    public enum Role { WHITE, BLACK, SPECTATOR }
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private final GameDataAccess gameDao;
    private final AuthDataAccess authDao;

    public WebSocketHandler(GameDataAccess gameDao, AuthDataAccess authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public static class ClientInfo {
        String authToken;
        String username;
        int gameId;
    }

    private final Map<Integer, Set<WsContext>> activeGames = new ConcurrentHashMap<>();
    private final Map<WsContext, Role> gameRoles = new ConcurrentHashMap<>();
    private final Map<WsContext, ClientInfo> gameUsers = new ConcurrentHashMap<>();

    private void sendError(WsContext ctx, String error) {
        ctx.send(new Gson().toJson(new ErrorMessage(error)));
    }

    public void notification(int gameId, Object msg, WsContext exclude) {
        String json = new Gson().toJson(msg);

        for (WsContext ctx : activeGames.getOrDefault(gameId, Set.of())) {
            if (ctx != exclude) {
                try { ctx.send(json); }
                catch (Exception ignored) {}
            }
        }
    }

    public void handleConnect(WsContext ctx, WebSocketMessage msg) throws DataAccessException {

        int gameId = msg.gameID;
        String token = msg.authToken;

        var game = gameDao.getGame(gameId);
        if (game == null) {
            sendError(ctx, "Game does not exist");
            return;
        }

        var auth = authDao.getAuth(token);
        if (auth == null) {
            sendError(ctx, "Invalid auth token");
            return;
        }

        String username = auth.getUsername();
        ClientInfo info = new ClientInfo();
        info.authToken = token;
        info.username = username;
        info.gameId = gameId;

        gameUsers.put(ctx, info);

        activeGames.putIfAbsent(gameId, ConcurrentHashMap.newKeySet());
        activeGames.get(gameId).add(ctx);

        boolean whiteTaken = activeGames.get(gameId).stream()
                .anyMatch(s -> gameRoles.get(s) == Role.WHITE);

        boolean blackTaken = activeGames.get(gameId).stream()
                .anyMatch(s -> gameRoles.get(s) == Role.BLACK);

        Role assigned =
                !whiteTaken ? Role.WHITE :
                        !blackTaken ? Role.BLACK :
                                Role.SPECTATOR;

        gameRoles.put(ctx, assigned);

        LoadGameMessage loadMsg = new LoadGameMessage(game);
        ctx.send(new Gson().toJson(loadMsg));

        NotificationMessage joinMsg = new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                username + " joined as " + assigned.name()
        );
        notification(gameId, joinMsg, ctx);
    }

    public void handleLeave (WsContext ctx, WebSocketMessage msg) {
        ClientInfo info = gameUsers.get(ctx);
        if (info == null) {return;}
        int gameId = info.gameId;
        String username = info.username;
        activeGames.getOrDefault(gameId, Set.of()).remove(ctx);
        gameRoles.remove(ctx);
        gameUsers.remove(ctx);
        NotificationMessage leaveMessage = new NotificationMessage(
                ServerMessage.ServerMessageType.PLAYER_LEFT,
                username + " left the game"
        );

        notification(gameId, leaveMessage, ctx);
    }

    public void handleMove(WsContext ctx, WebSocketMessage msg) throws DataAccessException {
        int gameId = msg.gameID;
        String token = msg.authToken;
        var auth = authDao.getAuth(token);
        if (auth == null) {
            sendError(ctx, "Invalid AuthToken");
            return;
        }
        var game = gameDao.getGame(gameId);
        if (game == null) {
            sendError(ctx, "Invalid gameId");
            return;
        }
        String username = auth.getUsername();
        ClientInfo info = gameUsers.get(ctx);

        if (info == null || info.gameId != msg.gameID) {
            sendError(ctx, "You are not a part of the game");
            return;
        }

        Role role = gameRoles.get(ctx);
        if (role == Role.SPECTATOR) {
            sendError(ctx, "Spectators can't make moves");
        }
        boolean whiteTurn = game.getGame().getTeamTurn() == ChessGame.TeamColor.WHITE;

        if (whiteTurn && role != Role.WHITE || (!whiteTurn && role != Role.BLACK)) {
            sendError(ctx, "Not your turn");
            return;
        }
        try {
            game.getGame().makeMove(msg.move);
        } catch (Exception e) {
            sendError(ctx, "Invalid move");
            return;
        }
        gameDao.updateGame(gameId, game.getWhiteUsername(), game.getBlackUsername());

        LoadGameMessage update = new LoadGameMessage(game);

        notification(gameId, update, ctx);
    }


    public void connect(WsConnectContext ctx) {
        logger.info("WebSocket CONNECT: " + ctx.sessionId());
    }

    public void message(WsMessageContext ctx) throws DataAccessException {
        String raw = ctx.message();
        Gson gson = new Gson();

        WebSocketMessage msg = gson.fromJson(raw, WebSocketMessage.class);

        switch (msg.commandType) {
            case "CONNECT" -> handleConnect(ctx, msg);
            case "MAKE_MOVE" -> handleMove(ctx, msg);
            case "LEAVE" -> handleLeave(ctx, msg);

            default -> sendError(ctx, "Unknown command: " + msg.commandType);
        }
    }

    public void close(WsCloseContext ctx) {
        logger.info("WebSocket CLOSED: " + ctx.sessionId());

        ClientInfo info = gameUsers.remove(ctx);
        if (info != null) {
            activeGames.getOrDefault(info.gameId, Set.of()).remove(ctx);
            gameRoles.remove(ctx);
        }
    }
}

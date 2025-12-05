package handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDataAccess;
import dataaccess.interfaces.GameDataAccess;
import io.javalin.websocket.*;
import model.GameData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clientMessages.WebSocketMessage;
import serverMessages.*;

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

    public boolean ggChecker(ChessGame chess) {
        ChessGame.TeamColor currentTeam = chess.getTeamTurn();
        return chess.isInCheckmate(currentTeam) || chess.isInStalemate(currentTeam);
    }

    public void ggMessage(int gameId, ChessGame chess) {
        String message;
        if (chess.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            message = "Great Enemy Felled: WHITE, Congratulations Tarnished: Black";
        } else if (chess.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            message = "Great Enemy Felled: BLACK, Congratulations Tarnished: WHITE";
        } else {
            message = "A Great Display of Equal Skill: Stalemate";
        }

        NotificationMessage ggWp = new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                message
        );
        notificationEveryone(gameId, ggWp);
    }

    public void notificationExclude(int gameId, Object msg, WsContext exclude) {
        String json = new Gson().toJson(msg);

        for (WsContext ctx : activeGames.getOrDefault(gameId, Set.of())) {
            if (!ctx.sessionId().equals(exclude.sessionId())) {
                try { ctx.send(json); }
                catch (Exception e) { sendError(ctx, "Error: Notification Issue"); }
            }
        }
    }

    public void notificationEveryone(int gameId, Object msg) {
        String json = new Gson().toJson(msg);
        for (WsContext ctx : activeGames.getOrDefault(gameId, Set.of())) {
            try { ctx.send(json); }
            catch (Exception e) { sendError(ctx, "Error: Notification Issue"); }
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

        boolean whiteTaken = gameRoles.containsValue(Role.WHITE);
        boolean blackTaken = gameRoles.containsValue(Role.BLACK);

        Role assigned = !whiteTaken ? Role.WHITE :
                !blackTaken ? Role.BLACK :
                        Role.SPECTATOR;

        gameRoles.put(ctx, assigned);
        activeGames.get(gameId).add(ctx);

        ctx.send(new Gson().toJson(new LoadGameMessage(game)));

        NotificationMessage joinMsg = new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                username + " joined as " + assigned.name()
        );
        notificationExclude(gameId, joinMsg, ctx);
    }

    public void handleLeave(WsContext ctx, WebSocketMessage msg) {
        ClientInfo info = gameUsers.get(ctx);
        if (info == null) return;

        int gameId = info.gameId;
        String username = info.username;

        activeGames.getOrDefault(gameId, Set.of()).remove(ctx);
        gameRoles.remove(ctx);
        gameUsers.remove(ctx);

        NotificationMessage leaveMessage = new NotificationMessage(
                ServerMessage.ServerMessageType.PLAYER_LEFT,
                username + " left the game"
        );

        notificationExclude(gameId, leaveMessage, ctx);
    }

    public void handleMove(WsContext ctx, WebSocketMessage msg) throws DataAccessException {

        int gameId = msg.gameID;

        var auth = authDao.getAuth(msg.authToken);
        if (auth == null) {
            sendError(ctx, "Invalid AuthToken");
            return;
        }

        var game = gameDao.getGame(gameId);
        if (game == null) {
            sendError(ctx, "Invalid gameId");
            return;
        }

        ChessGame chess = game.getGame();
        ClientInfo info = gameUsers.get(ctx);

        if (info == null || info.gameId != gameId) {
            sendError(ctx, "You are not a part of the game");
            return;
        }

        Role role = gameRoles.get(ctx);
        boolean whiteTurn = chess.getTeamTurn() == ChessGame.TeamColor.WHITE;

        if (role == Role.SPECTATOR) {
            sendError(ctx, "Spectators can't make moves");
            return;
        }

        if ((whiteTurn && role != Role.WHITE) || (!whiteTurn && role != Role.BLACK)) {
            sendError(ctx, "Not your turn");
            return;
        }

        try {
            chess.makeMove(msg.move);
            gameDao.updateGame(
                    gameId,
                    game.getWhiteUsername(),
                    game.getBlackUsername(),
                    chess
            );
        } catch (Exception e) {
            sendError(ctx, "Invalid move");
            return;
        }

        GameData updated = new GameData(
                game.getGameID(),
                game.getWhiteUsername(),
                game.getBlackUsername(),
                game.getGameName(),
                chess
        );

        NotificationMessage moveMessage = new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                info.username + " has made a move to " + msg.move.toString()
        );
        if (ggChecker(chess)) {
            notificationEveryone(gameId, new LoadGameMessage(updated));
            notificationExclude(gameId, moveMessage, ctx);
            ggMessage(gameId, chess);
            return;
        }
        notificationEveryone(gameId, new LoadGameMessage(updated));
        notificationExclude(gameId, moveMessage, ctx);
    }

    public void connect(WsConnectContext ctx) {
        logger.info("WebSocket CONNECT: " + ctx.sessionId());
    }

    public void message(WsMessageContext ctx) throws DataAccessException {
        String raw = ctx.message();
        WebSocketMessage msg = new Gson().fromJson(raw, WebSocketMessage.class);

        switch (msg.commandType) {
            case "CONNECT" -> handleConnect(ctx, msg);
            case "MAKE_MOVE" -> handleMove(ctx, msg);
            case "LEAVE" -> handleLeave(ctx, msg);
            case "RESIGN" -> handleResign(ctx, msg);
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

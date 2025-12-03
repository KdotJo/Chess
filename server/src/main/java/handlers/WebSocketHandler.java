package handlers;

import clientMessages.ConnectResponse;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.mysql.MySqlAuthDao;
import clientMessages.ConnectMessage;
import dataaccess.mysql.MySqlGameDao;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clientMessages.WebSocketMessage;
import serverMessages.ServerMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    public enum Role { WHITE, BLACK, SPECTATOR }
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    public static class ClientInfo {
        String authToken;
        String username;
        int gameId;
    }

    private final Map<Integer, Set<WsContext>> activeGames = new ConcurrentHashMap<>();
    private final Map<WsContext, Role> gameRoles = new ConcurrentHashMap<>();
    private final Map<WsContext, ClientInfo> gameUsers = new ConcurrentHashMap<>();

    private void sendError(WsContext ctx, String error) {
        Gson gson = new Gson();
        ServerMessage msg = new ServerMessage("ERROR", error);

        ctx.send(gson.toJson(msg));
    }

    public void notification(int gameId, Object notification) {
        Gson gson = new Gson();
        String json = gson.toJson(notification);

        for (WsContext ctx : activeGames.getOrDefault(gameId, Set.of())) {
            try {
                ctx.send(json);
            } catch (Exception e) {
                logger.error("Failed to send notification", e);
            }
        }
    }


    public void handleConnect(WsContext ctx, WebSocketMessage msg) throws DataAccessException {

        Gson gson = new Gson();
        ConnectMessage data = gson.fromJson(gson.toJson(msg.data), ConnectMessage.class);

        int gameId = data.gameID;

        var gameDao = new MySqlGameDao();
        var game = gameDao.getGame(gameId);
        if (game == null) {
            sendError(ctx, "Game does not exist");
            return;
        }

        var authDao = new MySqlAuthDao();
        var auth = authDao.getAuth(data.authToken);
        if (auth == null) {
            sendError(ctx, "Invalid auth token");
            return;
        }
        String username = auth.getUsername();

        ClientInfo info = new ClientInfo();
        info.authToken = data.authToken;
        info.username = username;
        info.gameId = gameId;

        gameUsers.put(ctx, info);

        activeGames.putIfAbsent(gameId, ConcurrentHashMap.newKeySet());
        activeGames.get(gameId).add(ctx);

        boolean whiteTaken = activeGames.get(gameId).stream()
                .anyMatch(s -> gameRoles.get(s) == Role.WHITE);
        boolean blackTaken = activeGames.get(gameId).stream()
                .anyMatch(s -> gameRoles.get(s) == Role.BLACK);

        Role assignedRole =
                !whiteTaken ? Role.WHITE :
                        !blackTaken ? Role.BLACK :
                                Role.SPECTATOR;

        gameRoles.put(ctx, assignedRole);

        ConnectResponse response = new ConnectResponse(username, gameId, assignedRole.name());
        ServerMessage serverMsg = new ServerMessage("LOAD_GAME", response);

        ctx.send(gson.toJson(serverMsg));

        ServerMessage joinMsg = new ServerMessage(
                "PLAYER_JOINED",
                username + " joined as " + assignedRole.name()
        );

        notification(gameId, joinMsg);
    }

    public void connect(WsConnectContext ctx) {
        logger.info("WebSocket CONNECT: " + ctx.sessionId());
    }

    public void message(WsMessageContext ctx) throws DataAccessException {
        String rawMessage = ctx.message();  // FIXED: Javalin gives message via ctx.message()

        Gson gson = new Gson();
        WebSocketMessage wsMessage = gson.fromJson(rawMessage, WebSocketMessage.class);

        switch (wsMessage.commandType) {
            case "CONNECT" -> handleConnect(ctx, wsMessage);
            default -> sendError(ctx, "Unknown command: " + wsMessage.commandType);
        }
    }

    public void close(WsCloseContext ctx) {
        logger.info("WebSocket CLOSED: " + ctx.sessionId() + " reason=" + ctx.reason());

        ClientInfo info = gameUsers.remove(ctx);
        if (info != null) {
            activeGames.getOrDefault(info.gameId, Set.of()).remove(ctx);
            gameRoles.remove(ctx);
        }
    }
}

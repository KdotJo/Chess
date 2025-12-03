package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.mysql.MySqlAuthDao;
import messages.ConnectMessage;
import org.eclipse.jetty.websocket.api.Session;
import messages.WebSocketMessage;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebSocketHandler {
    public enum Role { WHITE, BLACK, SPECTATOR }
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    public static class ClientInfo {
        String authToken;
        String username;
    }

    private final Map<Integer, Set<Session>> activeGames = new ConcurrentHashMap<>();
    private final Map<Session, Role> gameRoles = new ConcurrentHashMap<>();
    private final Map<Session, ClientInfo> gameUsers = new ConcurrentHashMap<>();

    public void notification(int gameId, Object notification) {
        Gson gson = new Gson();
        String json = gson.toJson(notification);
        for (Session s : activeGames.getOrDefault(gameId, Set.of())) {
            try {
                s.getRemote().sendString(json);
            } catch (Exception e) {
                logger.error("Failed to send WebSocket message", e);
            }
        }
    }

    public void handleConnect(Session session, WebSocketMessage msg) throws DataAccessException {
        Gson gson = new Gson();
        ConnectMessage data = gson.fromJson(gson.toJson(msg.data), ConnectMessage.class);
        int gameId = data.gameID;
        String token = data.authToken;

        var authDao = new MySqlAuthDao();
        var username = authDao.getAuth(token).getUsername();

        if (username == null) {
            sendError(session, "Error: Invalid Auth Token (Websocket)");
            return;
        }

        ClientInfo info = new ClientInfo();
        info.authToken = token;
        info.username = username;
        gameUsers.put(session, info);

        activeGames.putIfAbsent(gameId, ConcurrentHashMap.newKeySet());
        activeGames.get(gameId).add(session);

        boolean whiteTaken = false;
        boolean blackTaken = false;

        Set<Session> sessions = activeGames.get(gameId);
        for (Session s: sessions) {
            Role role = gameRoles.get(s);
            if (role == Role.WHITE) {whiteTaken = true;}
            if (role == Role.BLACK) {blackTaken = true;}
        }
        Role assignedRole;
        if (!whiteTaken) {
            assignedRole = Role.WHITE;
        } else if (!blackTaken) {
            assignedRole = Role.BLACK;
        } else {
            assignedRole = Role.SPECTATOR;
        }

        gameRoles.put(session, assignedRole);
    }

    public void connect(Session session) {
    }


    public void message(Session session, String message) {
        Gson gson = new Gson();
        WebSocketMessage wsMessage = gson.fromJson(message, WebSocketMessage.class);

        switch (wsMessage.commandType) {
            case "CONNECT":
                handleConnect(session, wsMessage);
                break;
            case "MAKE_MOVE":
                handleMove(session, wsMessage);
                break;
            case "RESIGN":
                handleResign(session, wsMessage);
                break;
            case "LEAVE":
                handleLeave(session, wsMessage);
                break;
            default:
                sendError(session, "Error: Unknown Command " + "\"" + wsMessage.commandType + "\"");
        }
    }

    public void close(Session session, int status, String message) {

    }

    public WebSocketHandler () {
    }

}

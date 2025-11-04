package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.LogoutRequest;
import result.LogoutResult;
import service.UserService;

import java.util.Map;

public class LogoutHandler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public void handleLogout (Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                ctx.status(401).json(Map.of("message", "Error: Unauthorized"));
                return;
            }
            LogoutRequest req = new LogoutRequest(authToken);
            LogoutResult result = userService.logout(req);
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

package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.LoginRequest;
import result.LoginResult;
import service.UserService;

import java.util.Map;

public class LoginHandler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void handleLogin (Context ctx) {
        try {
            LoginRequest req = ctx.bodyAsClass(LoginRequest.class);
            if (req.username() == null || req.username().isEmpty()) {
                ctx.status(400).json(Map.of("message", "Error: Missing Username"));
                return;
            }
            if ((req.password() == null || req.password().isEmpty())) {
                ctx.status(400).json(Map.of("message", "Error: Bad/Missing Password"));
                return;
            }
            LoginResult result = userService.login(req);
            ctx.status(200).json(result);
        } catch (DataAccessException e) {
            String message = e.getMessage().contains("Error") ? e.getMessage() : "Error: " + e.getMessage() + " Please Check LoginHandler";
            int code = "failed to get connection".equals(e.getMessage()) ? 500 : 401;
            if (code == 500) {
                ctx.status(code).json(Map.of("message", message));
            } else {
                ctx.status(code).json(Map.of("message", message));
            }
        }
    }
}

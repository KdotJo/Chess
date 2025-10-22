package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

import java.util.Map;

public class RegistrationHandler {
    private final UserService userService;

    public RegistrationHandler(UserService userService) {
        this.userService = userService;
    }

    public void handleRegistration(Context ctx) {
        try {
            RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
            if ((req.password() == null || req.password().isEmpty() || (req.email() == null || req.email().isEmpty()))) {
                ctx.status(400).json(Map.of("message", "Error: Password Required"));
                return;
            }
            RegisterResult result = userService.register(req);
            ctx.status(200).json(result);
        } catch (DataAccessException e) {
            ctx.status(403).json(Map.of("message", e.getMessage()));
        }
    }
}

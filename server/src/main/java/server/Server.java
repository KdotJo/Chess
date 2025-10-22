package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import handlers.RegistrationHandler;
import io.javalin.*;
import io.javalin.http.Context;
import org.eclipse.jetty.server.Authentication;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server {


    private final Javalin javalin;
    private final RegistrationHandler registrationHandler;
    final private HashSet<String> validToken = new HashSet<>(Set.of("token1", "token2"));

    public Server() {
        UserDAO userDao = new UserDAO();
        AuthDAO authDao = new AuthDAO();
        UserService userService = new UserService(userDao, authDao);
        this.registrationHandler = new RegistrationHandler(userService);


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", registrationHandler::handleRegistration);

        // Register your endpoints and exception handlers here.

    }



    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

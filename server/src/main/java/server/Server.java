package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import handlers.DatabaseHandler;
import handlers.LoginHandler;
import handlers.LogoutHandler;
import handlers.RegistrationHandler;
import io.javalin.*;
import service.UserService;

import java.util.HashSet;
import java.util.Set;

public class Server {


    private final Javalin javalin;
    private final RegistrationHandler registrationHandler;
    private final DatabaseHandler databaseHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    final private HashSet<String> validToken = new HashSet<>(Set.of("token1", "token2"));

    public Server() {
        UserDAO userDao = new UserDAO();
        AuthDAO authDao = new AuthDAO();
        GameDAO gameDao = new GameDAO();
        UserService userService = new UserService(userDao, authDao);
        this.registrationHandler = new RegistrationHandler(userService);
        this.databaseHandler = new DatabaseHandler();
        this.loginHandler = new LoginHandler(userService);
        this.logoutHandler = new LogoutHandler(userService);


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .delete("/session", logoutHandler::handleLogout)
            .post("/session", loginHandler::handleLogin)
            .delete("/db", databaseHandler::clearDB)
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

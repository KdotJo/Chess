package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import handlers.*;
import io.javalin.*;
import service.GameService;
import service.UserService;

public class Server {


    private final Javalin javalin;
    private final RegistrationHandler registrationHandler;
    private final DatabaseHandler databaseHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final CreateGameHandler createGameHandler;

    public Server() {
        UserDAO userDao = new UserDAO();
        AuthDAO authDao = new AuthDAO();
        GameDAO gameDao = new GameDAO();
        UserService userService = new UserService(userDao, authDao);
        GameService gameService = new GameService(authDao, gameDao);
        this.registrationHandler = new RegistrationHandler(userService);
        this.databaseHandler = new DatabaseHandler(userDao, gameDao, authDao);
        this.loginHandler = new LoginHandler(userService);
        this.logoutHandler = new LogoutHandler(userService);
        this.createGameHandler = new CreateGameHandler(gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/game", createGameHandler::handleCreateGame)
            .post("/session", loginHandler::handleLogin)
            .delete("/session", logoutHandler::handleLogout)
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

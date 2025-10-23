package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import handlers.*;
import io.javalin.*;
import io.javalin.json.JavalinGson;
import service.GameService;
import service.UserService;

public class Server {


    private final Javalin javalin;
    private final RegistrationHandler registrationHandler;
    private final DatabaseHandler databaseHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final CreateGameHandler createGameHandler;
    private final JoinGameHandler joinGameHandler;
    private final ListGamesHandler listGamesHandler;

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
        this.joinGameHandler = new JoinGameHandler(gameService);
        this.listGamesHandler = new ListGamesHandler(gameService);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        })
            .put("/game", joinGameHandler::handleJoinGame)
            .get("/game", listGamesHandler::handleListGames)
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

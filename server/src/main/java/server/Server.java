package server;

import dataaccess.DataAccessException;
import dataaccess.mysql.MySqlAuthDao;
import dataaccess.mysql.MySqlGameDao;
import dataaccess.mysql.MySqlUserDao;
import dataaccess.interfaces.AuthDataAccess;
import dataaccess.interfaces.GameDataAccess;
import dataaccess.interfaces.UserDataAccess;
import handlers.*;
import io.javalin.*;
import io.javalin.json.JavalinGson;
import service.GameService;
import service.UserService;

public class Server {


    private final Javalin javalin;
    private final RegistrationHandler registrationHandler;
    private final GetGameHandler getGameHandler;
    private final DatabaseHandler databaseHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final CreateGameHandler createGameHandler;
    private final JoinGameHandler joinGameHandler;
    private final ListGamesHandler listGamesHandler;

    public Server() {
        try {
            UserDataAccess userDao = new MySqlUserDao();
            AuthDataAccess authDao = new MySqlAuthDao();
            GameDataAccess gameDao = new MySqlGameDao();
            UserService userService = new UserService(userDao, authDao);
            GameService gameService = new GameService(authDao, gameDao);
            this.registrationHandler = new RegistrationHandler(userService);
            this.databaseHandler = new DatabaseHandler(gameService, userService);
            this.loginHandler = new LoginHandler(userService);
            this.logoutHandler = new LogoutHandler(userService);
            this.createGameHandler = new CreateGameHandler(gameService);
            this.getGameHandler = new GetGameHandler(gameService);
            this.joinGameHandler = new JoinGameHandler(gameService);
            this.listGamesHandler = new ListGamesHandler(gameService);
        } catch (DataAccessException e) {
            throw new RuntimeException("Internal Server Error", e);
        }
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        })
            .put("/game", joinGameHandler::handleJoinGame)
            .get("/data", getGameHandler::handleGetGame)
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

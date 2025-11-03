package server;

import dataaccess.DataAccessException;
import dataaccess.MySqlDAO.MySqlGameDAO;
import dataaccess.MySqlDAO.MySqlUserDAO;
import dataaccess.interfaces.AuthDataAccess;
import dataaccess.interfaces.GameDataAccess;
import dataaccess.interfaces.UserDataAccess;
import dataaccess.memoryDAO.MemoryAuthDAO;
import dataaccess.memoryDAO.MemoryGameDAO;
import dataaccess.memoryDAO.MemoryUserDAO;
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
        try {
            UserDataAccess UserDao = new MySqlUserDAO();
            AuthDataAccess AuthDao = new MemoryAuthDAO();
            GameDataAccess GameDao = new MySqlGameDAO();
            UserService userService = new UserService(UserDao, AuthDao);
            GameService gameService = new GameService(AuthDao, GameDao);
            this.registrationHandler = new RegistrationHandler(userService);
            this.databaseHandler = new DatabaseHandler(UserDao, GameDao, AuthDao);
            this.loginHandler = new LoginHandler(userService);
            this.logoutHandler = new LogoutHandler(userService);
            this.createGameHandler = new CreateGameHandler(gameService);
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

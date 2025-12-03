package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.memory.MemoryAuthDao;
import dataaccess.memory.MemoryGameDao;
import dataaccess.memory.MemoryUserDao;
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
    private final DatabaseHandler databaseHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final CreateGameHandler createGameHandler;
    private final JoinGameHandler joinGameHandler;
    private final ListGamesHandler listGamesHandler;


    public Server() {
        UserDataAccess userDao;
        AuthDataAccess authDao;
        GameDataAccess gameDao;
        try {
            DatabaseManager.configureDatabase();
             userDao = new MySqlUserDao();
             authDao = new MySqlAuthDao();
             gameDao = new MySqlGameDao();
        } catch (DataAccessException e) {
             userDao = new MemoryUserDao();
             authDao = new MemoryAuthDao();
             gameDao = new MemoryGameDao();
        }
        UserService userService = new UserService(userDao, authDao);
        GameService gameService = new GameService(authDao, gameDao);
        this.registrationHandler = new RegistrationHandler(userService);
        this.databaseHandler = new DatabaseHandler(gameService, userService);
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

        WebSocketHandler wsHandler = new WebSocketHandler();

        javalin.ws("/ws", ws -> {
            ws.onConnect(wsHandler::connect);
            ws.onMessage(wsHandler::message);
            ws.onClose(wsHandler::close);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

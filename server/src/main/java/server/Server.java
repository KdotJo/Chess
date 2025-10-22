package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import handlers.DatabaseHandler;
import handlers.RegistrationHandler;
import io.javalin.*;
import service.UserService;

import java.util.HashSet;
import java.util.Set;

public class Server {


    private final Javalin javalin;
    private final RegistrationHandler registrationHandler;
    private final DatabaseHandler databaseHandler;
    final private HashSet<String> validToken = new HashSet<>(Set.of("token1", "token2"));

    public Server() {
        UserDAO userDao = new UserDAO();
        AuthDAO authDao = new AuthDAO();
        GameDAO gameDao = new GameDAO();
        UserService userService = new UserService(userDao, authDao);
        this.registrationHandler = new RegistrationHandler(userService);
        this.databaseHandler = new DatabaseHandler();


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
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

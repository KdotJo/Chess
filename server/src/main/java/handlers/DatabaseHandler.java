package handlers;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import request.RegisterRequest;
import result.RegisterResult;

import java.util.Map;

public class DatabaseHandler {
    private final UserDAO userDao;
    private final GameDAO gameDao;
    private final AuthDAO authDao;

    public DatabaseHandler(UserDAO userDao, GameDAO gameDao, AuthDAO authDao) {

        this.userDao = userDao;
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public void clearDB (Context ctx) {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
        ctx.status(200).json(Map.of());
    }
}

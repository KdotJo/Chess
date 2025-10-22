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
    UserDAO userDao = new UserDAO();
    AuthDAO authDao = new AuthDAO();
    GameDAO gameDao = new GameDAO();

    public void clearDB (Context ctx) {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
        ctx.status(200).json(Map.of());
    }
}

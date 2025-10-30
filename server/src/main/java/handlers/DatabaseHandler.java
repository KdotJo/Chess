package handlers;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDataAccess;
import dataaccess.interfaces.GameDataAccess;
import dataaccess.interfaces.UserDataAccess;
import dataaccess.memoryDAO.MemoryAuthDAO;
import dataaccess.memoryDAO.MemoryGameDAO;
import io.javalin.http.Context;

import java.util.Map;

public class DatabaseHandler {
    private final UserDataAccess UserDao;
    private final GameDataAccess GameDao;
    private final AuthDataAccess AuthDao;

    public DatabaseHandler(UserDataAccess UserDao, GameDataAccess GameDao, AuthDataAccess AuthDao) {

        this.UserDao = UserDao;
        this.GameDao = GameDao;
        this.AuthDao = AuthDao;
    }

    public void clearDB (Context ctx) throws DataAccessException {
        UserDao.clear();
        AuthDao.clear();
        GameDao.clear();
        ctx.status(200).json(Map.of());
    }
}

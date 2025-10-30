package handlers;

import dataaccess.memoryDAO.MemoryAuthDAO;
import dataaccess.memoryDAO.MemoryGameDAO;
import dataaccess.memoryDAO.MemoryUserDAO;
import io.javalin.http.Context;

import java.util.Map;

public class DatabaseHandler {
    private final MemoryUserDAO memoryUserDao;
    private final MemoryGameDAO memoryGameDao;
    private final MemoryAuthDAO memoryAuthDao;

    public DatabaseHandler(MemoryUserDAO memoryUserDao, MemoryGameDAO memoryGameDao, MemoryAuthDAO memoryAuthDao) {

        this.memoryUserDao = memoryUserDao;
        this.memoryGameDao = memoryGameDao;
        this.memoryAuthDao = memoryAuthDao;
    }

    public void clearDB (Context ctx) {
        memoryUserDao.clear();
        memoryAuthDao.clear();
        memoryGameDao.clear();
        ctx.status(200).json(Map.of());
    }
}

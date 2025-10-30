package dataaccess;


import dataaccess.memoryDAO.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
//    user related interface requirements

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    boolean usernameExists(String username) throws DataAccessException;

//    game related interface requirements
    void createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername) throws DataAccessException;

//    auth related interface requirements
    String createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}

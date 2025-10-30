package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDataAccess {

    //    game related interface requirements
    void createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername) throws DataAccessException;

    void clear() throws DataAccessException;
}

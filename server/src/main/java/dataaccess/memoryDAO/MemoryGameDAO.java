package dataaccess.memoryDAO;

import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDataAccess;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDataAccess {

    HashMap<Integer, GameData> mockDB = new HashMap<>();

    public void createGame (GameData gameData) {
        int gameId = gameData.getGameID();
        mockDB.put(gameId, gameData);
    }

    public GameData getGame (int gameID) {
        return mockDB.get(gameID);
    }

    public Collection<GameData> listGames () {
        return mockDB.values();
    }

    public void updateGame (int gameID, String whiteUsername, String blackUsername) throws DataAccessException {
        GameData exists = mockDB.get(gameID);
        GameData updated = new GameData(gameID, whiteUsername, blackUsername, exists.getGameName(), exists.getGame());
        mockDB.put(gameID, updated);
    }

    public void clear () {
        mockDB.clear();
    }
}

package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO {

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
        if (exists == null) {
            throw new DataAccessException("Error: Game doesn't exist");
        }
        GameData updated = new GameData(gameID, whiteUsername, blackUsername, exists.getGameName(), exists.getGame());
        mockDB.put(gameID, updated);
    }

    public void clear () {
        mockDB.clear();
    }
}

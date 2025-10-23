package dataaccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class GameDAO {

    HashMap<Integer, GameData> mockDB = new HashMap<>();

    public void createGame (GameData gameData) {
        int gameId = gameData.getGameID();
        mockDB.put(gameId, gameData);
    }

//    public void getGame (int gameID) {
//
//    }

    //public void listGames () {
    // }

    //public void updateGame () {
    // }

    public void clear () {
        mockDB.clear();
    }
}

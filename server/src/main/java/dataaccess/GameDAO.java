package dataaccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;

public class GameDAO {

    HashMap<String, GameData> mockDB = new HashMap<>();

    //public void createGame (UserData userData) {
    // }

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

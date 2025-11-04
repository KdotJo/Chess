package dataaccess.memoryDao;

import dataaccess.interfaces.UserDataAccess;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDataAccess {

    HashMap<String, UserData> mockDB = new HashMap<>();


    public void createUser (UserData userData) {
        // TODO: Store user in database
        // Check if username already exists
        // If not, add to mockDB
        // If yes, throw exception
        mockDB.put(userData.getUsername(), userData);
    }

    public UserData getUser (String username) {
        return mockDB.get(username);
     }

    public void clear () {
        mockDB.clear();
    }
}

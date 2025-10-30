package dataaccess.memoryDAO;

import dataaccess.DataAccessException;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO {

    HashMap<String, UserData> mockDB = new HashMap<>();


    public void createUser (UserData userData) throws DataAccessException {
        // TODO: Store user in database
        // Check if username already exists
        // If not, add to mockDB
        // If yes, throw exception
        String username = userData.getUsername();
        if (usernameExists(username)) {
            throw new DataAccessException("Error: already taken");
        }
        else {mockDB.put(username, userData);}
    }

    public UserData getUser (String username) {
        return mockDB.get(username);
     }


    public boolean usernameExists (String username) {
        return mockDB.containsKey(username);
    }


    public void clear () {
        mockDB.clear();
    }
}

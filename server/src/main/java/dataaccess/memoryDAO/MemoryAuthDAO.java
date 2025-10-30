package dataaccess.memoryDAO;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDataAccess;
import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDataAccess {

    HashMap<String, AuthData> mockDB = new HashMap<>();

    public String createAuth (String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        mockDB.put(authToken, authData);
        return authToken;
     }

    public AuthData getAuth (String authToken) throws DataAccessException {
        return mockDB.get(authToken);
    }

    public void deleteAuth (String authToken) throws DataAccessException{
        if (!mockDB.containsKey(authToken)) {throw new DataAccessException("Error: Unauthorized");}
        mockDB.remove(authToken);
    }

    public void clear () {
        mockDB.clear();
    }

}

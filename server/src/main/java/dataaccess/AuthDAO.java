package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDAO {

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

    //public void deleteAuth () {
    // }

    public void clear () {
        mockDB.clear();
    }

}

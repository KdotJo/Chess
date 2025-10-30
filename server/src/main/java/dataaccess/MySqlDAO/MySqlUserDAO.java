package dataaccess.MySqlDAO;

import dataaccess.interfaces.UserDataAccess;
import dataaccess.DataAccessException;
import model.UserData;


public class MySqlUserDAO implements UserDataAccess {
    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public boolean usernameExists(String username) throws DataAccessException {
        return false;
    }

    @Override
    public void clear() throws DataAccessException {

    }


}

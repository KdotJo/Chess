package dataaccess.interfaces;


import dataaccess.DataAccessException;
import model.UserData;

public interface UserDataAccess {
//    user related interface requirements

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clear() throws DataAccessException;
}

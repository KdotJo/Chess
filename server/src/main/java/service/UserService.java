package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

import javax.xml.crypto.Data;

public class UserService {

    private final AuthDAO authDao;
    private final UserDAO userDao;

    public UserService (UserDAO userDao, AuthDAO authDao) {
        this.authDao = authDao;
        this.userDao = userDao;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        // TODO:
        // 1. Check if username already exists (use UserDAO)
        // 2. If exists, throw exception
        // 3. If not, create new UserData
        // 4. Store in database (use UserDAO)
        // 5. Generate auth token (use AuthDAO)
        // 6. Return RegisterResult with username and authToken

        if (userDao.usernameExists(registerRequest.username())) {
            throw new DataAccessException("Error: Username already taken");
        }
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDao.createUser(newUser);
        String authToken = authDao.createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(), authToken);
    }

//    public LoginResult login(LoginRequest loginRequest) {
//
//    }
//
//    public void logout(LogoutRequest logoutRequest) {
//
//    }
}

package service;

import dataaccess.interfaces.AuthDataAccess;
import dataaccess.interfaces.UserDataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.ClearResult;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;


public class UserService {

    private final AuthDataAccess authDao;
    private final UserDataAccess userDao;

    public UserService (UserDataAccess userDao, AuthDataAccess authDao) {
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

        if (userDao.getUser(registerRequest.username()) != null) {
            throw new DataAccessException("Error: Username already taken");
        }
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDao.createUser(newUser);
        String authToken = authDao.createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(), authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (userDao.getUser(loginRequest.username()) != null) {
            UserData user = userDao.getUser(loginRequest.username());
            if (BCrypt.checkpw(loginRequest.password(), user.getPassword())) {
                String authToken = authDao.createAuth(loginRequest.username());
                return new LoginResult(loginRequest.username(), authToken);
            }
            throw new DataAccessException("Error: Missing Password");
        }
        throw new DataAccessException("Error: Bad Request");

    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException{
        if (authDao.getAuth(logoutRequest.authToken()) == null) {
            throw new DataAccessException("Error: No authToken");
        }
        authDao.deleteAuth(logoutRequest.authToken());
        return new LogoutResult();
    }

    public ClearResult clear() throws DataAccessException {
        try {
            userDao.clear();
            return new ClearResult();
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: Clear Failed");
        }
    }
}

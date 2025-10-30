package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;


public class UserService {

    private final MemoryAuthDAO memoryAuthDao;
    private final MemoryUserDAO memoryUserDao;

    public UserService (MemoryUserDAO memoryUserDao, MemoryAuthDAO memoryAuthDao) {
        this.memoryAuthDao = memoryAuthDao;
        this.memoryUserDao = memoryUserDao;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        // TODO:
        // 1. Check if username already exists (use UserDAO)
        // 2. If exists, throw exception
        // 3. If not, create new UserData
        // 4. Store in database (use UserDAO)
        // 5. Generate auth token (use AuthDAO)
        // 6. Return RegisterResult with username and authToken

        if (memoryUserDao.usernameExists(registerRequest.username())) {
            throw new DataAccessException("Error: Username already taken");
        }
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        memoryUserDao.createUser(newUser);
        String authToken = memoryAuthDao.createAuth(registerRequest.username());
        return new RegisterResult(registerRequest.username(), authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (memoryUserDao.usernameExists(loginRequest.username())) {
            UserData user = memoryUserDao.getUser(loginRequest.username());
            if (user.getPassword().equals(loginRequest.password())) {
                String authToken = memoryAuthDao.createAuth(loginRequest.username());
                return new LoginResult(loginRequest.username(), authToken);
            }
            throw new DataAccessException("Error: Missing Password");
        }
        throw new DataAccessException("Error: Bad Request");

    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException{
        if (memoryAuthDao.getAuth(logoutRequest.authToken()) == null) {
            throw new DataAccessException("Error: No authToken");
        }
        memoryAuthDao.deleteAuth(logoutRequest.authToken());
        return new LogoutResult();
    }
}

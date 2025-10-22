package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import request.RegisterRequest;
import result.RegisterResult;

public class UserService {

    private final AuthDAO authDao;
    private final UserDAO userDao;

    public UserService (UserDAO userDao, AuthDAO authDao) {
        this.authDao = authDao;
        this.userDao = userDao;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        registerRequest.username();
    }

//    public LoginResult login(LoginRequest loginRequest) {
//
//    }
//
//    public void logout(LogoutRequest logoutRequest) {
//
//    }
}

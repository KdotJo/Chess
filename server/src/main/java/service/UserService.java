package service;

import request.RegisterRequest;
import result.RegisterResult;

public class UserService {
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

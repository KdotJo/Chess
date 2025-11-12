package client;

import com.google.gson.Gson;
import exceptions.ServerFacadeException;
import request.CreateGameRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;

import result.CreateGameResult;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import java.net.URI;
import dataaccess.interfaces.HttpFacadeRequest;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    private HttpRequest.BodyPublisher makeBody (Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeBody(body));
//        System.out.println("Attempting request to: " + serverUrl + path);
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpResponse<String> response(HttpRequest request) throws ServerFacadeException {
        try {
            HttpResponse<String> http = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (http.statusCode() != 200) {
                String error = http.body();
                throw new ServerFacadeException(
                        "Server error: " + http.statusCode() +
                        "Error Message: " + error
                );
            }
            return http;
        } catch (Exception e) {
//            System.out.println("Exception class: " + e.getClass());
//            System.out.println("Exception message: " + e.getMessage());
            throw new ServerFacadeException("HTTP has failed to communicate", e);
        }
    }

    public <Http extends HttpFacadeRequest, Obj> Obj facadeMethod(Http request, Class<Obj> result) throws ServerFacadeException{
        HttpRequest httpRequest = buildRequest(request.getMethodName(), request.getPathName(), request);
        HttpResponse<String> response = response(httpRequest);
        Gson gson = new Gson();
        return gson.fromJson(response.body(), result);
    }

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(String username, String password, String email) throws ServerFacadeException {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        return facadeMethod(registerRequest, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws ServerFacadeException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        HttpRequest httpRequest = buildRequest("POST", "/session", loginRequest);
        HttpResponse<String> response = response(httpRequest);
        Gson gson = new Gson();
        return gson.fromJson(response.body(), LoginResult.class);

        /* working codes
        LoginRequest loginRequest = new LoginRequest(username, password);
        HttpRequest httpRequest = buildRequest("POST", "/session", loginRequest);
        HttpResponse<String> response = response(httpRequest);
        Gson gson = new Gson();
        return gson.fromJson(response.body(), LoginResult.class);
        */
    }

/*
    public LogoutResult logout(String authToken) throws ServerFacadeException {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        HttpRequest httpRequest = buildRequest("DELETE", "/session", logoutRequest);
        HttpResponse<String> response = response(httpRequest);
        Gson gson = new Gson();
        return gson.fromJson(response.body(), LogoutResult.class);
    }
    public CreateGameResult create(String authToken, String gameName) throws ServerFacadeException {
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
        HttpResponse<String> response = response(httpRequest);
        Gson gson = new Gson();
        return gson.fromJson(response.body(), CreateGameResult.class);
    }
*/

}

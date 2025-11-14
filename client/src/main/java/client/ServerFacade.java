package client;

import com.google.gson.Gson;
import exceptions.ServerFacadeException;
import request.*;

import result.*;

import java.net.URI;
import dataaccess.interfaces.HttpFacadeRequest;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken;

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
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null && !authToken.isEmpty()) {
            request.setHeader("Authorization", authToken);
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
        RegisterResult result =  facadeMethod(registerRequest, RegisterResult.class);
        this.authToken = result.authToken();
        return result;
    }

    public LoginResult login(String username, String password) throws ServerFacadeException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult result =  facadeMethod(loginRequest, LoginResult.class);
        this.authToken = result.authToken();
        return result;
    }

    public LogoutResult logout() throws ServerFacadeException {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        return facadeMethod(logoutRequest, LogoutResult.class);
    }

    public CreateGameResult create(String gameName) throws ServerFacadeException {
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
        return facadeMethod(createGameRequest, CreateGameResult.class);
    }

    public ListGamesResult list() throws ServerFacadeException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        return facadeMethod(listGamesRequest, ListGamesResult.class);
    }
}

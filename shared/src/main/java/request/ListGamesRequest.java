package request;

import http.HttpFacadeRequest;

public record ListGamesRequest(String authToken) implements HttpFacadeRequest {

    public static String methodName = "GET";
    public static String pathName = "/game";

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getPathName() {
        return pathName;
    }
}

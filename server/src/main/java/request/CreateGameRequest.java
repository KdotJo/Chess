package request;

import dataaccess.interfaces.HttpFacadeRequest;

public record CreateGameRequest(String gameName) implements HttpFacadeRequest {

    public static String methodName = "POST";
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

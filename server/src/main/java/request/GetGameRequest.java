package request;

import dataaccess.interfaces.HttpFacadeRequest;

public record GetGameRequest(int gameID) implements HttpFacadeRequest {

    public static String methodName = "GET";
    public static String pathName = "/data";

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getPathName() {
        return pathName;
    }
}

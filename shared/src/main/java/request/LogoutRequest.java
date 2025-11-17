package request;

import HttpRequest.HttpFacadeRequest;

public record LogoutRequest(String authToken) implements HttpFacadeRequest {

    public static String methodName = "DELETE";
    public static String pathName = "/session";

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getPathName() {
        return pathName;

    }
}

package request;

import HttpRequest.HttpFacadeRequest;

public record RegisterRequest(String username,
                              String password,
                              String email) implements HttpFacadeRequest {

    public static String methodName = "POST";
    public static String pathName = "/user";

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getPathName() {
        return pathName;
    }
}

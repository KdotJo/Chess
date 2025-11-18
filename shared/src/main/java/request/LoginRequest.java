package request;

import http.HttpFacadeRequest;

public record LoginRequest(String username,
                           String password) implements HttpFacadeRequest {
    public static String methodName = "POST";
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

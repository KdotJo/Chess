package request;

public record CreateGameRequest(String authToken, String gameName) {
    static String methodName = "Post";
    static String pathName = "/session";
}

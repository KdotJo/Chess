package exceptions;

public class ServerFacadeException extends Exception{
    public ServerFacadeException() {
        super();
    }

    public ServerFacadeException(String message) {
        super(message);
    }

    public ServerFacadeException(String message, Throwable cause) {
        super(message, cause);
    }
}

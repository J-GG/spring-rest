package fr.jg.springrest.errors.pojo;

public class ServerException extends DetailedException {

    public ServerException(final String message) {
        super(message);
    }

    public ServerException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

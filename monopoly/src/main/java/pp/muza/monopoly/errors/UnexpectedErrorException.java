package pp.muza.monopoly.errors;

/**
 * @author dmytromuza
 */
public class UnexpectedErrorException extends RuntimeException {

    public UnexpectedErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}

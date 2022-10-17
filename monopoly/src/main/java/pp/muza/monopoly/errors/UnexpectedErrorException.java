package pp.muza.monopoly.errors;

/**
 * @author dmytromuza
 */
public class UnexpectedErrorException extends RuntimeException {

    public UnexpectedErrorException(Throwable cause) {
        super("This should never happen", cause);
    }
}

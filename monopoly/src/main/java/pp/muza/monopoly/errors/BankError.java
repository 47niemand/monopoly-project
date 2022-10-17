package pp.muza.monopoly.errors;

/**
 * @author dmytromuza
 */

public enum BankError {
    /**
     * Player has not enough money to pay.
     */
    NOT_ENOUGH_COINS("Not enough coins"),
    /**
     * Negative value
     */
    NEGATIVE_VALUE("Negative value");

    private final String message;

    BankError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

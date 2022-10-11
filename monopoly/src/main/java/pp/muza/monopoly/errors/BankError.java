package pp.muza.monopoly.errors;

public enum BankError {
    /**
     * Player has not enough money to pay.
     */
    NOT_ENOUGH_COINS("Not enough coins");

    private final String message;

    BankError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

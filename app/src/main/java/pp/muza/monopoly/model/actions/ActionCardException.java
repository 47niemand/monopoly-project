package pp.muza.monopoly.model.actions;

public class ActionCardException extends Exception {

    public final ActionCard actionCard; // the action card that caused the exception
    public final boolean isFinal; // true if the player has no more actions to perform

    public ActionCardException(Exception e, ActionCard actionCard) {
        this(e, actionCard, true);
    }

    public ActionCardException(Exception e, ActionCard actionCard, boolean isFinal) {
        super(e.getMessage());
        this.actionCard = actionCard;
        this.isFinal = isFinal;
    }

    public ActionCardException(String message, ActionCard actionCard) {
        super(message);
        this.actionCard = actionCard;
        this.isFinal = true;
    }
}

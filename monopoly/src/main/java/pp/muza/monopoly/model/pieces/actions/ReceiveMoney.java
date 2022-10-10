package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;


/**
 * A player receives coins from the card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReceiveMoney extends Income {

    /**
     * The sender of the coins.
     */
    private final Player sender;

    protected ReceiveMoney(int priority, int value, Player sender) {
        super(priority, value);
        this.sender = sender;
    }

    ReceiveMoney(int number, Player sender) {
        this(HIGHEST_PRIORITY, number, sender);
    }

    public static ActionCard of(int number, Player sender) {
        return new ReceiveMoney(number, sender);
    }
}

package pp.muza.monopoly.model.pieces.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.Player;



/**
 * A player receives coins from the card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReceiveMoney extends Income {

    private static final Logger LOG = LoggerFactory.getLogger(ReceiveMoney.class);

    /**
     * The sender of the coins.
     */
    private final Player sender;

    protected ReceiveMoney(String name, ActionType actionType, int priority, Integer number, Player sender) {
        super(name, actionType, priority, number);
        this.sender = sender;
    }

    ReceiveMoney(Integer number, Player sender) {
        this("Receive Money", ActionType.OBLIGATION, HIGHEST_PRIORITY, number, sender);
    }
}

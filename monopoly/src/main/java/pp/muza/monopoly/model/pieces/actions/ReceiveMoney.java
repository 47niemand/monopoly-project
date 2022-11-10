package pp.muza.monopoly.model.pieces.actions;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;


/**
 * A player receives coins from the card.
 *
 * @author dmytromuza
 */
@Getter
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

    ReceiveMoney(int value, Player sender) {
        this(HIGHEST_PRIORITY, value, sender);
    }

    public static ActionCard create(int value, Player sender) {
        return new ReceiveMoney(value, sender);
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                ImmutableMap.of("sender", sender)
        );
    }
}

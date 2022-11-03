package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * A base class for paying coins to another player.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class BasePayment extends BaseDebt {

    private static final Logger LOG = LoggerFactory.getLogger(BasePayment.class);

    protected final Player recipient;

    protected BasePayment(ActionType type, int priority, int value, Player recipient) {
        super(type, priority, value);
        this.recipient = recipient;
    }

    /**
     * Return cards if the player succeeds in paying the number to the recipient.
     * Can be overridden by subclasses to perform additional actions.
     *
     * @param turn the turn to execute the action on.
     * @return cards if the player succeeds in paying the number to the recipient.
     */
    @Override
    protected List<ActionCard> onSuccess(Turn turn) {
        try {
            turn.sendCard(recipient, new ReceiveMoney(value, turn.getPlayer()));
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Error sending money to recipient " + recipient, e);
        }
        return ImmutableList.of();
    }

    @Override
    protected Map<String, Object> params() {
        return mergeMaps(
                super.params(),
                ImmutableMap.of("recipient", recipient)
        );
    }
}

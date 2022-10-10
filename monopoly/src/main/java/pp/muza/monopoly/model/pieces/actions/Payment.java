package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * A base class for paying coins to another player.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Payment extends Debt {

    private static final Logger LOG = LoggerFactory.getLogger(Payment.class);

    protected final Player recipient;

    protected Payment(String name, ActionType type, int priority, int value, Player recipient) {
        super(name, type, priority, value);
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
            LOG.error("Error during executing the action: {}", this, e);
            throw new RuntimeException(e);
        }
        return ImmutableList.of();
    }
}

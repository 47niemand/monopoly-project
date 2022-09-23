package pp.muza.monopoly.model.pieces.actions;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.BankException;
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
public class Payment extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Payment.class);

    protected final Player recipient;
    protected final int value;

    protected Payment(String name, ActionType type, int priority, Player recipient, int value) {
        super(name, Action.PAY, type, priority);
        this.recipient = recipient;
        this.value = value;
    }

    Payment(int value, Player recipient) {
        this("Payment", ActionType.OBLIGATION, HIGH_PRIORITY, recipient, value);
    }

    public static ActionCard of(Integer valueOf, Player recipient) {
        return new Payment(valueOf, recipient);
    }

    /**
     * Return cards if the player cannot pay to the recipient.
     *
     * @param turn the turn to execute the action on.
     * @return cards if the player cannot pay the tax.
     */
    protected final List<ActionCard> onFailure(Turn turn) {
        return ImmutableList.<ActionCard>builder().add(this).addAll(CardUtils.createContractsForPlayerPossession(turn))
                .build();
    }

    /**
     * Check if the player can use the card.
     *
     * @param turn the turn to execute the action on.
     * @throws TurnException if the player cannot use the card.
     */
    protected void check(Turn turn) throws TurnException {
        // subclasses should override this method and throw an exception if the player cannot use the card.
    }

    /**
     * Return cards if the player succeeds in paying the number to the recipient.
     * Can be overridden by subclasses to perform additional actions.
     *
     * @param turn the turn to execute the action on.
     * @return cards if the player succeeds in paying the number to the recipient.
     */
    protected List<ActionCard> onSuccess(Turn turn) {
        try {
            turn.sendCard(recipient, new ReceiveMoney(value, turn.getPlayer()));
        } catch (TurnException e) {
            LOG.error("Error during executing the action: {}", this, e);
            throw new RuntimeException(e);
        }
        return ImmutableList.of();
    }

    @Override
    protected final List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            check(turn);
            try {
                turn.withdraw(value);
                result = onSuccess(turn);
            } catch (BankException e) {
                LOG.warn("Player cannot pay: {}", e.getMessage());
                result = onFailure(turn);
            }
        } catch (TurnException e) {
            LOG.warn("Player cannot play card: {}", e.getMessage());
            result = ImmutableList.of();
        }
        return result;
    }

}

package pp.muza.monopoly.model.pieces.actions;


import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.errors.BaseGameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
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
    protected final Integer number;

    protected Payment(String name, ActionType type, int priority, Player recipient, Integer number) {
        super(name, Action.PAY, type, priority);
        this.recipient = recipient;
        this.number = number;
    }

    Payment(Integer number, Player recipient) {
        this("Payment", ActionType.OBLIGATION, DEFAULT_PRIORITY, recipient, number);
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
    protected List<ActionCard> onFailure(Turn turn, BaseGameException e) {
        List<ActionCard> result;
        if (e instanceof BankException) {
            result = ImmutableList.<ActionCard>builder().add(this).addAll(CardUtils.createContractsForPlayerPossession(turn))
                    .build();
        } else if (e instanceof TurnException) {
            result = ImmutableList.of();
        } else {
            throw new IllegalStateException(e);
        }
        return result;
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
     *
     * @param turn the turn to execute the action on.
     * @return cards if the player succeeds in paying the number to the recipient.
     */
    protected List<ActionCard> onSuccess(Turn turn) {
        turn.sendCard(recipient, new ReceiveMoney(number, turn.getPlayer()));
        return ImmutableList.of();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            check(turn);
            turn.withdraw(number);
            result = onSuccess(turn);
        } catch (BaseGameException e) {
            LOG.warn("Player cannot play card: {}", e.getMessage());
            result = onFailure(turn, e);
        }
        return result;
    }

}

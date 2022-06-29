package pp.muza.monopoly.model.actions.cards;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.turn.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.lands.Property;

/**
 * The player has to pay money to the property owner on which player is standing.
 *
 * TODO: implement a pair of same color = double rent!
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PayRent extends ActionCard {

    private final Player recipient;
    private final BigDecimal amount;

    PayRent(Player recipient, Land land) {
        super("Pay Rent", Action.PAY_RENT, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.recipient = recipient;
        if (land.getType() == Land.Type.PROPERTY) {
            this.amount = ((Property) land).getRent();
        } else {
            throw new IllegalArgumentException("Land must be a property");
        }
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        try {
            turn.payRent(recipient, amount);
        } catch (BankException e) {
            // try to create a contract for each property in the player's possession
            boolean contractCreated = ActionUtils.createContractsForPlayersPossession(turn);
            throw new ActionCardException(e, this, !contractCreated);
        }
    }
}

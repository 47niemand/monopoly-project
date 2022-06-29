package pp.muza.monopoly.model.actions.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.turn.Turn;
import pp.muza.monopoly.model.lands.Property;

import java.math.BigDecimal;

/**
 * A contract for a property.
 * <p>
 * The player who owns the property can sale it to get a profit.
 * This card spawns when the player has no enough money to pay obligations.
 * </p>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Contract extends ActionCard {

    private final int landId; // the id of the land to be traded
    private final Property property; // the property being sent

    Contract(int landId, Property property) {
        super("Contract", Action.CONTRACT, Type.CONTRACT, DEFAULT_PRIORITY);
        this.landId = landId;
        this.property = property;
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        try {
            BigDecimal amount = property.getPrice();
            turn.doContract(landId, property, amount);
        } catch (BankException e) {
            throw new ActionCardException(e, this);
        }
    }
}

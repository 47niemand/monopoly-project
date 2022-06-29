package pp.muza.monopoly.model.actions.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.turn.Turn;
import pp.muza.monopoly.model.turn.TurnException;
import pp.muza.monopoly.model.lands.Property;

/**
 * This card can be used to purchase a property from the board at the player's current position.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Buy extends ActionCard {

    private final int landId;
    private final Property property;

    Buy(int landId, Property property) {
        super("Buy", Action.BUY, Type.OPTIONAL, DEFAULT_PRIORITY);
        this.landId = landId;
        this.property = property;
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        try {
            turn.buyProperty(landId, property);
        } catch (BankException | TurnException e) {
            throw new ActionCardException(e, this);
        }
    }
}

package pp.muza.monopoly.model.actions.contracts;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.lands.Property;

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
        super("Contract", ActionType.CONTRACT, 0, false);
        this.landId = landId;
        this.property = property;
    }

    public static Contract of(int landId, Property property) {
        return new Contract(landId, property);
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        Game game = turn.getGame();
        Player player = turn.getPlayer();
        try {
            BigDecimal amount = property.getPrice();
            game.contract(player, landId, property, amount);
        } catch (BankException e) {
            throw new ActionCardException(e, this);
        }
    }
}

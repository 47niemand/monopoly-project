package pp.muza.monopoly.model.actions.strategy;

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
 * This card can be used to purchase a property from the board at the player's current position.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Buy extends ActionCard {

    private final int landId;
    private final Property property;

    Buy(int landId, Property property) {
        super("Buy", ActionType.BUY, 100, false);
        this.landId = landId;
        this.property = property;
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        Game game = turn.getGame();
        Player player = turn.getPlayer();
        try {
            game.buyProperty(player, landId, property);
        } catch (BankException e) {
            throw new ActionCardException(e, this);
        }
    }
}

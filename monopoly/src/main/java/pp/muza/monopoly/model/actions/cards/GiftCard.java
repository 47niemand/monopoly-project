package pp.muza.monopoly.model.actions.cards;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.lands.Property;

/**
 * This is a special card that allows the player to buy property from the game.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class GiftCard extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(GiftCard.class);

    private final int landId;
    private final Property property;

    GiftCard(int landId, Property property) {
        super("Gift card", Action.GIFT, Type.CHANCE, HIGH_PRIORITY);
        this.landId = landId;
        this.property = property;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        if (turn.getPropertyOwner(landId) == null) {
            return ImmutableList.of(new BuyObligation(landId, property));
        } else {
            return ImmutableList.of(new Trade(landId, property));
        }
    }
}

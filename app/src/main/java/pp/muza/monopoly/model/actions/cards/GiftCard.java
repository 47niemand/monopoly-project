package pp.muza.monopoly.model.actions.cards;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.lands.Property;

import java.util.List;

/**
 * This is a special card that allows the player to get property from the game.
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
        try {
            turn.ownProperty(landId, property);
        } catch (TurnException e) {
            LOG.info("Player cannot own property: {}", e.getMessage());
        }
        return ImmutableList.of();
    }
}

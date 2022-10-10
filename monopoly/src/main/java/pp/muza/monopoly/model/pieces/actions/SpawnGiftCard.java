package pp.muza.monopoly.model.pieces.actions;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.Turn;

/**
 * Buy a free property. If all are owned, purchase from any player.
 **/
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class SpawnGiftCard extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(SpawnGiftCard.class.getName());

    SpawnGiftCard() {
        super("Gift: Move and Takeover", Action.GIFT, ActionType.OBLIGATION, DEFAULT_PRIORITY);
    }

    public static ActionCard of() {
        return new SpawnGiftCard();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        LOG.debug("Spawning MoveAndTakeover cards");
        List<IndexedEntry<Property>> properties = turn.getFreeProperties();
        if (properties.isEmpty()) {
            // if there are no free properties, the player has to choose one of the
            // properties he does not own
            properties = turn.getAllProperties().stream()
                    .filter(x -> turn.getPropertyOwner(x.getIndex()) != turn.getPlayer()).collect(Collectors.toList());
        }
        return properties.stream().map(x -> new MoveAndTakeover(x.getIndex())).collect(Collectors.toList());
    }
}

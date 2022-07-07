package pp.muza.monopoly.model.actions.cards;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class SpawnGiftCard extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(SpawnGiftCard.class.getName());

    SpawnGiftCard() {
        super("Spawn gift cards", Action.GIFT, Type.OBLIGATION, HIGH_PRIORITY);
    }

    public static SpawnGiftCard of() {
        return new SpawnGiftCard();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        LOG.info("Spawning gift cards");
        List<Land.Entry<Property>> properties = turn.getFreeProperties();
        if (properties.isEmpty()) {
            properties = turn.getAllProperties().stream().filter(x -> turn.getPropertyOwner(x.getPosition()) != turn.getPlayer()).collect(Collectors.toList());
        }
        return properties.stream().map(x -> new GiftCard(x.getPosition(), x.getLand())).collect(Collectors.toList());
    }
}

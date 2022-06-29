package pp.muza.monopoly.model.actions.cards.chance;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.turn.Turn;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class GiftCard extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(GiftCard.class.getName());

    GiftCard() {
        super("GiftCard", Action.GIFT, Type.OPTIONAL, DEFAULT_PRIORITY);
    }

    public static GiftCard of() {
        return new GiftCard();
    }

    @Override
    protected void onExecute(Turn turn) {
        LOG.info("Spawning gift cards");
        List<Land.Entry<Property>> properties = turn.getFreeProperties();
        if (properties.isEmpty()) {
            properties = turn.getAllProperties().stream().filter(x -> turn.getLandOwner(x.getPosition()) != turn.getPlayer()).collect(Collectors.toList());
        }
        List<ActionCard> giftCards = properties.stream().map(x -> new SpawnGiftCard(x.getPosition(), x.getLand())).collect(Collectors.toList());
        for (ActionCard giftCard : giftCards) {
            turn.addActionCard(giftCard);
        }
    }
}

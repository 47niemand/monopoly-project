package pp.muza.monopoly.model.actions.cards;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

/**
 * The player has to pay money to the property owner on which player is
 * standing.
 * <p>
 * TODO: implement a pair of same color = double rent!
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PayRent extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(PayRent.class);

    private final Player recipient;
    private final BigDecimal amount;

    PayRent(Player recipient, Land land) {
        super("Pay Rent", Action.PAY, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.recipient = recipient;
        if (land.getType() == Land.Type.PROPERTY) {
            this.amount = ((Property) land).getRent();
        } else {
            throw new IllegalArgumentException("Land must be a property");
        }
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            turn.payRent(recipient, amount);
            result = ImmutableList.of();
        } catch (BankException e) {
            LOG.info("Player cannot pay money: {}", e.getMessage());
            result = ImmutableList.<ActionCard>builder().addAll(createContractsForPlayerPossession(turn)).add(this).build();
        }
        return result;
    }

    public static List<ActionCard> createContractsForPlayerPossession(Turn turn) {
        LOG.info("Creating contracts for player's possession");
        List<Land.Entry<Property>> properties = turn.getProperties();
        return properties.stream()
                .map(property -> new Contract(property.getPosition(), property.getLand()))
                .collect(Collectors.toList());
    }
}

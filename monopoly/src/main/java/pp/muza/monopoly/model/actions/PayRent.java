package pp.muza.monopoly.model.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.game.Player;

import static pp.muza.monopoly.model.actions.CardUtils.createContractsForPlayerPossession;

/**
 * The player has to pay money to the property owner on which player is
 * standing.
 * TODO: implement a pair of same color = double rent!
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PayRent extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(PayRent.class);

    private final Player recipient;
    private final int landId;
    private final Property property;

    PayRent(Player recipient, Land land, int landId, Property property) {
        super("Pay Rent", Action.PAY, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.recipient = recipient;
        this.landId = landId;
        this.property = property;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            turn.payRent(landId);
            result = ImmutableList.of();
        } catch (BankException e) {
            LOG.info("Player cannot pay money: {}", e.getMessage());
            result = ImmutableList.<ActionCard>builder().addAll(createContractsForPlayerPossession(turn)).add(this).build();
        } catch (TurnException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


}

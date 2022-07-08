package pp.muza.monopoly.model.actions;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.lands.Start;

import java.util.List;

/**
 * This is a special card that allows the player to buy property from the game.
 * On your next turn, go forward to any free space and buy it. If all are owned, buy one from any player
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class BuyOrTrade extends AbstractActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(BuyOrTrade.class);

    private final int landId;
    private final Property property;

    BuyOrTrade(int landId, Property property) {
        super("Buy Or Trade", Action.GIFT, Type.CHANCE, HIGH_PRIORITY);
        this.landId = landId;
        this.property = property;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        assert turn.getLand(landId) == property;
        LOG.info("{} moving to {} ({})", turn.getPlayer().getName(), landId, turn.getLand(landId).getName());
        List<Land> path = turn.moveTo(landId);
        if (path.size() == 0) {
            LOG.warn("Staying on the same land");
        } else {
            path.stream().filter(land -> land.getType() == Land.Type.START).findFirst().ifPresent(land -> {
                LOG.info("Player {} has to get income due to start", turn.getPlayer().getName());
                try {
                    turn.addMoney(((Start) land).getIncomeTax());
                } catch (BankException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        // there is no need to roll dice or move if player did this action
        turn.playerStartedTurn();
        return ImmutableList.of(new Trade(landId, property), new EndTurn());
    }
}

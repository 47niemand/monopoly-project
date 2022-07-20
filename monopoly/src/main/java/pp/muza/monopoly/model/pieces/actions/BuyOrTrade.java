package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Turn;

/**
 * This card allows a player to buy property from the game.
 * On next turn, go forward to any free space and buy it, if all are owned, buy one from any player.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class BuyOrTrade extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(BuyOrTrade.class);

    private final int landId;

    BuyOrTrade(int landId) {
        super("Buy or Trade", Action.GIFT, Type.CHOOSE, HIGHEST_PRIORITY);
        this.landId = landId;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        LOG.info("{}: moving to {} ({})", turn.getPlayer().getName(), landId, turn.getLand(landId).getName());
        List<Land> path = turn.moveTo(landId);
        if (path.size() == 0) {
            LOG.warn("{}: Staying on the same land", turn.getPlayer().getName());
        }
        // there is no need to roll dice or move if a player did this action
        turn.playerTurnStarted();
        return ImmutableList.<ActionCard>builder()
                .addAll(CardUtils.onPath(turn, path))
                .add(new Trade(landId))
                .add(new EndTurn())
                .build();
    }
}

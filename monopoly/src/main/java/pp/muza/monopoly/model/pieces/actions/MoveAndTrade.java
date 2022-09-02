package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Turn;

/**
 * This card allows a player to buy property from the game.
 * On next turn, go forward to any free space and buy it, if all are owned, buy one from any player.
 */
public final class MoveAndTrade extends MoveTo {


    MoveAndTrade(int landId) {
        super("Move and Trade", ActionType.CHOOSE, HIGHEST_PRIORITY, landId);
    }

    @Override
    protected List<ActionCard> onArrival(Turn turn) {
        // there is no need to roll dice or move if a player did this action
        turn.playerTurnStarted();
        return ImmutableList.<ActionCard>builder()
                .add(new Buy(landId))
                .add(new EndTurn())
                .build();
    }
}

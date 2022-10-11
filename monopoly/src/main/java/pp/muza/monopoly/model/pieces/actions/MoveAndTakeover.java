package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

/**
 * This card allows a player to buy property from the game.
 * On next turn, go forward to any free space and buy it, if all are owned, buy one from any player.
 *
 * @author dmytromuza
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class MoveAndTakeover extends MoveTo {

    MoveAndTakeover(int position) {
        super(ActionType.CHOOSE, HIGHEST_PRIORITY, position);
    }

    public static ActionCard of(int position) {
        return new MoveAndTakeover(position);
    }

    @Override
    protected List<ActionCard> onArrival(Turn turn) {
        return ImmutableList.of(new Takeover(position), new EndTurn());
    }
}

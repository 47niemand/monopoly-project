package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Turn;

/**
 * This card starts the new turn.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class NewTurn extends BaseActionCard {

    NewTurn() {
        super(Action.NEW_TURN, ActionType.OBLIGATION, NEW_TURN_PRIORITY);
    }

    public static ActionCard create() {
        return new NewTurn();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        PlayerStatus status = turn.getPlayerStatus();
        List<ActionCard> result;
        switch (status) {
            case IN_GAME:
                result = ImmutableList.of(new RollDice(), new EndTurn());
                break;
            case IN_JAIL:
                result = ImmutableList.of(new JailFine(turn.getJailFine()), new EndTurn());
                break;
            default:
                assert false : "Unexpected status: " + status;
                result = ImmutableList.of(new EndTurn());
                break;
        }
        return result;
    }
}

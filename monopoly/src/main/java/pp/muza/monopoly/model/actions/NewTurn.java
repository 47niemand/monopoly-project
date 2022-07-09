package pp.muza.monopoly.model.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.game.PlayerStatus;
import pp.muza.monopoly.model.game.Turn;

/**
 * This card starts the new turn.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class NewTurn extends BaseActionCard {

    NewTurn() {
        super("New Turn", Action.NEW_TURN, Type.OBLIGATION, NEW_TURN_PRIORITY);
    }

    public static NewTurn of() {
        return new NewTurn();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        PlayerStatus status = turn.getStatus();
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

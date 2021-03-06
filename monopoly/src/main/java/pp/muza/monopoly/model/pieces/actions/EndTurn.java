package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Turn;


/**
 * This card finishes the turn.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class EndTurn extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(EndTurn.class);

    EndTurn() {
        super("End Turn", Action.END_TURN, Type.OBLIGATION, LOW_PRIORITY);
    }

    public static ActionCard of() {
        return new EndTurn();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.endTurn();
        } catch (TurnException e) {
            throw new IllegalStateException(e);
        }
        return ImmutableList.of();
    }
}

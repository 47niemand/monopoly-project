package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import com.google.common.collect.ImmutableList;

import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

public final class BirthdayParty extends BaseActionCard {

    public BirthdayParty() {
        super("Birthday Party", Action.PARTY, ActionType.OBLIGATION, DEFAULT_PRIORITY);
    }

    public static ActionCard of() {
        return new BirthdayParty();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            turn.doBirthdayParty();
        } catch (TurnException e) {
            throw new RuntimeException(e);
        }
        return ImmutableList.of();
    }
}


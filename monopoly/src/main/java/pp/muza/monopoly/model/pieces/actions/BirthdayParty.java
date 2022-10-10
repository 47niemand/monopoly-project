package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Turn;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class BirthdayParty extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(BirthdayParty.class);

    BirthdayParty() {
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
            LOG.error("Error during executing the action: {}", this, e);
            throw new RuntimeException(e);
        }
        return ImmutableList.of();
    }
}


package pp.muza.monopoly.model.pieces.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.consts.Constants;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.errors.UnexpectedErrorException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * Everyone gives you a present.
 *
 * @author dmytromuza
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class BirthdayParty extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(BirthdayParty.class);

    BirthdayParty() {
        super(Action.PARTY, ActionType.OBLIGATION, DEFAULT_PRIORITY);
    }

    public static ActionCard create() {
        return new BirthdayParty();
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        try {
            doBirthdayParty(turn);
        } catch (TurnException e) {
            throw new UnexpectedErrorException("Error during executing the action: " + this, e);
        }
        return ImmutableList.of();
    }

    private void doBirthdayParty(Turn turn) throws TurnException {
        Player player = turn.getPlayer();
        LOG.info("Birthday party for {}", player);
        turn.holdTurn();
        for (Player guest : turn.getPlayers()) {
            if (guest != player && !turn.getPlayerStatus(guest).isFinal()) {
                try {
                    turn.sendCard(guest, new Gift(Constants.BIRTHDAY_GIFT_AMOUNT, player));
                    turn.sendCard(guest, new EndTurn());
                } catch (TurnException e) {
                    throw new UnexpectedErrorException("Error sending birthday invitation to " + player, e);
                }
            }
        }
    }
}


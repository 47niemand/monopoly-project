package pp.muza.monopoly.model.actions.cards;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.PlayerStatus;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.game.TurnException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static pp.muza.monopoly.model.actions.cards.PayRent.createContractsForPlayerPossession;

/**
 * The player has to pay money to the bank.
 * <p>
 * if the player is in jail, successfully pay the bill will allow to end the turn.
 * </p>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class JailFine extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(JailFine.class);

    private final BigDecimal amount;

    JailFine(BigDecimal amount) {
        super("JailFine", Action.TAX, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.amount = amount;
    }

    public static JailFine of(BigDecimal amount) {
        return new JailFine(amount);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            if (turn.getStatus() == PlayerStatus.IN_JAIL) {
                turn.payTax(amount);
                turn.leaveJail();
            } else {
                LOG.warn("Player is not in jail, cancel jail fine");
            }
            result = ImmutableList.of(EndTurn.of());
        } catch (BankException e) {
            LOG.info("Player cannot pay money: {}", e.getMessage());
            result = new ArrayList<>();
            result.add(this);
            result.addAll(createContractsForPlayerPossession(turn));
        } catch (TurnException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
}

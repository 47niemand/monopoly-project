package pp.muza.monopoly.model.pieces.actions;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.errors.BankException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Turn;

/**
 * A player has to pay money to other player.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PayGift extends BaseActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(PayGift.class);

    private final Player recipient;
    private final BigDecimal amount;

    PayGift(Player recipient, BigDecimal amount) {
        super("Pay Gift", Action.PAY, Type.OBLIGATION, DEFAULT_PRIORITY);
        this.recipient = recipient;
        this.amount = amount;
    }

    public static ActionCard of(Player player, BigDecimal valueOf) {
        return new PayGift(player, valueOf);
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result;
        try {
            turn.withdraw(amount);
            turn.sendCard(recipient, new Gift(amount, turn.getPlayer()));
            result = ImmutableList.of();
        } catch (BankException e) {
            LOG.info("Player cannot pay money: {}", e.getMessage());
            result = ImmutableList.<ActionCard>builder().add(this).addAll(CardUtils.createContractsForPlayerPossession(turn)).build();
        }
        return result;
    }


}

package pp.muza.monopoly.model.actions.strategy;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.lands.Land;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.lands.Property;

/**
 * The player has to pay money to the property owner on which player is standing.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PayRent extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(PayRent.class);

    private final Player recipient;
    private final BigDecimal amount;

    PayRent(Player recipient, Land land) {
        super("Pay Rent", ActionType.PAY_RENT, 10, true);
        this.recipient = recipient;
        if (land.getType() == Land.Type.PROPERTY) {
            this.amount = ((Property) land).getRent();
        } else {
            this.amount = BigDecimal.valueOf(0);
        }
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        Game game = turn.getGame();
        Player player = turn.getPlayer();
        try {
            game.payRent(player, recipient, amount);
        } catch (BankException e) {
            // try to create a contract for each property in the player's possession
            boolean contractCreated = ContractHelper.createContractsForPlayersPossession(turn);
            throw new ActionCardException(e, this, !contractCreated);
        }
    }
}

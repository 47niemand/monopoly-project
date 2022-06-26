package pp.muza.monopoly.model.actions.strategy;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.BankException;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.player.Player;

/**
 * The player receives money from this card.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Income extends ActionCard {
    private final BigDecimal amount;

    Income(BigDecimal amount) {
        super("Income", ActionType.INCOME, 0, true);
        this.amount = amount;
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        Player player = turn.getPlayer();
        Game game = turn.getGame();
        try {
            game.addMoney(player, amount);
        } catch (BankException e) {
            throw new ActionCardException(e, this);
        }
    }
}

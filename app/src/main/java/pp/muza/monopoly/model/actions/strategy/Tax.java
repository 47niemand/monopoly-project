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
 * The player has to pay money to the bank.
 * <p>
 * if the player is in jail, successfully pay the bill will allow to end the turn.
 * </p>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Tax extends ActionCard {
    private final BigDecimal amount;

    Tax(BigDecimal amount) {
        super("Tax", ActionType.TAX, 0, true);
        this.amount = amount;
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        Player player = turn.getPlayer();
        Game game = turn.getGame();
        try {
            game.payTax(player, amount);
        } catch (BankException e) {
            boolean contractCreated = !ContractHelper.createContractsForPlayersPossession(turn);
            throw new ActionCardException(e, this, contractCreated);
        }
        if (game.getPlayerStatus(player) == Game.PlayerStatus.IN_JAIL) {
            game.setPlayerStatus(player, Game.PlayerStatus.IN_GAME);
        }
    }
}

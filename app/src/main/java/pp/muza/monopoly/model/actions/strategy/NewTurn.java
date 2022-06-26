package pp.muza.monopoly.model.actions.strategy;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.player.Player;
import pp.muza.monopoly.model.lands.Jail;

/**
 * This card starts the new turn.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class NewTurn extends ActionCard {

    NewTurn() {
        super("New Turn", ActionType.NEW_TURN, 0, true);
    }

    public static NewTurn of() {
        return new NewTurn();
    }

    @Override
    protected void onExecute(Turn turn) {
        Game game = turn.getGame();
        Player player = turn.getPlayer();
        int pos = game.getPlayerPos(player);
        Game.PlayerStatus playerStatus = game.getPlayerStatus(player);

        //add EndTurn card to player's hand
        turn.addActionCard(new EndTurn());

        switch (playerStatus) {
            case IN_JAIL:
                // If player is in jail, he/she can't move
                Jail jail = (Jail) game.getBoard().getLand(pos);
                // If player is in jail and has a get out by paying fine
                turn.addActionCard(new Tax(jail.getFine()));
                break;
            case IN_GAME:
                // If player is in game, he/she can move
                turn.addActionCard(new RollDice());
                break;
        }
    }
}

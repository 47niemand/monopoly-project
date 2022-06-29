package pp.muza.monopoly.model.actions.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.turn.Turn;

/**
 * This card starts the new turn.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class NewTurn extends ActionCard {

    NewTurn() {
        super("New Turn", Action.NEW_TURN, Type.OBLIGATION, NEW_TURN_PRIORITY);
    }

    public static NewTurn of() {
        return new NewTurn();
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        Game.PlayerStatus playerStatus = turn.getStatus();

        // add EndTurn card to player's hand
        turn.addActionCard(new EndTurn());

        switch (playerStatus) {
            case IN_JAIL:
                // If player is in jail and has a get out by paying fine
                turn.addActionCard(new Tax(turn.getJailFine()));
                break;
            case IN_GAME:
                // If player is in game, he/she can move
                turn.addActionCard(new RollDice());
                break;
            default:
                // it seems that we don't need to do anything here
                break;
        }
    }
}

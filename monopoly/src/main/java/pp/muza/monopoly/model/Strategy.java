package pp.muza.monopoly.model;

import java.util.List;

import pp.muza.monopoly.data.TurnInfo;

/**
 * @author dmytromuza
 */
public interface Strategy {

    /**
     * Returns the card to be played.
     * Implement this method to determine what action to execute for the player.
     *
     * @param board    the board
     * @param players  players listed in the game
     * @param turnInfo the turn info.
     * @return the card to be played, or null if no card should be played.
     */
    ActionCard playTurn(Board board, List<Player> players, TurnInfo turnInfo);

}

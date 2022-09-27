package pp.muza.monopoly.model;

import pp.muza.monopoly.data.TurnInfo;

import java.util.List;

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

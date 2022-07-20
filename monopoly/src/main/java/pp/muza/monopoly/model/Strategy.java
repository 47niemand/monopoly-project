package pp.muza.monopoly.model;

import pp.muza.monopoly.data.TurnInfo;

public interface Strategy {

    /**
     * Returns the card to be played.
     * Implement this method to determine what action to execute for the player.
     *
     * @param turnInfo the turn info.
     * @return the card to be played, or null if no card should be played.
     */
    ActionCard playTurn(TurnInfo turnInfo);

}

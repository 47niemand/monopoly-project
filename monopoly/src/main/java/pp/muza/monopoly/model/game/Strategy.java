package pp.muza.monopoly.model.game;

public interface Strategy {

    /**
     * implement this method to determine what action to execute for the player.
     *
     * @param currentTurn the current turn.
     */
    void playTurn(TurnPlayer currentTurn);
}

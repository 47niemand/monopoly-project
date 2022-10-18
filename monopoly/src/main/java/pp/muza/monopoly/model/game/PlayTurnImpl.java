package pp.muza.monopoly.model.game;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.PlayTurn;
import pp.muza.monopoly.model.Player;

import java.util.List;

public class PlayTurnImpl implements PlayTurn {

    private final BaseTurn turn;

    public PlayTurnImpl(BaseTurn turn) {
        this.turn = turn;
    }

    @Override
    public Player getPlayer() {
        return turn.getPlayer();
    }

    @Override
    public void playCard(ActionCard actionCard) throws TurnException {
        turn.playCard(actionCard);
    }

    @Override
    public boolean isFinished() {
        return turn.isFinished();
    }

    @Override
    public TurnInfo getTurnInfo() {
        return turn.getTurnInfo();
    }

    @Override
    public List<Player> getPlayers() {
        return turn.getPlayers();
    }

    @Override
    public void endTurn() throws TurnException {
        turn.endTurn();
    }
}

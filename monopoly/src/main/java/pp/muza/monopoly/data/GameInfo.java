package pp.muza.monopoly.data;

import java.util.List;

import lombok.Builder;
import lombok.Value;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;

/**
 * This class represents the state of the game.
 */
@Value
@Builder
public class GameInfo {
    List<Player> players;
    List<PlayerInfo> playerInfo;
    Board board;
    List<Fortune> fortunes;
    int currentPlayerIndex;
    int turnNumber;
    int maxTurns;


    @Override
    public String toString() {
        return "GameInfo(players=" + this.getPlayers()
                + ", playerInfo=" + this.getPlayerInfo()
                + ", board=" + this.getBoard()
                + ", fortunes=" + this.getFortunes()
                + ", currentPlayerIndex=" + this.getCurrentPlayerIndex()
                + ", turnNumber=" + this.getTurnNumber()
                + ", maxTurns=" + this.getMaxTurns() + ")";
    }
}

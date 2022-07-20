package pp.muza.monopoly.data;

import java.util.List;

import lombok.Value;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;

/**
 * This class represents the state of the game.
 *
 */
@Value
public class GameInfo {
    List<Player> players;
    List<PlayerInfo> playerInfo;
    Board board;
    List<Fortune> fortunes;
    int currentPlayerIndex;
    int turnNumber;
    int maxTurns;
}

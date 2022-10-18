package pp.muza.monopoly.data;

import java.util.List;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;

/**
 * This class represents the state of the game.
 *
 * @author dmytromuza
 */
@Value
@Builder
@ToString
public class GameInfo {
    List<Player> players;
    List<PlayerInfo> playerInfos;
    Board board;
    List<Fortune> fortunes;
    int currentPlayerIndex;
    int turnNumber;
    int maxTurns;
}

package pp.muza.monopoly.data;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import pp.muza.monopoly.consts.RuleOption;
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
    List<PlayerInfo> playerInfo;
    List<Map.Entry<RuleOption, String>> rules;
    Board board;
    List<Fortune> fortunes;
    int currentPlayerIndex;
    int turnNumber;
    int maxTurns;
}

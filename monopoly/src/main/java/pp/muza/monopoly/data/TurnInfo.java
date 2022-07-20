package pp.muza.monopoly.data;

import java.util.List;
import java.util.Map;

import lombok.Value;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Player;

@Value
public class TurnInfo {

    int turn;
    /**
     * Action cards that the player can play.
     */
    List<ActionCard> activeCards;
    /**
     * The player who is playing the turn.
     */
    PlayerInfo playerInfo;
    /**
     * The board of the game.
     */
    Board board;
    /**
     * Active players in the game.
     */
    List<Player> players;
    /**
     * Belongings of the players.
     */
    Map<Integer, Player> propertyOwners;
}

package pp.muza.monopoly.data;

import java.util.List;
import java.util.Map;

import lombok.Value;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Player;

/**
 * The information about the turn is stored in this class.
 * The player should use this to make a turn-related decision.
 * <p>
 * The player can currently choose from the active action cards {@link TurnInfo#activeCards} that are shown to them.
 * Player can see their own data {@link TurnInfo#playerInfo}. Players may see who is currently playing {@link TurnInfo#players}.
 * The player may view the board {@link TurnInfo#board}, properties and property's owners {@link TurnInfo#propertyOwners}.
 * </p>
 */

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

package pp.muza.monopoly.data;

import lombok.Builder;
import lombok.Value;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the player's information in the game.
 *
 * @author dmytromuza
 */
@Value
@Builder(toBuilder = true)
public class PlayerInfo {
    Player player;
    int position;
    PlayerStatus status;
    int coins;
    @Builder.Default
    List<ActionCard> actionCards = null;
    @Builder.Default
    List<Integer> belongings = null;

    @SuppressWarnings("all")
    @Override
    public String toString() {
        return "Player: " + player
                + " at position: " + position
                + ", with coins: " + coins
                + ", with status: " + status
                + (actionCards != null ? ", with action cards: " + actionCards.stream().map(ActionCard::getName).collect(Collectors.toList()) : "")
                + (belongings != null ? " and belongings: " + belongings.stream().map(x -> x).collect(Collectors.toList()) : "");
    }
}

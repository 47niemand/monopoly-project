package pp.muza.monopoly.data;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;
import pp.muza.monopoly.entry.IndexedEntry;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Property;

/**
 * This class represents the player's information in the game.
 */
@Value
public
class PlayerInfo {
    Player player;
    int position;
    PlayerStatus status;
    Integer coins;
    List<ActionCard> actionCards;
    List<IndexedEntry<Property>> belongings;

    @Override
    public String toString() {
        return "Player: " + player.getName()
                + " at position: " + position
                + ", with status: " + status
                + ", with coins: " + coins
                + ", with action cards: " + actionCards.stream().map(ActionCard::getName).collect(Collectors.toList())
                + " and belongings: " + belongings.stream().map(x -> x.getValue().getName()).collect(Collectors.toList());
    }
}

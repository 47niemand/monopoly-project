package pp.muza.monopoly.data;

import java.math.BigDecimal;
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
    BigDecimal money;
    List<ActionCard> actionCards;
    List<IndexedEntry<Property>> belongings;

    @Override
    public String toString() {
        return "PlayerInfo(player=" + this.getPlayer().getName()
                + ", position=" + this.getPosition()
                + ", status=" + this.getStatus()
                + ", money=" + this.getMoney()
                + ", actionCards=" + this.getActionCards().stream().map(ActionCard::getName).collect(Collectors.toList())
                + ", belongings=" + this.getBelongings().stream().map(x -> x.getValue().getName()).collect(Collectors.toList())
                + ")";
    }


}

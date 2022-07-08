package pp.muza.monopoly.model.game;

import lombok.Value;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.lands.Property;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Value
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

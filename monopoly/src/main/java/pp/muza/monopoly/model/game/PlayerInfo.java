package pp.muza.monopoly.model.game;

import lombok.Value;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.player.Player;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Value
final class PlayerInfo {
    private final Player player;
    private final int position;
    private final PlayerStatus status;
    private final BigDecimal money;
    private final List<ActionCard> actionCards;
    private final List<Property> belongings;

    public String toString() {
        return "PlayerInfo(player=" + this.getPlayer().getName()
                + ", position=" + this.getPosition()
                + ", status=" + this.getStatus()
                + ", money=" + this.getMoney()
                + ", actionCards=[" + this.getActionCards().stream().map(ActionCard::getName).collect(Collectors.joining(",")) + "]"
                + ", belongings=[" + this.getBelongings().stream().map(Property::getName).collect(Collectors.joining(",")) + "]"
                + ")";
    }
}

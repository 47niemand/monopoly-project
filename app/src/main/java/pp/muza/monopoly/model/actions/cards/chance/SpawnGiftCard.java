package pp.muza.monopoly.model.actions.cards.chance;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.turn.Turn;
import pp.muza.monopoly.model.lands.Property;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class SpawnGiftCard extends ActionCard {

    private final int landId;
    private final Property property;

     SpawnGiftCard(int landId, Property property) {
        super("SpawnGiftCard", Action.GIFT, Type.CHANCE, DEFAULT_PRIORITY);
         this.landId = landId;
         this.property = property;
     }

    @Override
    protected void onExecute(Turn turn) {
        turn.ownProperty(landId, property);
    }
}

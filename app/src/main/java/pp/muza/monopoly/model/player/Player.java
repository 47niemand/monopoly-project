package pp.muza.monopoly.model.player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.game.Turn;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class Player {

    private final String name;

    // simple strategy to execute all action cards in order
    public abstract void playTurn(Turn currentTurn);

}

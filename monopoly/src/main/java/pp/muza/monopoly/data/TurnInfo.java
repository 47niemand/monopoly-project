package pp.muza.monopoly.data;

import lombok.Builder;
import lombok.Value;
import pp.muza.monopoly.model.ActionCard;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The information about the turn is stored in this class.
 * The player should use this to make a turn-related decision.
 * <p>
 * The player can currently choose from the active action cards {@link TurnInfo#activeCards} that are shown to them.
 * Player can see their own data {@link TurnInfo#playerInfo}.
 * </p>
 *
 * @author dmytromuza
 */

@Value
@Builder(toBuilder = true)
public class TurnInfo {

    int turnNumber;
    PlayerInfo playerInfo;
    List<ActionCard> activeCards;
    boolean isFinished;
    @Builder.Default
    List<ActionCard> usedCards = null;

    @SuppressWarnings("all")
    @Override
    public String toString() {
        return "TurnInfo{" +
                "turnNumber=" + turnNumber +
                ", playerInfo=" + playerInfo +
                ", activeCards=" + activeCards.stream().map(ActionCard::getName).collect(Collectors.toList()) +
                (usedCards != null ? ", usedCards=" + usedCards.stream().map(ActionCard::getName).collect(Collectors.toList()) : "") +
                ", isFinished=" + isFinished +
                '}';
    }
}

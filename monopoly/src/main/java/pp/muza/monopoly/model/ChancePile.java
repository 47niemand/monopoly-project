package pp.muza.monopoly.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pp.muza.monopoly.model.pieces.actions.Chance;
import pp.muza.monopoly.model.pieces.actions.FortuneCard;

/**
 * The default chance pile.
 *
 * @author dmytromuza
 */
public final class ChancePile {

    /**
     * The default list of chance cards.
     *
     * @return the list of chance cards.
     */
    public static List<Fortune> defaultPile() {
        return Arrays
                .stream(Chance.values())
                .map(FortuneCard::create)
                .collect(Collectors.toList());
    }

}

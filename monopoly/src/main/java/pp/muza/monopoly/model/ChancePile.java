package pp.muza.monopoly.model;

import pp.muza.monopoly.model.pieces.actions.Chance;
import pp.muza.monopoly.model.pieces.actions.FortuneCard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ChancePile {

    public static List<Fortune> defaultPile() {
        return Arrays
                .stream(Chance.values())
                .map(FortuneCard::of)
                .collect(Collectors.toList());
    }

}

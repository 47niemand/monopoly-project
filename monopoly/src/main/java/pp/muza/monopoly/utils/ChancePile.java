package pp.muza.monopoly.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.pieces.actions.FortuneCard;

public class ChancePile {

    public static List<Fortune> defaultPile() {
        return Arrays
                .stream(Fortune.Chance.values())
                .map(FortuneCard::of)
                .collect(Collectors.toList());
    }

}

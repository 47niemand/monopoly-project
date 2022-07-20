package pp.muza.monopoly.strategy;

import java.util.List;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Strategy;

public class ObedientStrategy implements Strategy {

    public static Strategy STRATEGY = new ObedientStrategy();

    @Override
    public ActionCard playTurn(TurnInfo turnInfo) {
        List<ActionCard> a = turnInfo.getActiveCards();
        // it always returns the first card
        return a.size() > 0 ? a.get(0) : null;
    }
}

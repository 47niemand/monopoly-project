package pp.muza.monopoly.strategy;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Strategy;

public class DefaultStrategy implements Strategy {

    public static final Strategy STRATEGY = new DefaultStrategy();

    private static final Logger LOG = LoggerFactory.getLogger(DefaultStrategy.class);

    @Override
    public ActionCard playTurn(TurnInfo turnInfo) {
        List<ActionCard> a = turnInfo.getActiveCards();
        // TODO: implement a better strategy
        //  if there are CONTRACT cards, chose which a better to sale (to cover the obligation, or get more profit);
        //  If there is a BUY card, decide whether to buy it or not if a better option could be available;
        //  if there are BuyOrTrade cards, chose card which fits player balance;
        //  if there are GetOrPay cards, It is preferable to select an unowned land, but consider the player's balance;
        //  if there are OptionMove cards, chose the one which is better to the player's current situation;

        int random = (int) (Math.random() * a.size());
        LOG.debug("Random card: {}", random);
        return a.size() > 0 ? a.get(random) : null;
    }
}

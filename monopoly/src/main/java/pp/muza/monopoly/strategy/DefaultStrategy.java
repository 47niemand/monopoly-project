package pp.muza.monopoly.strategy;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.muza.monopoly.data.TurnInfo;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.Strategy;

public class DefaultStrategy implements Strategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultStrategy.class);
    private static final DefaultStrategy STRATEGY = new DefaultStrategy();

    public static Strategy getInstance() {
        return STRATEGY;
    }

    @Override
    public ActionCard playTurn(Board board, List<Player> players, TurnInfo turnInfo) {
        List<ActionCard> cards = turnInfo.getActiveCards();
        LOG.info("Active cards: {}", cards.stream().map(ActionCard::getName).collect(Collectors.toList()));
        // TODO: implement a better strategy
        //  if there are CONTRACT cards, chose which a better to sale (to cover the obligation, or get more profit);
        //  If there is a BUY card, decide whether to buy it or not if a better option could be available;
        //  if there are MoveAndTakeover cards, chose card which fits player balance;
        //  if there are GetOrPay cards, It is preferable to select an unowned land, but consider the player's balance;
        //  if there are OptionMove cards, chose the one which is better to the player's current situation;

        int random = (int) (Math.random() * cards.size());
        LOG.debug("Random card: {}", random);
        return cards.size() > 0 ? cards.get(random) : null;
    }
}

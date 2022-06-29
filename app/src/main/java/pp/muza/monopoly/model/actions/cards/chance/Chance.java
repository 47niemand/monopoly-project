package pp.muza.monopoly.model.actions.cards.chance;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp.muza.monopoly.model.actions.ActionCard;
import pp.muza.monopoly.model.actions.ActionCardException;
import pp.muza.monopoly.model.actions.cards.Income;
import pp.muza.monopoly.model.actions.cards.Move;
import pp.muza.monopoly.model.actions.cards.MoveTo;
import pp.muza.monopoly.model.actions.cards.Tax;
import pp.muza.monopoly.model.game.Game;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.lands.PropertyName;
import pp.muza.monopoly.model.turn.Turn;

import java.math.BigDecimal;
import java.util.List;

/**
 * This is a special card that stores the chance pile of the game. it should be returned to the game when the card is used.
 * <p>
 * This class is a part of the "Chance" package.
 * </p>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Chance extends ActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Chance.class);
    private static final int PLAYER_1 = 0;
    private static final int PLAYER_2 = 1;
    private static final int PLAYER_3 = 2;
    private static final int PLAYER_4 = 3;
    private final ChanceCard card;

    Chance(ChanceCard card) {
        super("Chance"
                , Action.CHANCE
                , card.isGiftCard() ? Type.KEEPABLE : Type.OBLIGATION
                , card.isGiftCard() ? HIGH_PRIORITY : DEFAULT_PRIORITY
        );
        this.card = card;
    }

    public static Chance of(ChanceCard card) {
        return new Chance(card);
    }

    private boolean checkGiftCard(Turn turn) throws ActionCardException {
        switch (card) {
            case GIVE_THIS_CARD_TO_A_PLAYER_1:
                if (turn.getPlayers().size() > PLAYER_1 && turn.isPlayerInGame(turn.getPlayers().get(PLAYER_1))) {
                    turn.sendGiftCard(turn.getPlayers().get(PLAYER_1), new GiftCard());
                } else {
                    LOG.warn("Player 1 is not in the game, get another chance card");
                    turn.addActionCard(turn.popChanceCard());
                }
                break;
            case GIVE_THIS_CARD_TO_A_PLAYER_2:
                if (turn.getPlayers().size() > PLAYER_2 && turn.isPlayerInGame(turn.getPlayers().get(PLAYER_2))) {
                    turn.sendGiftCard(turn.getPlayers().get(PLAYER_2), new GiftCard());
                } else {
                    LOG.warn("Player 2 is not in the game, get another chance card");
                    turn.addActionCard(turn.popChanceCard());
                }
                break;
            case GIVE_THIS_CARD_TO_A_PLAYER_3:
                if (turn.getPlayers().size() > PLAYER_3 && turn.isPlayerInGame(turn.getPlayers().get(PLAYER_3))) {
                    turn.sendGiftCard(turn.getPlayers().get(PLAYER_3), new GiftCard());
                } else {
                    LOG.warn("Player 3 is not in the game, get another chance card");
                    turn.addActionCard(turn.popChanceCard());
                }
                break;
            case GIVE_THIS_CARD_TO_A_PLAYER_4:
                if (turn.getPlayers().size() > PLAYER_4 && turn.isPlayerInGame(turn.getPlayers().get(PLAYER_4))) {
                    turn.sendGiftCard(turn.getPlayers().get(PLAYER_4), new GiftCard());
                } else {
                    LOG.warn("Player 4 is not in the game, get another chance card");
                    turn.addActionCard(turn.popChanceCard());
                }
                break;
            default:
                return false;
        }
        return true;
    }


    private boolean checkAdvanceToChance(Turn turn) {
        switch (card) {
            case ADVANCE_TO_MAYFAIR:
                spawnGetOrPayByName(turn, PropertyName.MAYFAIR.getName());
                break;
            case ADVANCE_TO_YELLOW_OR_RAINBOW:
                spawnGetOrPayByColor(turn, Property.Color.YELLOW);
                spawnGetOrPayByColor(turn, Property.Color.RAINBOW);
                break;
            case ADVANCE_TO_GREEN_OR_VIOLET:
                spawnGetOrPayByColor(turn, Property.Color.GREEN);
                spawnGetOrPayByColor(turn, Property.Color.VIOLET);
                break;
            case ADVANCE_TO_BLUE_OR_ORANGE:
                spawnGetOrPayByColor(turn, Property.Color.BLUE);
                spawnGetOrPayByColor(turn, Property.Color.ORANGE);
                break;
            case ADVANCE_TO_INDIGO_OR_RED:
                spawnGetOrPayByColor(turn, Property.Color.RED);
                spawnGetOrPayByColor(turn, Property.Color.INDIGO);
                break;
            case ADVANCE_TO_GO_KARTS:
                spawnGetOrPayByName(turn, PropertyName.GO_KARTS.getName());
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onExecute(Turn turn) throws ActionCardException {
        if (checkAdvanceToChance(turn)) {
            return;
        }
        if (checkGiftCard(turn)) {
            return;
        }
        // check the rest
        switch (card) {
            case INCOME:
                turn.addActionCard(Income.of(BigDecimal.valueOf(2)));
                break;
            case BIRTHDAY:
                //TODO: implement this.
                // proof of concept: create instance of Turn for rest players.
                // add an action to transfer the money to current player.
                // execute the action for rest players.
                break;
            case LUXURY_TAX:
                turn.addActionCard(Tax.of(BigDecimal.valueOf(2)));
                break;
            case ADVANCE_TO_GO:
                turn.addActionCard(MoveTo.of(turn.getStartPos()));
                break;
            case MOVE_FORWARD_ONE_SPACE:
                turn.addActionCard(new ChanceMove(1));
                turn.addActionCard(turn.popChanceCard());
                break;
            case MOVE_FORWARD_UP_TO_5_SPACES:
                turn.addActionCard(Move.of(5));
                break;
            case GET_OUT_OF_JAIL_FREE:
                if (turn.getStatus() == Game.PlayerStatus.IN_JAIL) {
                    turn.leaveJail();
                }
                break;
            default:
                throw new ActionCardException("Unknown chance card: " + card, this);
        }
    }

    private void spawnGetOrPayByName(Turn turn, String name) {
        int landId = turn.foundLandByName(name);
        turn.addActionCard(new GetOrPay(landId));
    }

    private void spawnGetOrPayByColor(Turn turn, Property.Color color) {
        List<Integer> list = turn.foundLandsByColor(color);
        for (Integer landId : list) {
            turn.addActionCard(new GetOrPay(landId));
        }
    }
}
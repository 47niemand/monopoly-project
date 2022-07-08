package pp.muza.monopoly.model.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.game.PlayerStatus;
import pp.muza.monopoly.model.game.Turn;
import pp.muza.monopoly.model.game.TurnException;
import pp.muza.monopoly.model.lands.Property;
import pp.muza.monopoly.model.lands.PropertyName;
import pp.muza.monopoly.model.game.Player;

/**
 * This is a special card that stores the chance pile of the game. it should be returned to the game when the card is used.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Chance extends AbstractActionCard {

    private static final Logger LOG = LoggerFactory.getLogger(Chance.class);
    private static final int PLAYER_1 = 0;
    private static final int PLAYER_2 = 1;
    private static final int PLAYER_3 = 2;
    private static final int PLAYER_4 = 3;
    private final ChanceCard card;

    Chance(ChanceCard card) {
        super("Chance(" + card.name() + ")"
                , Action.CHANCE
                , card.isGiftCard() ? Type.KEEPABLE : Type.OBLIGATION
                , card.isGiftCard() ? HIGH_PRIORITY : DEFAULT_PRIORITY
        );
        this.card = card;
    }

    public static Chance of(ChanceCard card) {
        return new Chance(card);
    }

    private static List<ActionCard> sendGiftCard(Turn turn, int playerId) {
        List<ActionCard> result = new ArrayList<>();
        List<Player> players = turn.getPlayers();
        Player player;
        if (players.size() > playerId) {
            player = players.get(playerId);
        } else {
            player = null;
        }
        if (player == null) {
            LOG.warn("Player {} is not in the game, get another chance card", playerId);
            result.add(turn.popChanceCard());
        } else if (player == turn.getPlayer()) {
            LOG.warn("Player {} is trying to give a chance card to himself, get another chance card", playerId);
            result.add(turn.popChanceCard());
        } else if (turn.getPlayerStatus(player).isFinal()) {
            LOG.warn("Player {} is out of the game, get another chance card", playerId);
            result.add(turn.popChanceCard());
        } else {
            turn.sendCard(players.get(playerId), new SpawnGiftCard());
            result.add(turn.popChanceCard());
        }
        return result;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result = new ArrayList<>();
        switch (card) {
            case ADVANCE_TO_MAYFAIR:
                result.addAll(spawnGetOrPayByName(turn, PropertyName.MAYFAIR.getName()));
                break;
            case ADVANCE_TO_YELLOW_OR_RAINBOW:
                result.addAll(spawnGetOrPayByColor(turn, Property.Color.YELLOW));
                result.addAll(spawnGetOrPayByColor(turn, Property.Color.RAINBOW));
                break;
            case ADVANCE_TO_GREEN_OR_VIOLET:
                result.addAll(spawnGetOrPayByColor(turn, Property.Color.GREEN));
                result.addAll(spawnGetOrPayByColor(turn, Property.Color.VIOLET));
                break;
            case ADVANCE_TO_BLUE_OR_ORANGE:
                result.addAll(spawnGetOrPayByColor(turn, Property.Color.BLUE));
                result.addAll(spawnGetOrPayByColor(turn, Property.Color.ORANGE));
                break;
            case ADVANCE_TO_INDIGO_OR_RED:
                result.addAll(spawnGetOrPayByColor(turn, Property.Color.RED));
                result.addAll(spawnGetOrPayByColor(turn, Property.Color.INDIGO));
                break;
            case ADVANCE_TO_GO_KARTS:
                result.addAll(spawnGetOrPayByName(turn, PropertyName.GO_KARTS.getName()));
                break;
            case INCOME:
                result.add(new Income(BigDecimal.valueOf(2)));
                break;
            case BIRTHDAY:
                //TODO: improve this.
                // proof of concept: create instance of Turn for rest players.
                // add an action to transfer the money to current player.
                // execute the action for rest players.
                turn.birthdayParty();
                break;
            case LUXURY_TAX:
                result.add(new Tax(BigDecimal.valueOf(2)));
                break;
            case ADVANCE_TO_GO:
                result.add(new MoveTo(turn.getStartPos()));
                break;
            case MOVE_FORWARD_ONE_SPACE:
                result.add(new ChanceMove(1));
                result.add(new TakeChanceCard());
                break;
            case MOVE_FORWARD_UP_TO_5_SPACES:
                result.add(new Move(5));
                break;
            case GET_OUT_OF_JAIL_FREE:
                if (turn.getStatus() == PlayerStatus.IN_JAIL) {
                    try {
                        turn.leaveJail();
                    } catch (TurnException e) {
                        LOG.error("Error while leaving jail", e);
                    }
                } else {
                    LOG.warn("Player {} is not in jail", turn.getPlayer().getName());
                    result.add(this);
                }
                break;
            case GIVE_THIS_CARD_TO_A_PLAYER_1:
                result.addAll(sendGiftCard(turn, PLAYER_1));
                break;
            case GIVE_THIS_CARD_TO_A_PLAYER_2:
                result.addAll(sendGiftCard(turn, PLAYER_2));
                break;
            case GIVE_THIS_CARD_TO_A_PLAYER_3:
                result.addAll(sendGiftCard(turn, PLAYER_3));
                break;
            case GIVE_THIS_CARD_TO_A_PLAYER_4:
                result.addAll(sendGiftCard(turn, PLAYER_4));
                break;
            default:
                throw new IllegalStateException("Unknown chance card: " + card);
        }
        return result;
    }

    private List<ActionCard> spawnGetOrPayByName(Turn turn, String name) {
        int landId = turn.foundLandByName(name);
        return ImmutableList.of(new GetOrPay(landId));
    }

    private List<ActionCard> spawnGetOrPayByColor(Turn turn, Property.Color color) {
        List<ActionCard> result = new ArrayList<>();
        List<Integer> list = turn.foundLandsByColor(color);
        for (Integer landId : list) {
            result.add(new GetOrPay(landId));
        }
        return result;
    }
}
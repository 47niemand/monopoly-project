package pp.muza.monopoly.model.pieces.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.Property;

/**
 * This is a special card that stores the chance pile of the game. it should be
 * returned to the game when the card is used.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class FortuneCard extends BaseActionCard implements Fortune {

    private static final Logger LOG = LoggerFactory.getLogger(FortuneCard.class);
    private static final int PLAYER_1 = 0;
    private static final int PLAYER_2 = 1;
    private static final int PLAYER_3 = 2;
    private static final int PLAYER_4 = 3;

    private final Chance chance;

    FortuneCard(Chance chance) {
        super("Chance(" + chance.name() + ")", Action.CHANCE
                , chance.isGiftCard() ? Type.KEEPABLE : Type.OBLIGATION
                , chance.isGiftCard() ? HIGHEST_PRIORITY : DEFAULT_PRIORITY);
        this.chance = chance;
    }

    public static Fortune of(Chance chance) {
        return new FortuneCard(chance);
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
            result.add(turn.popFortuneCard());
        } else if (player == turn.getPlayer()) {
            LOG.warn("Player {} is trying to give a chance card to himself, get another chance card",
                    playerId);
            result.add(turn.popFortuneCard());
        } else if (turn.getPlayerStatus(player).isFinished()) {
            LOG.warn("Player {} is out of the game, get another chance card", playerId);
            result.add(turn.popFortuneCard());
        } else {
            turn.sendCard(players.get(playerId), new SpawnGiftCard());
            result.add(turn.popFortuneCard());
        }
        return result;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        List<ActionCard> result = new ArrayList<>();
        switch (chance) {
            case ADVANCE_TO_MAYFAIR:
                result.addAll(spawnGetOrPayByName(turn, Property.Asset.MAYFAIR));
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
                result.addAll(spawnGetOrPayByName(turn, Property.Asset.GO_KARTS));
                break;
            case INCOME:
                result.add(new Income(BigDecimal.valueOf(2)));
                break;
            case BIRTHDAY:
                turn.birthdayParty();
                break;
            case LUXURY_TAX:
                result.add(new Tax(BigDecimal.valueOf(2)));
                break;
            case ADVANCE_TO_GO:
                result.add(new MoveTo(turn.getStartPos()));
                break;
            case MOVE_FORWARD_ONE_SPACE:
                result.add(new OptionMove(1));
                result.add(new TakeFortuneCard());
                break;
            case MOVE_FORWARD_UP_TO_5_SPACES:
                result.add(new OptionMove(5));
                result.add(new OptionMove(4));
                result.add(new OptionMove(3));
                result.add(new OptionMove(2));
                result.add(new OptionMove(1));
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
                throw new IllegalStateException("Unknown chance card: " + chance);
        }
        return result;
    }

    private List<ActionCard> spawnGetOrPayByName(Turn turn, Property.Asset asset) {
        int landId = turn.foundProperty(asset);
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
package pp.muza.monopoly.model.pieces.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pp.muza.monopoly.consts.Meta;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.ActionType;
import pp.muza.monopoly.model.Asset;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.PropertyColor;
import pp.muza.monopoly.model.Turn;

/**
 * This is a specific card that stores the chance pile of the game.
 * It should be returned to the game when the card is used.
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
        super(Action.CHANCE.name(), Action.CHANCE
                , chance.isKeepable() ? ActionType.KEEPABLE : ActionType.OBLIGATION
                , chance.isKeepable() ? HIGHEST_PRIORITY : DEFAULT_PRIORITY);
        this.chance = chance;
    }

    public static Fortune of(Chance chance) {
        return new FortuneCard(chance);
    }

    private static List<ActionCard> sendGiftCard(Turn turn, int playerId) throws TurnException {
        List<ActionCard> result = new ArrayList<>();
        List<Player> players = turn.getPlayers();
        Player recipient;
        if (players.size() > playerId) {
            recipient = players.get(playerId);
        } else {
            recipient = null;
        }
        if (recipient == null) {
            LOG.warn("Player #{} is not in the game, get another chance card", 1 + playerId);
            result.add(turn.popFortuneCard());
        } else if (recipient == turn.getPlayer()) {
            LOG.warn("{} is trying to give a chance card to himself, get another chance card",
                    recipient.getName());
            result.add(turn.popFortuneCard());
        } else if (turn.getPlayerStatus(recipient).isFinal()) {
            LOG.warn("{} is out of the game, get another chance card", recipient.getName());
            result.add(turn.popFortuneCard());
        } else {
            turn.sendCard(recipient, new SpawnGiftCard());
            result.add(turn.popFortuneCard());
        }
        return result;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
        LOG.info("Chance: " + chance.name());
        LOG.debug("Executing card {} for player {}", this, turn.getPlayer().getName());
        List<ActionCard> result = new ArrayList<>();
        try {
            switch (chance) {
                case ADVANCE_TO_MAYFAIR:
                    result.addAll(spawnGetOrPayByName(turn, Asset.MAYFAIR));
                    break;
                case ADVANCE_TO_YELLOW_OR_RAINBOW:
                    result.addAll(spawnGetOrPayByColor(turn, PropertyColor.YELLOW));
                    result.addAll(spawnGetOrPayByColor(turn, PropertyColor.RAINBOW));
                    break;
                case ADVANCE_TO_GREEN_OR_VIOLET:
                    result.addAll(spawnGetOrPayByColor(turn, PropertyColor.GREEN));
                    result.addAll(spawnGetOrPayByColor(turn, PropertyColor.VIOLET));
                    break;
                case ADVANCE_TO_BLUE_OR_ORANGE:
                    result.addAll(spawnGetOrPayByColor(turn, PropertyColor.BLUE));
                    result.addAll(spawnGetOrPayByColor(turn, PropertyColor.ORANGE));
                    break;
                case ADVANCE_TO_INDIGO_OR_RED:
                    result.addAll(spawnGetOrPayByColor(turn, PropertyColor.RED));
                    result.addAll(spawnGetOrPayByColor(turn, PropertyColor.INDIGO));
                    break;
                case ADVANCE_TO_GO_KARTS:
                    result.addAll(spawnGetOrPayByName(turn, Asset.GO_KARTS));
                    break;
                case PRIZE:
                    result.add(Income.of(Meta.PRIZE_AMOUNT));
                    break;
                case BIRTHDAY:
                    result.add(BirthdayParty.of());
                    break;
                case LUXURY_TAX:
                    result.add(new Tax(Meta.LUXURY_TAX_AMOUNT));
                    break;
                case ADVANCE_TO_GO:
                    result.add(MoveTo.of(turn.getStartPos()));
                    break;
                case MOVE_FORWARD_ONE_SPACE:
                    result.add(new OptionMove(1));
                    result.add(new TakeFortuneCard());
                    break;
                case MOVE_FORWARD_UP_TO_5_SPACES:
                    IntStream.rangeClosed(1, 5).forEach(i -> result.add(new OptionMove(i)));
                    break;
                case GET_OUT_OF_JAIL_FREE:
                    if (turn.getPlayerStatus() == PlayerStatus.IN_JAIL) {
                        turn.leaveJail();
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
        } catch (TurnException e) {
            LOG.error("Error executing chance card {}", this, e);
            throw new IllegalStateException(e);
        }
        LOG.debug("Fortune: resulting cards: {}", result);
        return result;
    }

    private List<ActionCard> spawnGetOrPayByName(Turn turn, Asset asset) {
        int position = turn.foundProperty(asset);
        return ImmutableList.of(new OptionMoveTo(position));
    }

    private List<ActionCard> spawnGetOrPayByColor(Turn turn, PropertyColor color) {
        ImmutableList.Builder<ActionCard> builder = ImmutableList.builder();
        List<Integer> list = turn.foundLandsByColor(color);
        for (Integer position : list) {
            builder.add(new OptionMoveTo(position));
        }
        return builder.build();
    }
}
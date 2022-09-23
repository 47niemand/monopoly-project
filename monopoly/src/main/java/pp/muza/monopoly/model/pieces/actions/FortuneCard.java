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
import pp.muza.monopoly.errors.GameException;
import pp.muza.monopoly.errors.TurnException;
import pp.muza.monopoly.model.ActionCard;
import pp.muza.monopoly.model.Asset;
import pp.muza.monopoly.model.Fortune;
import pp.muza.monopoly.model.Player;
import pp.muza.monopoly.model.PlayerStatus;
import pp.muza.monopoly.model.Turn;
import pp.muza.monopoly.model.pieces.lands.PropertyColor;

/**
 * This is a specific card that stores the chance pile of the game.
 * It should be returned to the game when the card is used.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class FortuneCard extends BaseActionCard implements Fortune {

    public static final int PRIZE_AMOUNT = 2;
    public static final int LUXURY_TAX_AMOUNT = 2;
    private static final Logger LOG = LoggerFactory.getLogger(FortuneCard.class);
    private static final int PLAYER_1 = 0;
    private static final int PLAYER_2 = 1;
    private static final int PLAYER_3 = 2;
    private static final int PLAYER_4 = 3;
    private final Chance chance;
    private final String description;

    FortuneCard(Chance chance) {
        super(chance.getName(), Action.CHANCE
                , chance.isGiftCard() ? ActionType.KEEPABLE : ActionType.OBLIGATION
                , chance.isGiftCard() ? HIGHEST_PRIORITY : DEFAULT_PRIORITY);
        this.chance = chance;
        this.description = chance.getDescription();
    }

    public static Fortune of(Chance chance) {
        return new FortuneCard(chance);
    }

    @Override
    protected boolean canBeUsed(Turn turn) {
        if (chance == Chance.GET_OUT_OF_JAIL_FREE) {
            return turn.getPlayerStatus() == PlayerStatus.IN_JAIL;
        }
        return true;
    }

    private static List<ActionCard> sendGiftCard(Turn turn, int playerId) throws GameException {
        List<ActionCard> result = new ArrayList<>();
        List<Player> players = turn.getPlayers();
        Player player;
        if (players.size() > playerId) {
            player = players.get(playerId);
        } else {
            player = null;
        }
        if (player == null) {
            LOG.warn("Player #{} is not in the game, get another chance card", 1 + playerId);
            result.add(turn.popFortuneCard());
        } else if (player == turn.getPlayer()) {
            LOG.warn("{} is trying to give a chance card to himself, get another chance card",
                    player.getName());
            result.add(turn.popFortuneCard());
        } else if (turn.getPlayerStatus(player).isFinal()) {
            LOG.warn("{} is out of the game, get another chance card", player.getName());
            result.add(turn.popFortuneCard());
        } else {
            try {
                turn.sendCard(player, new SpawnGiftCard());
            } catch (TurnException e) {
                throw new RuntimeException(e);
            }
            result.add(turn.popFortuneCard());
        }
        return result;
    }

    @Override
    protected List<ActionCard> onExecute(Turn turn) {
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
                    result.add(Income.of(PRIZE_AMOUNT));
                    break;
                case BIRTHDAY:
                    turn.birthdayParty();
                    break;
                case LUXURY_TAX:
                    result.add(new Tax(LUXURY_TAX_AMOUNT));
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
                        try {
                            turn.leaveJail();
                        } catch (TurnException e) {
                            LOG.error("Error while leaving jail", e);
                            throw new GameException(e);
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
        } catch (GameException e) {
            throw new IllegalStateException("Error while executing chance card " + this, e);
        }
        LOG.debug("resulting cards: {}", result);
        return result;
    }

    private List<ActionCard> spawnGetOrPayByName(Turn turn, Asset asset) {
        int landId = turn.foundProperty(asset);
        return ImmutableList.of(new OptionMoveTo(landId));
    }

    private List<ActionCard> spawnGetOrPayByColor(Turn turn, PropertyColor color) {
        ImmutableList.Builder<ActionCard> builder = ImmutableList.builder();
        List<Integer> list = turn.foundLandsByColor(color);
        for (Integer landId : list) {
            builder.add(new OptionMoveTo(landId));
        }
        return builder.build();
    }
}
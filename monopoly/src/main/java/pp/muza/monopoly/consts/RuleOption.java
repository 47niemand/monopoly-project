package pp.muza.monopoly.consts;

/**
 * The game rule options.
 *
 * @author dmytromuza
 */
public enum RuleOption {

    /**
     * The player can choose to start an auction.
     */
    AUCTION,
    /**
     * The player must buy free properties he lands on.
     */
    BUY_OBLIGATORY,
    /**
     * The player can mortgage his property.
     * <p>
     * Note: this option is not implemented yet.
     */
    MORTGAGE,
    /**
     * The player can build houses on the property.
     * <p>
     * Note: this option is not implemented yet.
     */
    BUILD,

}

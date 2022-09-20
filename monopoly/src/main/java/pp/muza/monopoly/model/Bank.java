package pp.muza.monopoly.model;

import pp.muza.monopoly.errors.BankException;

/**
 * This interface represents a bank in the game.
 * There are methods to get the balance of a player, to deposit coins, to withdraw
 */
public interface Bank {

    /**
     * Returns the balance of a player.
     *
     * @param player the player.
     * @return the balance of the player. If the player is not in the bank, 0 is returned.
     */
    int getBalance(Player player);

    /**
     * Adds coins to the player's balance.
     *
     * @param player the player to add coins to
     * @param value  the value of coins to add
     * @throws BankException if the player wallet is full
     */
    void deposit(Player player, int value) throws BankException;

    /**
     * Subtracts the given number from the player's balance.
     *
     * @param player the player to subtract coins from
     * @param value  the number of coins to subtract
     * @throws BankException if the player doesn't have enough coins
     */
    void withdraw(Player player, int value) throws BankException;

    /**
     * Set the player's balance to the given value.
     *
     * @param player the player to set the balance for
     * @param value  the value to set the balance to
     */
    void set(Player player, int value);
}

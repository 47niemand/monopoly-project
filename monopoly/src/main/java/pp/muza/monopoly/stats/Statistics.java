package pp.muza.monopoly.stats;

import lombok.Value;

public class Statistics {

    @Value
    public static class Turn {
        int turn; // turn number
        int distance; // distance movement in the turn
        int player; // player number
        int owned; // number of owned properties at this turn
        int ownedPrice; // total price of owned properties at this turn
        int contracted; // number of contracted properties at this turn
        int contractedPrice; // total price of contracted properties at this turn
        int rentPayed; // total rent paid at this turn
        int rentObliged; // total rent owed at this turn
        int lost; // number of lost properties at this turn
        int lostPrice; // total price of lost properties at this turn
        int landsVisited; // number of lands visited at this turn
        int income; // total income at this turn
        int cardsPlayed; // number of cards played at this turn
        int chancePlayed; // number of chance cards played at this turn
        int totalCards; // total number of cards at this turn
    }

}

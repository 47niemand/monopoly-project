monopoly-project
================

The simple application which plays a game of Monopoly with a given number of players.
There is a board with a number of properties, each with a name, a price, and a color.
Specific lands are also on the board, such as the Go, Jail, and Chance spaces.

     |              |          BLUE         |        |         INDIGO           |            |
     | PARKING      | CINEMA    | THEATRE   | CHANCE | PET_SHOP   | TOY_SHOP    |GO_TO_JAIL  |
     | GO_KARTS     | GREEN  |                                        | VIOLET  |AQUARIUM    |
     | SWIMMING_POOL| GREEN  |                                        | VIOLET  |THE_ZOO     |
     | CHANCE       |                                                           | CHANCE     |
     | MUSEUM       | YELLOW |                                        | RAINBOW |PARK_LANE   |
     | LIBRARY      | YELLOW |                                        | RAINBOW |MAYFAIR     |
     |              |           ORANGE      |        |           RED            |            |
     | JAIL         |  BURGER_JOINT| BAKERY | CHANCE | DONUT_SHOP | COFFEE_SHOP | START      |
     |                                                                      <<--- Direction  |
     
On each turn, a player executes different actions, such as rolling the dice, buying a property, etc. Depending on the rules of the game, different actions are available at the moment.

It is considered that a player is out of the game if they cannot complete their obligations (such as pay rent).
When there is only one player left in the game or when the maximum number of turns has been taken, the game is over.

Winning the game is determined by the amount of money the player has at the end of the game.

# Build

This application uses Gradle to build. From the root repository directory, run the following command: 
    
    gradlew monopoly_app:simJar
    
The resulting monopoly_app-sim.jar file will be placed to the ./monopoly_app/build/libs folder

# Run

use java to execute the application:

    java -jar monopoly_app/build/libs/monopoly_app-sim.jar [-p PLAYERS] 

usage: monopoly_app-sim.jar
    
    -p,--players            number of players to simulate (2-4) 
                            two players by default
    
      
## Execution example:

    java -jar build/libs/game-0.1-sim.jar -p 4

    This is a game of Monopoly!
    14:18:12.830 Putting 18 coins in @Player1's account
    14:18:12.834 Putting 18 coins in @Player2's account
    14:18:12.835 Game turn 1 for player @Player1
    14:18:12.842 @Player1 can now play the following action cards: [New Turn]
    14:18:12.855 [New Turn] is being played by @Player1
    14:18:12.859 Used [New Turn]; @Player1 received the following cards: [Roll Dice, End Turn]
    14:18:12.859 @Player1 can now play the following action cards: [Roll Dice]
    14:18:12.859 [Roll Dice] is being played by @Player1
    14:18:12.859 @Player1 rolled 2
    14:18:12.860 Used [Roll Dice]; @Player1 received the following cards: [Move]
    14:18:12.860 @Player1 can now play the following action cards: [Move]
    14:18:12.860 [Move] is being played by @Player1
    14:18:12.860 @Player1: advancing by 2 steps to 2 (Donut Shop)
    14:18:12.861 @Player1 is moving from position 0 to position 2
    14:18:12.865 Used [Move]; @Player1 received the following cards: [Arrival]
    14:18:12.865 @Player1 can now play the following action cards: [Arrival]
    14:18:12.865 [Arrival] is being played by @Player1
    14:18:12.865 No one owns the Donut Shop, @Player1 can purchase it
    14:18:12.866 Used [Arrival]; @Player1 received the following cards: [Buy]
    14:18:12.866 @Player1 can now play the following action cards: [Buy]
    14:18:12.866 [Buy] is being played by @Player1
    14:18:12.866 Withdrawing 1 coin(s) from player @Player1
    14:18:12.866 @Player1 has 17 coin(s)
    14:18:12.866 Property 2 (Donut Shop) is now owned by @Player1
    14:18:12.866 Used [Buy]; @Player1 received the following cards: []
    14:18:12.866 @Player1 can now play the following action cards: [End Turn]
    14:18:12.868 [End Turn] is being played by @Player1
    14:18:12.869 Finishing turn for player @Player1
    14:18:12.869 Used cards: [New Turn, Roll Dice, Move, Arrival, Buy]
    ...
    14:20:10.172 Game turn 111 for player @Player1
    14:20:10.172 @Player1 can now play the following action cards: [New Turn]
    14:20:10.172 [New Turn] is being played by @Player1
    14:20:10.172 Used [New Turn]; @Player1 received the following cards: [Roll Dice, End Turn]
    14:20:10.172 @Player1 can now play the following action cards: [Roll Dice]
    14:20:10.172 [Roll Dice] is being played by @Player1
    14:20:10.172 @Player1 rolled 4
    14:20:10.172 Used [Roll Dice]; @Player1 received the following cards: [Move]
    14:20:10.172 @Player1 can now play the following action cards: [Move]
    14:20:10.172 [Move] is being played by @Player1
    14:20:10.172 @Player1: advancing by 4 steps to 8 (Museum)
    14:20:10.172 @Player1 is moving from position 4 to position 8
    14:20:10.172 Used [Move]; @Player1 received the following cards: [Arrival]
    14:20:10.172 @Player1 can now play the following action cards: [Arrival]
    14:20:10.172 [Arrival] is being played by @Player1
    14:20:10.172 Player @Player1 is obligated to pay rent to @Player2 for Museum
    14:20:10.172 Player @Player2 owns all properties of the same color YELLOW, so the owner gets double rent: 4
    14:20:10.172 Used [Arrival]; @Player1 received the following cards: [Pay Rent]
    14:20:10.172 @Player1 can now play the following action cards: [Pay Rent]
    14:20:10.173 [Pay Rent] is being played by @Player1
    14:20:10.173 Withdrawing 4 coin(s) from player @Player1
    14:20:10.173 @Player1 has not enough coins 4, current balance: 2
    14:20:10.173 Player cannot play card: Not enough coins
    14:20:10.173 Creating contracts for player's possession
    14:20:10.173 Used [Pay Rent]; @Player1 received the following cards: [Pay Rent, Contract]
    14:20:10.173 @Player1 can now play the following action cards: [Contract]
    14:20:10.173 [Contract] is being played by @Player1
    14:20:10.173 Player @Player1 is contracting property 4 (Bakery)
    14:20:10.173 Adding 1 coins to @Player1
    14:20:10.173 @Player1 has 3
    14:20:10.173 Property 4 (Bakery) is now free
    14:20:10.173 @Player1 lost property 4 (Bakery)
    14:20:10.173 Used [Contract]; @Player1 received the following cards: []
    14:20:10.173 @Player1 can now play the following action cards: [Pay Rent]
    14:20:10.173 [Pay Rent] is being played by @Player1
    14:20:10.173 Withdrawing 4 coin(s) from player @Player1
    14:20:10.173 @Player1 has not enough coins 4, current balance: 3
    14:20:10.173 Player cannot play card: Not enough coins
    14:20:10.173 Player @Player1 has no properties
    14:20:10.173 Used [Pay Rent]; @Player1 received the following cards: [Pay Rent]
    14:20:10.173 Finishing turn for player @Player1
    14:20:10.173 Used cards: [New Turn, Roll Dice, Move, Arrival, Contract]
    14:20:10.173 Not used cards: [End Turn, Pay Rent]
    14:20:10.173 Player @Player1 has mandatory cards: [End Turn, Pay Rent]
    14:20:10.173 @Player1: changing status from IN_GAME to OUT_OF_GAME
    ...
    14:20:10.175 No next player
    14:20:10.175 Game ended
    14:20:10.176 Free properties on the board: [Bakery, Burger Joint, Swimming Pool, Toy Shop]
    14:20:10.182 Winner: @Player2
    14:20:10.183 @Player1 - Player: @Player1 at position: 8, with status: OUT_OF_GAME, with coins: 3, with action cards: [End Turn, Pay Rent] and belongings: []
    14:20:10.238 @Player2 - Player: @Player2 at position: 14, with status: IN_GAME, with coins: 19, with action cards: [] and belongings: [Coffee Shop, Donut Shop, Library, Museum, Go-Karts, Cinema, Theatre, Pet Shop, Aquarium, The ZOO, Park Lane, Mayfair]


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

    java -jar monopoly_app/build/libs/monopoly_app-sim.jar -p 2 

    This is a game of Game!
    Number of players: 2
    11:00:11.475 @Player1 at position 0
    11:00:11.479 @Player2 at position 0
    11:00:11.479 Putting 18 coin(s) in @Player1's account
    11:00:11.479 Putting 18 coin(s) in @Player2's account
    11:00:11.479 Starting game
    11:00:11.482 @Player1 is starting turn 1
    11:00:11.487 Info: Player: @Player1 at position: 0, with status: IN_GAME, with coins: 18, with action cards: [] and belongings: []
    11:00:11.524 Active cards: [New Turn]
    11:00:11.524 'New Turn' is being played by @Player1
    11:00:11.526 Card played: 'New Turn'
    11:00:11.527 @Player1 received the following cards: [Roll Dice, End Turn]
    11:00:11.529 Active cards: [Roll Dice]
    11:00:11.529 'Roll Dice' is being played by @Player1
    11:00:11.529 @Player1 rolled 3
    11:00:11.529 Card played: 'Roll Dice'
    11:00:11.530 @Player1 received the following cards: [Move]
    11:00:11.530 Active cards: [Move]
    11:00:11.530 'Move' is being played by @Player1
    11:00:11.530 @Player1: advancing by 3 steps to 3 (Chance)
    11:00:11.533 @Player1 is moving from position 0 to position 3
    ...
    11:00:11.619 Info: Player: @Player1 at position: 14, with status: IN_GAME, with coins: 11, with action cards: [] and belongings: [Toy Shop, Mayfair, Museum, Go-Karts, Theatre]
    11:00:11.619 Active cards: [New Turn]
    11:00:11.619 'New Turn' is being played by @Player1
    11:00:11.619 Card played: 'New Turn'
    11:00:11.619 @Player1 received the following cards: [Roll Dice, End Turn]
    11:00:11.619 Active cards: [Roll Dice]
    11:00:11.619 'Roll Dice' is being played by @Player1
    11:00:11.619 @Player1 rolled 2
    11:00:11.619 Card played: 'Roll Dice'
    11:00:11.619 @Player1 received the following cards: [Move]
    11:00:11.619 Active cards: [Move]
    11:00:11.619 'Move' is being played by @Player1
    11:00:11.619 @Player1: advancing by 2 steps to 16 (Pet Shop)
    11:00:11.619 @Player1 is moving from position 14 to position 16
    11:00:11.619 Card played: 'Move'
    11:00:11.619 @Player1 received the following cards: [Arrival]
    11:00:11.620 Active cards: [Arrival]
    11:00:11.620 'Arrival' is being played by @Player1
    11:00:11.620 No one owns the Pet Shop, @Player1 can purchase it
    11:00:11.620 Card played: 'Arrival'
    11:00:11.620 @Player1 received the following cards: [Buy]
    11:00:11.620 Active cards: [Buy]
    11:00:11.620 'Buy' is being played by @Player1
    11:00:11.620 Withdrawing 3 coin(s) from player @Player1
    11:00:11.620 @Player1 has 8 coin(s)
    11:00:11.620 Property 16 (Pet Shop) is now owned by @Player1
    11:00:11.620 Card played: 'Buy'
    11:00:11.620 @Player1 received no cards.
    11:00:11.620 Active cards: [End Turn]
    ...
    11:00:11.714 Withdrawing 5 coin(s) from player @Player2
    11:00:11.714 @Player2 has not enough coins 5, current balance: 4
    11:00:11.714 Player cannot pay: Not enough coins
    11:00:11.714 Player @Player2 has no properties
    11:00:11.714 Card played: 'Pay Rent'
    11:00:11.714 @Player2 received the following cards: [Pay Rent]
    11:00:11.714 @Player2: card 'Payment(super=BaseActionCard(name=Pay Rent, action=PAY, type=OBLIGATION, priority=1000), recipient=Player(name=@Player1), value=5)' was already used
    11:00:11.715 Active cards: [End Turn]
    11:00:11.715 'End Turn' is being played by @Player2
    11:00:11.715 Player @Player2 has mandatory cards: [Pay Rent]
    11:00:11.715 @Player2: changing status from IN_GAME to OUT_OF_GAME
    11:00:11.716 Card played: 'End Turn'
    11:00:11.716 @Player2 received no cards.
    Game finished
    Free properties on the board: [Donut Shop, Bakery, Library, Cinema, Aquarium, The ZOO, Park Lane]
    Winner: @Player1
    @Player1 - Player: @Player1 at position: 16, with status: IN_GAME, with coins: 24, with action cards: [] and belongings: [Coffee Shop, Burger Joint, Museum, Swimming Pool, Go-Karts, Theatre, Pet Shop, Toy Shop, Mayfair]
    @Player2 - Player: @Player2 at position: 23, with status: OUT_OF_GAME, with coins: 4, with action cards: [] and belongings: []


monopoly-project
================

The simple application which plays a game of Monopoly with a given number of players.
There is a board with a number of properties, each with a name, a price, and a color.
Specific lands are also on the board, such as the Go, Jail, and Chance spaces.

    +-----------+-----------+-----------+-----------+-----------+-----------+-----------+      
    |           |   BLUE    |   BLUE    |           |  INDIGO   |  INDIGO   |           |      
    |  Parking  | Cinema $3 |Theatre $3 |  Chance   | Pet Shop  | Toy Shop  | Goto Jail |      
    |           |           |           |           |    $3     |    $3     |           |      
    +-----------+-----------+-----------+-----------+-----------+-----------+-----------+      
    |   GREEN   |                                                           |  VIOLET   |      
    | Go-Karts  |                                                           | Aquarium  |      
    |    $2     |                                                           |    $4     |      
    +-----------+                                                           +-----------+      
    |   GREEN   |                                                           |  VIOLET   |      
    | Swimming  |                                                           |The ZOO $4 |      
    |  Pool $2  |                                                           |           |      
    +-----------+                                                           +-----------+      
    |           |                                                           |           |      
    |  Chance   |                                                           |  Chance   |      
    |           |                                                           |           |      
    +-----------+                                                           +-----------+      
    |  YELLOW   |                                                           |  RAINBOW  |      
    | Museum $2 |                                                           | Park Lane |      
    |           |                                                           |    $5     |      
    +-----------+                                                           +-----------+      
    |  YELLOW   |                                                           |  RAINBOW  |      
    |Library $2 |                                                           |Mayfair $5 |      
    |           |                                                           |           |      
    +-----------+-----------+-----------+-----------+-----------+-----------+-----------+      
    |           |  ORANGE   |  ORANGE   |           |    RED    |    RED    |           |      
    |   Jail    |  Burger   | Bakery $1 |  Chance   |Donut Shop |  Coffee   |   Start   |      
    |           | Joint $1  |           |           |    $1     |  Shop $1  |           |      
    +-----------+-----------+-----------+-----------+-----------+-----------+-----------+

On each turn, a player executes different actions, such as rolling the dice, buying a property, etc.
Depending on the rules of the game, different actions are available at the moment.

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
    -v,--verbose            verbose output

## Execution example:

    java -jar monopoly_app/build/libs/monopoly_app-sim.jar -p 4 

Game simulation with 4 players

    This is a game of Monopoly!
    Number of players: 4
    It was the turn number 1 by Player1. Player1 has $17 and 1 plots of land.
    Player1 is now located at Burger Joint in coordinates 5.
    Player1 has bought Burger Joint.
    The cards played by Player1 were: New Turn, Roll Dice, Move, Arrival, Buy, End Turn.
    
    Player2 made turn 2. Player2 has $17 and 1 plots of land.
    Player2 is now located at Coffee Shop in coordinates 1.
    Player2 has acquired Coffee Shop.
    The cards played by Player2 were: New Turn, Roll Dice, Move, Arrival, Buy, End Turn.
    
    On its 3 turn, Player3 played... Player3 has a total of 0 territories and $18.
    Player3 is now located at Chance in coordinates 3.
    Player got a chance: Birthday. Happy Birthday! Everyone gives you a present.
    The cards played by Player3 were: New Turn, Roll Dice, Move, Arrival, Birthday, Birthday Party.
    
    ...
    
    Player2 made turn 115. Player2 has $60 and 8 plots of land.
    Player2 obtained $6 from Player3.
    Player2 is now located at Park Lane in coordinates 22.
    Player2's moves were: Rent Revenue, New Turn, Roll Dice, Move, Arrival, End Turn.
    
    It was the turn number 116 by Player3. Player3 has a total of 0 territories and $3.
    Player3 is now located at Aquarium in coordinates 19.
    Theatre has been lost by Player3..
    Player3's moves were: New Turn, Roll Dice, Move, Arrival, Contract, End Turn.
    After Player3's visit to Aquarium, it have to pay Player2 $4.
    It's game over for player because player has failed to fulfill its obligations.
    
    Game finished
    Free properties on the board: [Donut Shop, Bakery, Burger Joint, Swimming Pool, Go-Karts, Theatre, The ZOO, Mayfair]
    Winner: Player2
    Player1 - Player: Player1 at position: 22, with coins: 3, with status: OUT_OF_GAME, with action cards: [Buy] and belongings: []
    Player2 - Player: Player2 at position: 22, with coins: 60, with status: IN_GAME, with action cards: [] and belongings: [1, 7, 8, 13, 16, 17, 19, 22]
    Player3 - Player: Player3 at position: 19, with coins: 3, with status: OUT_OF_GAME, with action cards: [Pay Rent] and belongings: []
    Player4 - Player: Player4 at position: 22, with coins: 4, with status: OUT_OF_GAME, with action cards: [Pay Rent] and belongings: []

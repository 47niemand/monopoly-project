monopoly-project
================

This application plays a game of Monopoly with a set number of players.
A board has several properties, each having a name, a price, and a color.
There are specific lands on the board, including the Go, Jail, and Chance spaces.

    +[12]-------+[13]-------+[14]-------+[15]-------+[16]-------+[17]-------+[18]-------+  
    |           |   BLUE    |   BLUE    |  Chance   |  INDIGO   |  INDIGO   |           |  
    |  Parking  | Cinema $3 |Theatre $3 |   Land    | Pet Shop  | Toy Shop  | Goto Jail |  
    |           |           |           |           |    $3     |    $3     |           |  
    +[11]-------+-----------+-----------+-----------+-----------+-----------+[19]-------+  
    |   GREEN   |                                                           |  VIOLET   |  
    | Go Karts  |                                                           | Aquarium  |  
    |    $2     |                                                           |    $4     |  
    +[10]-------+                                                           +[20]-------+  
    |   GREEN   |                                                           |  VIOLET   |  
    | Swimming  |                                                           |The Zoo $4 |  
    |  Pool $2  |                                                           |           |  
    +[9]--------+                                                           +[21]-------+  
    |  Chance   |                                                           |  Chance   |  
    |   Land    |                                                           |   Land    |  
    |           |                                                           |           |  
    +[8]--------+                                                           +[22]-------+  
    |  YELLOW   |                                                           |  RAINBOW  |  
    | Museum $2 |                                                           | Park Lane |  
    |           |                                                           |    $4     |  
    +[7]--------+                                                           +[23]-------+  
    |  YELLOW   |                                                           |  RAINBOW  |  
    |Library $2 |                                                           |Mayfair $5 |  
    |           |                                                           |           |  
    +[6]--------+[5]--------+[4]--------+[3]--------+[2]--------+[1]--------+[0]--------+  
    |           |  ORANGE   |  ORANGE   |  Chance   |    RED    |    RED    |           |  
    |   Jail    |  Burger   | Bakery $1 |   Land    |Donut Shop |  Coffee   |   Start   |  
    |           | Joint $1  |           |           |    $1     |  Shop $1  |           |  
    +-----------+-----------+-----------+-----------+-----------+-----------+-----------+  

On each turn, a player takes different actions, like rolling the dice or purchasing
a property, etc. Depending on the game's rules, various actions can be taken at the moment.

A player is out of the game if they can't meet their obligations, like paying rent.
The game ends when only one player remains or when the maximum number of turns has been reached.

The game's winner is determined by the amount of money the player has at the end.

Build
-----

To build the application, you need Java 21. Use Gradle to build it. Run this command from the root directory of the repository:

    gradlew monopoly_app:simJar

The resulting monopoly_app-sim.jar file will be placed to the ./monopoly_app/build/libs folder

Run
---

To run the application, use the following command:

    java -jar monopoly_app/build/libs/monopoly_app-sim.jar [-p PLAYERS] 

usage: monopoly_app-sim.jar

    -p,--players            number of players to simulate (2-4) 
                            two players by default
    -v,--verbose            verbose output

Execution example
-----------------

    java -jar monopoly_app/build/libs/monopoly_app-sim.jar -p 4 

Game simulation with 4 players

    This is a game of Monopoly!
    Number of players: 4

    Turn 1 was played by Player1. Player1 owns a total of 1 properties and holds $17.
    Player1 is currently at Donut Shop, located at coordinates 2.
    Player1 has purchased Donut Shop.
    The following cards were played by Player1: New Turn, Roll Dice, Move, Arrival, Buy, End Turn.
    
    Turn 2 was played by Player2. Player2 possesses $17 and owns 1 plots.
    Player2 is currently at Bakery, located at coordinates 4.
    Player2 has obtained ownership of Bakery.
    The moves by Player2 were: New Turn, Roll Dice, Move, Arrival, Buy, End Turn.
    
    Player3 executed turn 3. Player3 owns a total of 0 properties and holds $18.
    Player3 is currently at Jail, located at coordinates 6.
    The moves by Player3 were: New Turn, Roll Dice, Move, Arrival, End Turn.
    
    During turn 4, Player4 made their move. Player4 possesses $17 and owns 0 plots.
    Player4 is currently at Donut Shop, located at coordinates 2.
    Player4 has paid $1 to Player1 for Donut Shop.
    Listed below are the cards played by Player4: New Turn, Roll Dice, Move, Arrival, Pay Rent, End Turn.
    
    Player1 executed turn 5. Player1 possesses $18 and owns 1 plots.
    Player1 collected $1 from Player4.
    Player1 is currently at Chance Land, located at coordinates 3.
    Player got a chance: Happy Birthday! Everyone gives you a present.
    The moves by Player1 were: Rent Revenue, New Turn, Roll Dice, Move, Arrival, Fortune Card, Birthday Party.
    
    Player2 executed turn 6. Player2 possesses $16 and owns 1 plots.
    Player2 is currently at Bakery, located at coordinates 4.
    Player2 settles debts amounting to $1.
    The moves by Player2 were: Gift, End Turn.
    
    Player3 executed turn 7. Player3 owns a total of 0 properties and holds $17.
    Player3 is currently at Jail, located at coordinates 6.
    Player3 settles debts amounting to $1.
    The moves by Player3 were: Gift, End Turn.

    
    ...

    Turn number 148 was completed by Player4. Player4 owns a total of 4 properties and holds $4.
    Player4 received $7 transferred by Player3, Player1.
    Player4 is currently at Mayfair, located at coordinates 23.
    Player4 has paid $5 to Player3 for Mayfair.
    The moves by Player4 were: Rent Revenue, Rent Revenue, New Turn, Roll Dice, Move, Arrival, Pay Rent, End Turn.
    
    Turn number 149 was completed by Player1. Player1 possesses $5 and owns 6 plots.
    Player1 is currently at Mayfair, located at coordinates 23.
    Player1 has paid $5 to Player3 for Mayfair.
    Listed below are the cards played by Player1: New Turn, Roll Dice, Move, Arrival, Pay Rent, End Turn.
    
    Turn number 150 was completed by Player3. Player3 possesses $69 and owns 5 plots.
    Player3 collected $10 from Player1, Player4.
    Player3 is currently at Pet Shop, located at coordinates 16.
    Listed below are the cards played by Player3: Rent Revenue, Rent Revenue, New Turn, Roll Dice, Move, Arrival, End Turn.
    
    The game has finished.
    Available properties on the board: [The Zoo]
    Winner: Player3
    Player1 - Player: Player1 at position: 23, with coins: 5, with status: IN_GAME, with action cards: [] and belongings: [2, 5, 7, 8, 11, 14]
    Player2 - Player: Player2 at position: 23, with coins: 2, with status: OUT_OF_GAME, with action cards: [PayRent] and belongings: []
    Player3 - Player: Player3 at position: 16, with coins: 69, with status: IN_GAME, with action cards: [] and belongings: [1, 4, 16, 17, 23]
    Player4 - Player: Player4 at position: 23, with coins: 4, with status: IN_GAME, with action cards: [NewTurn, FortuneCard] and belongings: [10, 13, 19, 22]

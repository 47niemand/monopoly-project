monopoly-project
================

The simple application which plays a game of monopoly with a given number of players.
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

This application uses Gradle to build. Use the following command: 
    
    gradlew monopoly:simJar
    
The resulting monopoly-0.1-sim.jar file will be placed to the ./monopoly/build/libs folder

# Run

use java to execute the application:

    java -jar monopoly/build/libs/monopoly-0.1-sim.jar

usage: monopoly
    
    -p,--players            number of players to simulate (2-4) 
                            two players by default
    
      
## Execution example:

    java -jar build/libs/monopoly-0.1-sim.jar -p 4

    This is a game of Monopoly!
    Number of players: 4
    13:22:40.753 Setting 18 money to @Player1
    13:22:40.756 Setting 18 money to @Player2
    13:22:40.756 Setting 18 money to @Player3
    13:22:40.756 Setting 18 money to @Player4
    13:22:40.756 PlayTurn 1 - Player @Player1
    13:22:40.758 Player's @Player1 action cards: [New Turn]
    13:22:40.758 Step 1
    13:22:40.763 @Player1: active action cards: [New Turn]
    13:22:40.763 @Player1: playing card New Turn
    13:22:40.769 @Player1 used New Turn and spawned some new cards: [Roll Dice, End Turn]
    13:22:40.769 Step 2
    13:22:40.769 @Player1: active action cards: [Roll Dice]
    13:22:40.769 @Player1: playing card Roll Dice
    13:22:40.769 Player @Player1 rolled 4
    13:22:40.770 @Player1 used Roll Dice and spawned some new cards: [Move]
    13:22:40.770 Step 3
    13:22:40.770 @Player1: active action cards: [Move]
    13:22:40.770 @Player1: playing card Move
    13:22:40.770 @Player1: moving by 4 steps to 4 (Bakery)
    13:22:40.771 @Player1: changing position from 0 to 4
    13:22:40.773 @Player1 used Move and spawned some new cards: [Arrival]
    ....
    13:22:40.920 Player's @Player3 action cards: [New Turn]
    13:22:40.920 Step 1
    13:22:40.920 @Player3: active action cards: [New Turn]
    13:22:40.920 @Player3: playing card New Turn
    13:22:40.920 @Player3 used New Turn and spawned some new cards: [Roll Dice, End Turn]
    13:22:40.920 Step 2
    13:22:40.920 @Player3: active action cards: [Roll Dice]
    13:22:40.920 @Player3: playing card Roll Dice
    13:22:40.920 Player @Player3 rolled 2
    13:22:40.920 @Player3 used Roll Dice and spawned some new cards: [Move]
    13:22:40.920 Step 3
    13:22:40.920 @Player3: active action cards: [Move]
    13:22:40.920 @Player3: playing card Move
    13:22:40.920 @Player3: moving by 2 steps to 12 (Parking)
    13:22:40.920 @Player3: changing position from 10 to 12
    13:22:40.920 @Player3 used Move and spawned some new cards: [Arrival]
    13:22:40.920 Step 4
    13:22:40.920 @Player3: active action cards: [Arrival]
    13:22:40.920 @Player3: playing card Arrival
    13:22:40.920 @Player3 used Arrival and spawned no new cards
    13:22:40.920 Step 5
    13:22:40.920 @Player3: active action cards: [End Turn]
    13:22:40.920 @Player3: playing card End Turn
    13:22:40.920 Finishing turn for player @Player3
    13:22:40.920 Used cards: [New Turn, Roll Dice, Move, Arrival]
    13:22:40.920 Not used cards: []
    13:22:40.920 @Player3 used End Turn and spawned no new cards
    13:22:40.920 Game loop ended after 150 turns
    13:22:40.922 Winner: @Player1
    13:22:40.924 @Player1 - PlayerInfo(player=@Player1, position=5, status=IN_GAME, money=51, actionCards=[], belongings=[Donut Shop, Library, Park lane, Mayfair])
    13:22:40.942 @Player2 - PlayerInfo(player=@Player2, position=10, status=IN_GAME, money=35, actionCards=[], belongings=[Coffee Shop, Burger Joint, Swimming pool, Pet Shop, Aquarium])
    13:22:40.942 @Player3 - PlayerInfo(player=@Player3, position=12, status=IN_GAME, money=8, actionCards=[], belongings=[])
    13:22:40.942 @Player4 - PlayerInfo(player=@Player4, position=19, status=OUT_OF_GAME, money=0, actionCards=[End Turn, Pay Rent], belongings=[])

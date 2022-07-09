monopoly-project
================

The simple application which plays a game of monopoly with a given number of players.
There is a board with a number of properties, each with a name, a price, and a color.
Special lands are also on the board, such as the Go, Jail, and Chance spaces.

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
     
On each turn, a player executes different actions, such as rolling the dice, buying a property, etc. 
Depending on the rules of the game, different actions are available at the moment.

A player is considered to be out of the game if they have enough money to cover their obligations.
When there is only one player left in the game or when the maximum number of turns have been taken, 
the game is over.

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
    13:10:12.119 [main] INFO  p.m.m.m.g.Bank - Setting 18 money to @Player1
    13:10:12.122 [main] INFO  p.m.m.m.g.Bank - Setting 18 money to @Player2
    13:10:12.122 [main] INFO  p.m.m.m.g.Bank - Setting 18 money to @Player3
    13:10:12.122 [main] INFO  p.m.m.m.g.Bank - Setting 18 money to @Player4
    13:10:12.131 [main] INFO  p.m.m.m.g.Game - Turn 1 - Player @Player1
    13:10:12.133 [main] INFO  p.m.m.m.g.Game - Player's @Player1 action cards: [New Turn]
    13:10:12.133 [main] INFO  p.m.m.s.DefaultStrategy - Step 1
    13:10:12.137 [main] INFO  p.m.m.m.g.Game - @Player1: active action cards: [New Turn]
    13:10:12.138 [main] INFO  p.m.m.m.g.Game - @Player1: playing card New Turn
    13:10:12.143 [main] INFO  p.m.m.m.g.Game - @Player1 used New Turn and spawned some new cards: [Roll Dice, End Turn]
    13:10:12.143 [main] INFO  p.m.m.s.DefaultStrategy - Step 2
    13:10:12.144 [main] INFO  p.m.m.m.g.Game - @Player1: active action cards: [Roll Dice]
    13:10:12.144 [main] INFO  p.m.m.m.g.Game - @Player1: playing card Roll Dice
    13:10:12.144 [main] INFO  p.m.m.m.a.RollDice - Player @Player1 rolled 3
    13:10:12.146 [main] INFO  p.m.m.m.g.Game - @Player1 used Roll Dice and spawned some new cards: [Move]
    13:10:12.146 [main] INFO  p.m.m.s.DefaultStrategy - Step 3
    13:10:12.146 [main] INFO  p.m.m.m.g.Game - @Player1: active action cards: [Move]
    13:10:12.146 [main] INFO  p.m.m.m.g.Game - @Player1: playing card Move
    13:10:12.146 [main] INFO  p.m.m.m.a.Move - @Player1 moving by 3 steps to 3 (Chance)
    13:10:12.150 [main] INFO  p.m.m.m.g.Game - @Player1 changing position from 0 to 3
    13:10:12.153 [main] INFO  p.m.m.m.g.Game - @Player1 used Move and spawned some new cards: [Arrival]
    13:10:12.153 [main] INFO  p.m.m.s.DefaultStrategy - Step 4
    13:10:12.153 [main] INFO  p.m.m.m.g.Game - @Player1: active action cards: [Arrival]
    13:10:12.153 [main] INFO  p.m.m.m.g.Game - @Player1: playing card Arrival
    13:10:12.153 [main] INFO  p.m.m.m.g.Game - @Player1 used Arrival and spawned some new cards: [Chance(ADVANCE_TO_YELLOW_OR_RAINBOW)]
    13:10:12.153 [main] INFO  p.m.m.s.DefaultStrategy - Step 5
    13:10:12.153 [main] INFO  p.m.m.m.g.Game - @Player1: active action cards: [Chance(ADVANCE_TO_YELLOW_OR_RAINBOW)]
    13:10:12.153 [main] INFO  p.m.m.m.g.Game - @Player1: playing card Chance(ADVANCE_TO_YELLOW_OR_RAINBOW)
    13:10:12.155 [main] INFO  p.m.m.m.g.Game - @Player1 used Chance(ADVANCE_TO_YELLOW_OR_RAINBOW) and spawned some new cards: [Get or pay, Get or pay, Get or pay, Get or pay]
    13:10:12.155 [main] INFO  p.m.m.m.g.Game - Chance card ADVANCE_TO_YELLOW_OR_RAINBOW returned
    ...
    13:10:12.253 [main] INFO  p.m.m.s.DefaultStrategy - Step 7
    13:10:12.253 [main] INFO  p.m.m.m.g.Game - @Player4: active action cards: [End Turn]
    13:10:12.253 [main] INFO  p.m.m.m.g.Game - @Player4: playing card End Turn
    13:10:12.253 [main] INFO  p.m.m.m.g.Game - Finishing turn for player @Player4
    13:10:12.253 [main] INFO  p.m.m.m.g.Game - Used cards: [New Turn, Roll Dice, Move, Income, Arrival, Pay Rent]
    13:10:12.253 [main] INFO  p.m.m.m.g.Game - Not used cards: []
    13:10:12.253 [main] INFO  p.m.m.m.g.Game - @Player4 used End Turn and spawned no new cards
    13:10:12.253 [main] INFO  p.m.m.m.g.Game - Game loop ended after 100 turns
    13:10:12.255 [main] INFO  p.m.m.m.g.Game - Winner: @Player4
    13:10:12.257 [main] INFO  p.m.m.m.g.Game - @Player1 - PlayerInfo(player=@Player1, position=11, status=IN_GAME, money=2, actionCards=[Chance(GET_OUT_OF_JAIL_FREE)], belongings=[Pet Shop])
    13:10:12.274 [main] INFO  p.m.m.m.g.Game - @Player2 - PlayerInfo(player=@Player2, position=6, status=IN_GAME, money=4, actionCards=[], belongings=[Donut Shop, Bakery, Burger Joint, Swimming pool, Mayfair])
    13:10:12.274 [main] INFO  p.m.m.m.g.Game - @Player3 - PlayerInfo(player=@Player3, position=5, status=IN_GAME, money=14, actionCards=[], belongings=[Go-Karts, Aquarium, The ZOO])
    13:10:12.274 [main] INFO  p.m.m.m.g.Game - @Player4 - PlayerInfo(player=@Player4, position=4, status=IN_GAME, money=40, actionCards=[], belongings=[Coffee Shop, Library, Museum, Cinema, Theatre, Toy Shop, Park lane])





monopoly-project
================

The simple application which plays a game of monopoly with a given number of players.
There is a board with a number of properties, each with a name, a price, and a color.
Special lands are also on the board, such as the Go, Jail, and Chance spaces.
On each turn, a player executes different actions, such as rolling the dice, buying a property, etc. 
Depending on the rules of the game, different actions are available at the moment.

A player is considered to be out of the game if they have enough money to cover their obligations.
When there is only one player left in the game or when the maximum number of turns have been taken, 
the game is over.

Winning the game is determined by the amount of money the player has at the end of the game.


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

    java -jar build/libs/monopoly-0.1-sim.jar -p 2

    This is a game of Monopoly!
    12:49:20.041 [main] INFO  pp.muza.monopoly.model.game.Bank - Adding 18 money to @Player1
    12:49:20.043 [main] INFO  pp.muza.monopoly.model.game.Bank - @Player1 has 18
    12:49:20.043 [main] INFO  pp.muza.monopoly.model.game.Bank - Adding 18 money to @Player2
    12:49:20.043 [main] INFO  pp.muza.monopoly.model.game.Bank - @Player2 has 18
    12:49:20.051 [main] INFO  pp.muza.monopoly.model.game.Game - Turn 1 - Player @Player1
    12:49:20.053 [main] INFO  pp.muza.monopoly.model.game.Game - Player's @Player1 Action cards: [New Turn]
    12:49:20.053 [main] INFO  p.m.m.m.g.strategy.DefaultStrategy - Executing step 1
    .... 
    12:49:20.243 [main] INFO  pp.muza.monopoly.model.game.Game - Action card End Turn executed: true
    12:49:20.243 [main] INFO  pp.muza.monopoly.model.game.Game - No next player
    12:49:20.246 [main] INFO  pp.muza.monopoly.model.game.Game - Winner: @Player1
    12:49:20.247 [main] INFO  pp.muza.monopoly.model.game.Game - @Player1 - PlayerInfo(player=@Player1, position=0, status=IN_GAME, money=39, actionCards=[], belongings=[Coffee Shop,Donut Shop,Bakery,Burger Joint,Library,Museum,Go-Karts,Theatre,Pet Shop,Toy Shop,Aquarium,The ZOO,Park lane,Mayfair])
    12:49:20.258 [main] INFO  pp.muza.monopoly.model.game.Game - @Player2 - PlayerInfo(player=@Player2, position=17, status=OUT_OF_GAME, money=0, actionCards=[End Turn,Contract,Contract,Pay Rent], belongings=[])



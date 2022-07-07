monopoly-project
================

The simple application which plays a game of monopoly with a given number of players.

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



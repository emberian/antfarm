# antfarm
a demonstration of integrating unity and processing4

## Simulator Features

The simulator implements a straightforward cellular automata. Ants move from one grid square to an adjacent (8 neighbor) square. They remember the grid squares they have moved from, and will try to avoid making “large steers” from one turn to the next by preferring to move forward in the direction they moved last turn. The cells accumulate “pheromone” as ants pass over them, according to the equation

phn+1 =phn + isAnt * (1.01-phn)- 0.001  

(where isAnt represents that the cell is currently occupied by an ant). The cells also accumulate “food pheromone” as food-carrying ants pass over them, according to the equation

fPhn+1=fPhn + (isAnt * isFood * 55) - 0.025

When deciding which adjacent cell to move to, ants compute the function Val = 5 + fPh + ph + (isFood * 150) for each neighbor and uses this value as the width of an interval. The ant then selects randomly a point from [0, sum(neighbor vals)], determines the interval the point falls in, and moves to the square that contributed that interval.

This weighted random walk is only followed when the ant does not find food. When the ant finds food, it remembers exactly the path it took and reverses it, taking shortcuts when it encounters a landmark that it remembers seeing (crosses its own path history). The constants in the pheromone evolution function are tuned to be interesting to look at. The screenshot above shows how strong the food pheromones are compared to the normal pheromones, almost no exploration is occurring now that a few food trails have been created. The screenshot to the right is the near-terminal state of that colony, showing that a few different paths are randomly taken. The superposition of ants belies the true population, somewhere in the thousands.

## Unity Features

The Unity simulator connects over an HTTP bridge using a “current state”/”next state”/”perform action” paradigm. It supports basic gesture detection via the Ultraleap Stereo hand tracking module, and when an ant is touched it is “squashed”. There is no interpolation, animation, etc, it’s a very brute renderer of the network information at the moment. It demonstrates how Processing apps and the Unity engine can successfully intercommunicate.

## Connecting to your own Processing app

The bridge is in [UnityConnector.java](./java/src/main/java/antfarm/UnityConnector.java). The `Simulator` interface, when implemented, will automatically network to Unity when using the `Run` harness. It is not generic, you must edit the Frame, Updates, and Action classes to represent your game world, changes to the game world, and user actions respectively.

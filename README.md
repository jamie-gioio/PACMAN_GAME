Pac-Man (Java Swing)

A classic Pac-Man clone built in Java using Swing and AWT. Guide Pac-Man through the maze, eat every dot(food) and avoid the four ghosts.

## Features

- Classic 19x21 tile maze with smooth, rounded neon-blue walls drawn using Path2D
  and Area.
- Four ghosts (blue, pink, red, yellow) that roam the maze in random directions
  and change sprites based on movement direction.
- Directional PacMan sprites (up, down, left, right).
- Queued turning: press a direction early and Pacman turns at the next   
  intersection.
- Score tracking (10 points per dot) and a 3 life cycle.
- Start screen button with a 3 second countdown before the game begins.
- Automatic maze reset when all dots are eaten
- Game Over screen with quick restart on any key press

## Controls

- Arrow Up: Move Up
- Arrow Down: Move Down
- Arrow Left: Move Left
- Arrow Right: Move Right
- Any Key: Restart the game 

## How to Play

1. Click the start button in the center of the maze.
2. Wait 3 seconds and game automatically starts.
3. Use the arrow keys to steer PacMan and eat all the dots.
4. Touching a ghost costs one life and resets everyone to their starting 
   positions.
5. You lose when all 3 lives are gone. Press any key to play again.

## Requirements

 - Java Development Kit (JDK) 11 or newer.
 - A desktop environment ( Swing needs a display ).

 ## Project Setup

 src
 |- PacMan.java     # Game Logic, rendering, input handling
 |- App.java        # Entry point: creates the window and launches the game
 |--assets/         # Sprites (loaded from the classpath)
    |- pacmanUp.png / pacmanDown.png / pacmanLeft.png / pacmanRight.png
    |- blueGhostUp.png / blueGhostDown.png / blueGhostLeft.png / blueGhostRight.png


## Running the Game

1. From command line: 
    - `javac App.java PacMan.java`
    - `java App`

2. From an IDE
    - Open the project in VS Code.
    - Make sure the assets folder is on the classpath alongside the source files.
    - Run App.java

## How it Works

- Tile map: The maze is defined as an array of strigns in PacMan.java
    - `X` : Wall
    - blank space: Dot
    - `P` : PacMan start Position
    - `b, p, r, y` : Blue, pink, red, yellow ghost start positions
    - `O` : Empty tile with no dot

- Game Loop: A Swing Timer fires every 50ms to move entities, check collisions, and repaint.
- Movement: Entities move in quarter-tile steps. Pac-Man only changes direction when aligned to 
            the tile grid.
- Collisions: Simple axis-aligned bounding-box checks between Pac-Man, walls, dots, and ghosts
- Ghost AI: Ghosts pick a random direction whenever they hit a wall.

## Known Limitations / Ideas for Future Work

1. Ghosts move randomly rather than chasing Pac-Man.
    - Update will have the ghost seeking out Pac-Man.
2. No power pellets or frightened-ghost mode yet.
3. No sound effects or persistent high score.
4. Pac-Man and the ghosts dont wrap through the side tunnel.

Author
Jamie
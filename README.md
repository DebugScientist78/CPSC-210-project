# Personal Project - Minesweeper

## Project Description
This project centers around the idea of minesweeper, a classic board like game.
However, it will have some unique features apart from the core game,

This **includes**: 
- The ability to choose the number of mines present in a board
- the user being able to define the amount of mines present
- Choose a custom size for the board
- Replay function, the application can play a players prior solved board, move by move
- A feature I strongly want to implement is a *solver,
for such a custom board* 

## Motivation
I choose this idea as I've been playing Minesweeper during waiting times on public 
transit and I thought it would be interesting to deviate from the standard minesweeper
setup and, as well, include a *solver*. As an interesting technical challenge to this 
project. Something that these types of puzzle games don't offer is a replay function, 
a way to re-watch how a board is played and solved

## User Stories
- As a user, I want to be able to be able to view replay input log
- As a user, I want to be able to choose the amount of mines a board has
- As a user, I want to be able to choose the board size
- As a user, I want to be able to provide a board solver
- As a user, I want to be able to Flag a square
- As a user, I want to be able to uncover a square

- As a user, I want to have the choice to save the game state and replay replay board at any point
- As a user, I want to be able to be able to load a previous in-progress or finished game, 
including latest turn/board state and play log

# Instructions for Grader
- You can add an Action State by either: placing a flag or uncovering a square from a menu 
  - (First related action to adding Xs to Ys)
- You can view the previous Action States as a Play log view a menu button, as well filter which turns you want to view
of three options (all, only opening action, or only placing/removing flags)
  - (Second related action to adding Xs to Ys)
- You can find the visual components via:
  - icon next to save and load buttons
  - the game board tiles
- You can save the state of the game via a button in the corner
- You can load the state of a previous game via a button in the corner
- You can solve the board via a button in the corner.

# Phase 4: Task 2

Tue Nov 28 17:15:59 PST 2023 
Tile opened at: 0, 0

Tue Nov 28 17:16:07 PST 2023 
Flag added at: 2, 2

Tue Nov 28 17:16:17 PST 2023 
Tile opened at: 3, 2

Tue Nov 28 17:16:22 PST 2023 
Flag added at: 2, 3

Tue Nov 28 17:16:37 PST 2023 
Tile opened at: 3, 0

Tue Nov 28 17:16:41 PST 2023 Flag added at: 3, 1

Tue Nov 28 17:16:51 PST 2023
Tile opened at: 3, 3

Tue Nov 28 17:16:55 PST 2023
View 3 most recent ActionStates in Replay

Tue Nov 28 17:17:01 PST 2023
View all Flag related ActionStates in Replay

Tue Nov 28 17:17:05 PST 2023
View all tile opening related ActionStates in Replay

Tue Nov 28 17:17:08 PST 2023
View All ActionStates in Replay

# Phase 4: Task 3
Observing the UML diagram and quickly skimming the GUI.java file, 
it becomes clear that the GUI class has extremely high coupling as it 
is dependent on the most of the classes in the Model package. Thus, refactoring
to rework the game's functionality into the model package instead would strengthen the
model classes' cohesion as it reduces the vague functionality that the GUI class currently
has. As it currently has both UI related rendering functions and game behavioural actions.
So moving some of the GUI functions regarding updating a replay object and game board should
be moved into the Player class, since the Player class handles user inputs in conjunction with
any game defined rule-set at my discretion. Similar to the SIGame class that is present in the
Invaders game example from lecture.

A smaller design issue would be introducing abstract classes for the GameBoard and GameTime 
class as they share similar helper functions, which would reduce coupling and probability
that debugging is prone to errors.
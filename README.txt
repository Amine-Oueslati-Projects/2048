=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: amineo
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays
    - The 2D array grid in TwentyFortyEight represents the game grid.
    Each cell in the array corresponds to a tile in the game.
    - The array keeps track of the value of each tile,
    facilitating the logic for combining tiles and determining game state.
    - 2D Arrays allow for straightforward indexing and manipulation of tiles.

  2. File I/O
    - The saveGame() and loadGame() methods in TwentyFortyEight use File I/O to
    save and load the game state. This includes the grid, score, and highest score.
    - Using File I/O to handle game state persistence allows the player to
    stop and resume the game at a later time.

  3. Stack Collection
    - The gridHistory and scoreHistory stacks in TwentyFortyEight are used to
    implement the undo feature. Each time a player makes a move, the current state
    of the grid and the score are pushed onto these stacks.
    - The undo() method pops the last state from the stack, allowing players to
    revert to a previous game state.
    - Stacks are ideal for the undo functionality as they operate on the LIFO principle.

  4. JUnit testable component
    - The TwentyFortyEightTest class contains various unit tests to check the
    functionality of TwentyFortyEight.
    - Tests include checking tile merging, score updates, game state changes, and undo operations.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

  - Grid Class:
  This class is responsible for the main game interface in the GUI.
  It includes components like score labels, status labels, and the game grid panel.
  It handles user input through keyboard events to control the game.
  It also manages the display of the game's current state, including scores and game status.

  - GameGridPanel Class:
  This class is focused on the visual representation of the game grid.
  It draws the game tiles, their colors, and numbers.

  - RunTwentyFortyEight Class:
  This class sets up the main game window and controls.
  It initializes the game by creating a JFrame, setting up the Grid panel,
  and adding control buttons like "Reset" and "Undo".

  - TwentyFortyEight Class:
  This class contains the core game logic. It manages the game's data state,
  including the grid of tiles, score, and game state (ongoing, won, lost).
  It handles the logic for player moves, tile merging, and generates new tiles.
  It also includes functionality for saving and loading game states, and undoing moves.

  - Tile Class:
  Represents a single tile in the game. It holds the value of the tile and
  provides methods for basic operations like checking if it's empty, setting its value,
  and doubling its value (for merging tiles).

  - TwentyFortyEightTest Class:
  Contains unit tests for the TwentyFortyEight class.


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

  Handling the GUI components alongside the game logic might have been challenging,
  especially ensuring that the graphical interface accurately reflects the
  game state at all times.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

    - The design shows a good separation of concerns.
    The GUI (Grid and GameGridPanel), game logic (TwentyFortyEight),
    and tile representation (Tile) are separated.
    The testing class (TwentyFortyEightTest) is also separated.
    - Encapsulation: Game state and logic are contained within the TwentyFortyEight class,
    while the Grid class manages the user interface aspects.
    - If I had more time, I would further modularize the code, improve error handling, and expand
    the unit tests to cover more edge cases.

========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

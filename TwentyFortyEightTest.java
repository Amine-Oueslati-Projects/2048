package org.cis1200.twentyfortyeight;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class TwentyFortyEightTest {
    private TwentyFortyEight game;

    @BeforeEach
    public void setUp() {
        game = new TwentyFortyEight();
        game.reset(false); // Do not load old game. Play new game.
    }

    @Test
    public void testInitialization() {
        assertEquals(
                TwentyFortyEight.GameState.ONGOING, game.getState(),
                "Game should be in ONGOING state upon initialization."
        );
        assertEquals(
                0, game.getScore(),
                "Score should be 0 upon initialization."
        );
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertTrue(
                        game.getValue(i, j) == 0 ||
                                game.getValue(i, j) == 2,
                        "Each tile should either be 0 or 2 upon initialization."
                );
            }
        }
    }

    @Test
    public void testPlayAndUndoTurn() {
        playRandomTurns(20);
        int scoreBefore = game.getScore();
        playRandomTurns(1);
        game.undo();
        assertEquals(
                scoreBefore, game.getScore(),
                "The undo should revert the score."
        );
    }

    @Test
    public void testSaveAndLoadGame() throws IOException {
        playRandomTurns(20);
        game.saveGame();
        assertTrue(
                Files.exists(Paths.get(TwentyFortyEight.FILE_NAME)),
                "Game file should exist after save."
        );

        game.reset(false);
        game.loadGame();
        assertEquals(
                0, game.getScore(),
                "Score should be 0 after loading a new game."
        );
    }

    @Test
    public void testScoreIncrementOnMerge() {
        int[][] mergingState = {
            { 2, 0, 0, 0 },
            { 2, 0, 0, 0 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 }
        };

        setupGrid(mergingState);

        int scoreBefore = game.getScore();
        game.playTurn(TwentyFortyEight.Direction.UP);
        assertEquals(game.getScore(), scoreBefore + 4, "Score should increase after tiles merge");
        assertEquals(
                4, game.getValue(0, 0),
                "Merging two '2' tiles should result in a '4' tile at the top of the first column"
        );
    }

    @Test
    public void testNoScoreIncrementOnNoMerge() {
        int[][] noMergeState = {
            { 2, 4, 2, 4 },
            { 4, 2, 4, 2 },
            { 2, 4, 2, 4 },
            { 4, 2, 4, 2 }
        };

        setupGrid(noMergeState);

        int scoreBefore = game.getScore();
        game.playTurn(TwentyFortyEight.Direction.UP);
        assertEquals(scoreBefore, game.getScore(), "Score should not change if no tiles merge");

        for (int i = 0; i < 4; i++) {
            assertEquals(
                    noMergeState[0][i], game.getValue(0, i),
                    "Tiles should not merge and just shift up"
            );
        }
    }

    @Test
    public void testUndoAfterMove() {
        int[][] initialState = {
            { 2, 4, 2, 4 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 }
        };
        setupGrid(initialState);

        game.playTurn(TwentyFortyEight.Direction.UP);
        game.undo();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(
                        initialState[i][j], game.getValue(i, j),
                        "Undoing the move should revert the grid to its initial state"
                );
            }
        }
    }

    @Test
    public void testUndoWithNoHistory() {
        Tile[][] initialGrid = copyGrid(game.getGrid());
        game.undo();
        Tile[][] postUndoGrid = game.getGrid();
        assertTrue(
                areGridsEqual(initialGrid, postUndoGrid),
                "Undoing with no history should leave the initial state unchanged"
        );
    }

    // Test Tile Operations
    @Test
    public void testNewTile() {
        Tile tile = new Tile();
        assertTrue(tile.isEmpty(), "Newly created tile should be empty");
    }

    @Test
    public void testDoubleValue() {
        Tile tile = new Tile();
        tile.setValue(4);
        tile.doubleValue();
        assertEquals(8, tile.getValue(), "Doubling 4 should result in 8");
    }

    @Test
    public void testSetTileEmpty() {
        Tile tile = new Tile();
        tile.setValue(4);
        tile.setEmpty();
        assertTrue(tile.isEmpty(), "After setting a tile to empty, it should be empty");
    }

    @Test
    public void testInitialTileGeneration() {
        int tileCount = countNonEmptyTiles(game.getGrid());
        assertEquals(2, tileCount, "Exactly two tiles should be generated initially");
    }

    @Test
    public void testResettingGrid() {
        playRandomTurns(20);
        game.reset(false);
        int tileCount = countNonEmptyTiles(game.getGrid());
        assertEquals(
                2, tileCount, "Resetting the game should result in exactly two tiles on the grid"
        );
        assertEquals(sumGrid(), 4);
    }

    @Test
    public void testNoNewTileOnFullGrid() {
        int[][] mergingState = {
            { 2, 4, 8, 16 },
            { 32, 62, 128, 256 },
            { 2, 4, 8, 16 },
            { 32, 62, 128, 256 }
        };

        setupGrid(mergingState);

        int tileCountBefore = countNonEmptyTiles(game.getGrid());
        int tileSumBefore = sumGrid();
        game.playTurn(TwentyFortyEight.Direction.UP);
        int tileCountAfter = countNonEmptyTiles(game.getGrid());
        int tileSumAfter = sumGrid();
        assertEquals(
                tileCountBefore, tileCountAfter,
                "No new tile should be generated on an invalid move"
        );
        assertEquals(tileSumBefore, tileSumAfter, "Sum should not change on an invalid move");
    }

    @Test
    public void testCheckLosingCondition() {
        int[][] mergingState = {
            { 2, 4, 8, 16 },
            { 32, 62, 128, 256 },
            { 2, 4, 8, 16 },
            { 32, 62, 128, 256 }
        };

        setupGrid(mergingState);

        game.playTurn(TwentyFortyEight.Direction.UP);
        assertEquals(
                TwentyFortyEight.GameState.LOST, game.getState(),
                "Game should be in LOST state when no moves are possible."
        );
    }

    @Test
    public void testWinningCondition() {
        int[][] mergingState = {
            { 2, 4, 8, 1024 },
            { 32, 62, 128, 1024 },
            { 2, 4, 8, 16 },
            { 32, 62, 128, 256 }
        };

        setupGrid(mergingState);

        game.playTurn(TwentyFortyEight.Direction.UP);
        assertEquals(
                TwentyFortyEight.GameState.WON, game.getState(),
                "Game should be in WON state when 2048 tile is created."
        );
    }

    // Helper methods
    public void playRandomTurns(int numTurns) {
        Random random = new Random();
        while (numTurns > 0) {
            int randomNumber = random.nextInt(4);

            switch (randomNumber) {
                case 0:
                    game.playTurn(TwentyFortyEight.Direction.UP);
                    break;
                case 1:
                    game.playTurn(TwentyFortyEight.Direction.DOWN);
                    break;
                case 2:
                    game.playTurn(TwentyFortyEight.Direction.RIGHT);
                    break;
                case 3:
                    game.playTurn(TwentyFortyEight.Direction.LEFT);
                    break;
                default:
                    break;
            }

            numTurns--;
        }
    }

    private void setupGrid(int[][] state) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                game.setValue(i, j, state[i][j]);
            }
        }
    }

    private int countNonEmptyTiles(Tile[][] grid) {
        int count = 0;
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                if (!tile.isEmpty()) {
                    count++;
                }
            }
        }
        return count;
    }

    private Tile[][] copyGrid(Tile[][] original) {
        Tile[][] copy = new Tile[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                copy[i][j] = new Tile();
                copy[i][j].setValue(original[i][j].getValue());
            }
        }
        return copy;
    }

    private int sumGrid() {
        int sum = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                sum += game.getValue(i, j);
            }
        }
        return sum;
    }

    private boolean areGridsEqual(Tile[][] grid1, Tile[][] grid2) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (grid1[i][j].getValue() != grid2[i][j].getValue()) {
                    return false;
                }
            }
        }
        return true;
    }
}

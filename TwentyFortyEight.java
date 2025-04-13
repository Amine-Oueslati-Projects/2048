package org.cis1200.twentyfortyeight;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;
import java.util.Stack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;

class Tile {
    private int value;

    public Tile() {
        value = 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        value = val;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public void setEmpty() {
        value = 0;
    }

    public void doubleValue() {
        value *= 2;
    }
}

public class TwentyFortyEight {
    private Tile[][] grid;
    private int score;
    private GameState state;
    private int highestScore;

    public final static String FILE_NAME = "2048.txt";

    private Stack<Tile[][]> gridHistory;
    private Stack<Integer> scoreHistory;

    public enum GameState {
        ONGOING, WON, LOST
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public TwentyFortyEight() {
        initialize();
    }

    private void initialize() {
        reset(true);
        highestScore = 0;
        gridHistory = new Stack<>();
        scoreHistory = new Stack<>();
    }

    public void reset(boolean load) {
        highestScore = Math.max(highestScore, score);

        grid = new Tile[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                grid[i][j] = new Tile();
            }
        }
        generateNewTile();
        generateNewTile();

        score = 0;
        state = GameState.ONGOING;

        if (load) {
            try {
                loadGame();
            } catch (IOException e) {
                System.err.println("Error loading game: " + e.getMessage());
            }
        } else {
            try {
                saveGame();
            } catch (IOException e) {
                System.err.println("Error saving game: " + e.getMessage());
            }
        }
    }

    private void saveCurrentState() {
        Tile[][] gridCopy = new Tile[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                gridCopy[i][j] = new Tile();
                gridCopy[i][j].setValue(grid[i][j].getValue());
            }
        }

        gridHistory.push(gridCopy);
        scoreHistory.push(score);

        // Limit the history size to avoid memory issues
        if (gridHistory.size() > 1000) {
            gridHistory.remove(0);
            scoreHistory.remove(0);
        }
    }

    public void undo() {
        if (!gridHistory.isEmpty() && !scoreHistory.isEmpty()) {
            grid = gridHistory.pop();
            score = scoreHistory.pop();
        }
    }

    public void playTurn(Direction direction) {
        saveCurrentState();

        if (direction == Direction.UP) {
            shiftUp();
        }

        if (direction == Direction.RIGHT) {
            rotateCCW();
            shiftUp();
            rotateCCW();
            rotateCCW();
            rotateCCW();
        }

        if (direction == Direction.DOWN) {
            rotateCCW();
            rotateCCW();
            shiftUp();
            rotateCCW();
            rotateCCW();
        }

        if (direction == Direction.LEFT) {
            rotateCCW();
            rotateCCW();
            rotateCCW();
            shiftUp();
            rotateCCW();
        }

        generateNewTile();

        try {
            saveGame();
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    private void shiftUp() {
        for (int c = 0; c < 4; c++) {
            shiftColumnUpOnce(c);

            for (int r = 0; r < 3; r++) {
                if (grid[r][c].getValue() == grid[r + 1][c].getValue()) {
                    grid[r][c].doubleValue(); // merging happens here
                    score += grid[r][c].getValue();
                    if (grid[r][c].getValue() == 2048) {
                        state = GameState.WON;
                    }
                    grid[r + 1][c].setEmpty();

                    shiftColumnUpOnce(c); // fill the merging gap
                    break;
                }
            }
        }
    }

    private void shiftColumnUpOnce(int c) {
        for (int r = 1; r < 4; r++) {
            if (!grid[r][c].isEmpty()) {
                int curR = r;
                while (curR > 0 && grid[curR - 1][c].isEmpty()) {
                    grid[curR - 1][c].setValue(grid[curR][c].getValue());
                    grid[curR][c].setEmpty();
                    curR--;
                }
            }
        }
    }

    private boolean emptyColumn(int c) {
        return grid[0][c].isEmpty() && grid[1][c].isEmpty() &&
                grid[2][c].isEmpty() && grid[3][c].isEmpty();
    }

    private void rotateCCW() {
        Tile[][] rotatedGrid = new Tile[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                rotatedGrid[i][j] = grid[j][3 - i];
            }
        }
        grid = rotatedGrid;
    }

    private void generateNewTile() {
        List<int[]> emptyCells = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (grid[i][j].isEmpty()) {
                    emptyCells.add(new int[] { i, j });
                }
            }
        }

        if (emptyCells.isEmpty()) {
            checkLosing();
            return;
        }

        Random random = new Random();
        int[] selectedCell = emptyCells.get(random.nextInt(emptyCells.size()));
        grid[selectedCell[0]][selectedCell[1]].setValue(2);
    }

    private void checkLosing() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i > 0 && grid[i][j].getValue() == grid[i - 1][j].getValue()) {
                    return;
                }

                if (i < 3 && grid[i][j].getValue() == grid[i + 1][j].getValue()) {
                    return;
                }

                if (j > 0 && grid[i][j].getValue() == grid[i][j - 1].getValue()) {
                    return;
                }

                if (j < 3 && grid[i][j].getValue() == grid[i][j + 1].getValue()) {
                    return;
                }

            }
        }
        state = GameState.LOST;
    }

    public void saveGame() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(score + "\n");
            writer.write(highestScore + "\n");
            writer.write(state.toString() + "\n");

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    writer.write(grid[i][j].getValue() + " ");
                }
                writer.newLine();
            }
        }
    }

    public void loadGame() throws IOException {
        File file = new File(FILE_NAME);

        if (!file.exists() || file.length() == 0) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            score = Integer.parseInt(reader.readLine());
            highestScore = Integer.parseInt(reader.readLine());
            state = GameState.valueOf(reader.readLine());

            for (int i = 0; i < 4; i++) {
                String[] values = reader.readLine().trim().split(" ");
                for (int j = 0; j < 4; j++) {
                    grid[i][j].setValue(Integer.parseInt(values[j]));
                }
            }
        }
    }

    private void printGameState() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(grid[i][j].getValue());
                if (j < 3) {
                    System.out.print(" | ");
                }
            }
            if (i < 3) {
                System.out.println("\n--------------");
            }
        }
        System.out.println("\n\nScore: " + score + "\n");
    }

    public int getValue(int r, int c) {
        return grid[r][c].getValue();
    }

    public void setValue(int r, int c, int x) {
        grid[r][c].setValue(x);
    }

    public int getScore() {
        return score;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public GameState getState() {
        return state;
    }

    public Tile[][] getGrid() {
        return grid;
    }

    public static void main(String[] args) {
        TwentyFortyEight game = new TwentyFortyEight();
        game.printGameState();

        int turns = 20;
        while (turns > 0) {
            Scanner scanner = new Scanner(System.in);
            int randomNumber = scanner.nextInt();

            if (randomNumber == 0) {
                game.playTurn(Direction.UP);
            } else if (randomNumber == 1) {
                game.playTurn(Direction.DOWN);
            } else if (randomNumber == 2) {
                game.playTurn(Direction.RIGHT);
            } else {
                game.playTurn(Direction.LEFT);
            }
            turns--;

            game.printGameState();
        }
    }

}

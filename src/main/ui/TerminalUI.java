package ui;

import model.*;
import model.exceptions.MineInTileException;
import model.exceptions.TileOutOfBoundsException;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class TerminalUI {
    GameBoard gameBoard;
    Replay playHistory;
    Scanner sc;
    JsonReader reader;
    JsonWriter writer;
    boolean playing;

    public TerminalUI() {
        runMinesweeper();
    }

    //EFFECTS: Way of validating integer inputs from terminal, bounded with hint message upon
    //         failing to be within bounds
    public int getValidInteger(int lowerBound, int higherBound, String message) {
        int input = sc.nextInt();
        while (input < lowerBound || input > higherBound) {
            System.out.println(message);
            input = sc.nextInt();
        }
        return input;
    }

    public void loadPastGame() {
        System.out.println("Enter File Name, or type 'quit' to go create a new game: ");
        String file = sc.next();

        if (file.equals("quit")) {
            displayBoardCreation();
            return;
        }

        reader = new JsonReader("./data/", file);
        while (true) {
            try {
                playHistory = reader.parseJsonGameState();
                gameBoard = playHistory.listAllTurns().getLast().getBoard();
                break;
            } catch (IOException e) {
                System.out.println("File does not exist");
                loadPastGame();
            }
        }

    }

    public void promptPastGame() {
        System.out.println("Welcome to Minesweeper!");
        System.out.println("Do you want to load a past game?");
        System.out.println("1) Yes");
        System.out.println("2) No");
        int response = getValidInteger(1,2, "Pick 1 or 2");
        if (response == 1) {
            loadPastGame();
        } else {
            displayBoardCreation();
        }

    }

    public void saveGame() {
        System.out.println("What should the file be named?");
        String file = sc.next();
        file += ".json";

        writer = new JsonWriter("./data/", file);
        try {
            writer.openFile();
            writer.saveReplay(playHistory);
            System.out.println("Game Saved!");
        } catch (IOException e) {
            System.out.println("Could not save to file");
        }
    }

    //REQUIRES: user is assumed to always input numbers
    //EFFECTS: initalize the board size and mine amounts
    public void displayBoardCreation() {
        System.out.println("Pick a width for the board: ");
        int width = getValidInteger(1,
                Integer.MAX_VALUE,
                "width must be a positive integer");
        System.out.println("Pick a height for the board: ");
        int height = getValidInteger(1,
                Integer.MAX_VALUE,
                "height must be a positive integer");

        gameBoard = new GameBoard(width, height);
        playHistory = new Replay();

        System.out.println("Pick the number of mines present: ");
        int numOfMines = getValidInteger(1, width * height,
                "must be between " + 1 + " and " + (width * height));
        gameBoard.initalizeMines(numOfMines);
    }

    //EFFECTS: pre-generate a String version of the board
    public List<List<String>> generatePrintBoard() {
        List<List<String>> printBoard = new ArrayList<>();
        for (List<GameTile> row: gameBoard.getBoard()) {
            List<String> printRow = new ArrayList<>();
            for (GameTile tile: row) {
                if (tile.isTileVisible()) {
                    if (tile.getStatus() == GameTile.MINE) {
                        printRow.add("M");
                    } else {
                        printRow.add(String.valueOf(tile.getStatus()));
                    }
                } else {
                    printRow.add("*");
                }
            }
            printBoard.add(printRow);
        }
        return printBoard;
    }

    // EFFECTS: displays the board to terminal, override print generated board
    //          on positions that require a flag
    public void displayBoard() {
        List<int[]> flagCoords = new ArrayList<>();
        if (playHistory.getTurnNumber() != 0) {
            flagCoords = playHistory.listAllTurns().getLast().getFlags();
        }
        List<List<String>> printBoard = generatePrintBoard();

        for (int[] pair: flagCoords) {
            printBoard.get(pair[1]).set(pair[0], "F");
        }

        for (List<String> row: printBoard) {
            for (String tile: row) {
                System.out.print(tile);
                System.out.print(" ");
            }
            System.out.println();
        }

    }

    //EFFECTS: displays the options the player can use to interact
    public void displayPlayOptions() {
        System.out.println("Turn  #" + playHistory.getTurnNumber());
        System.out.println("Pick based on number option: ");
        System.out.println("1) Place Flag");
        System.out.println("2) Open Tile");
        System.out.println("3) View Play Log");
        System.out.println("4) Revert Turn");
        System.out.println("5) save Current Session");
        System.out.println("6) Quit");
    }

    //EFFECTS: Places a flag at user requested position
    //         Per actionState, it will update the list of all flag positions
    public void placeFlag() {
        int[] coords = new int[2];
        System.out.println("pick X-position: ");
        coords[0] = getValidInteger(0, gameBoard.getWidth(),
                "pick between 1 and" + gameBoard.getWidth());
        System.out.println("pick Y-position: ");
        coords[1] = getValidInteger(0, gameBoard.getHeight(),
                "pick between 1 and " + gameBoard.getHeight());
        ActionState newState = new ActionState(coords[0], coords[1]);
        if (playHistory.getTurnNumber() != 0) {
            newState.setFlags(playHistory.listAllTurns().getLast().getFlags());
        }

        boolean result = Player.placeFlag(newState.getFlags(), coords[0], coords[1]);
        if (!result) {
            List<int[]> flags = newState.getFlags();
            for (int[] coord: flags) {
                if (coord[0] == coords[0] && coord[1] == coords[1]) {
                    flags.remove(coord);
                }
            }
            newState.setFlags(flags);
        }
        newState.setBoard(gameBoard);

        playHistory.addPlayState(newState);
    }

    private int[] getPos() {
        System.out.println("pick X-position: ");
        int xpos = getValidInteger(0, gameBoard.getWidth() - 1,
                "pick between 0 and" + (gameBoard.getWidth() - 1));
        System.out.println("pick Y-position: ");
        int ypos = getValidInteger(0, gameBoard.getHeight(),
                "pick between 0 and " + (gameBoard.getHeight() - 1));
        return new int[]{xpos, ypos};
    }

    // EFFECTS: Calls the board to open the tile at a user request position
    //          Updates playhistory
    public void openTile() {
        int[] res = getPos();
        int xpos = res[0];
        int ypos = res[1];
        ActionState newState = new ActionState(xpos, ypos);

        try {
            boolean result = Player.openTile(gameBoard, playHistory.listAllTurns().getLast().getFlags(), xpos, ypos);
            if (result) {
                if (playHistory.getTurnNumber() != 0) {
                    newState.setFlags(playHistory.listAllTurns().getLast().getFlags());
                }
                newState.setBoard(gameBoard);
                playHistory.addPlayState(newState);
            } else {
                System.out.println("Flag exists at that position");
            }
        } catch (MineInTileException e) {
            System.out.println("Oh no, you opened a mine!");
            System.out.println("RESTARTING ......\n");
            runMinesweeper();
        } catch (TileOutOfBoundsException e) {
            System.out.println("Invalid position");
        }
    }

    //EFFECTS: prints the history of player input, such as which tiles they open
    //         and where flags are put
    public void displayPlayLog() {
        System.out.println("----------------------------------");

        ActionState prev = playHistory.listAllTurns().getFirst();
        for (ActionState state: playHistory.listAllTurns()) {
            System.out.println("Turn " + state.getTurnNumber() + ") " + state.playerMove(prev));
            prev = state;
        }

        System.out.println("----------------------------------");
    }

    //EFFECTS: Take user input for interacting with the game
    public void readInput() {
        int playerOption = getValidInteger(0, 7, "Pick between 1 and 6");
        if (playerOption == 1) {
            placeFlag();
        } else if (playerOption == 2) {
            openTile();
        } else if (playerOption == 3) {
            displayPlayLog();
        } else if (playerOption == 4) {
            System.out.println("What turn number would you revert to: ");
            int turnNumber = getValidInteger(1, playHistory.getTurnNumber(),
                    "number must be between 0 and " + playHistory.getTurnNumber());

            ActionState revertedState = playHistory.revertTo(turnNumber);
            gameBoard = revertedState.getBoard();
        } else if (playerOption == 5) {
            saveGame();
        } else if (playerOption == 6) {
            playing = false;
        }
    }

    //EFFECTS: Skeleton method that represents the runtime of the game
    public void runMinesweeper() {
        sc = new Scanner(System.in);
        promptPastGame();
        playing = true;
        while (playing) {
            displayBoard();
            displayPlayOptions();
            readInput();
        }

    }

}

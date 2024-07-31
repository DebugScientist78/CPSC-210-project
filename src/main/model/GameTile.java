package model;

import java.util.List;

/*
 *  GameTile class - Represents the state of a tile on a minesweeper game board
 */

public class GameTile {
    public static final int OPEN = 0;
    public static final int FLAG = 9;
    public static final int MINE = 10;

    private int status;
    private final int xpos;
    private final int ypos;
    private boolean isVisible;

    // EFFECTS: The default tile state is constructed
    public GameTile(int xpos, int ypos) {
        status = OPEN;
        this.xpos = xpos;
        this.ypos = ypos;
        isVisible = false;
    }

    private int getEdge(int index, int maxLength, boolean isBefore) {
        if (isBefore) {
            if (index - 1 < 0) {
                return index;
            } else {
                return index - 1;
            }
        } else {
            if (index + 1 >= maxLength) {
                return index;
            } else {
                return index + 1;
            }
        }
    }

    //REQUIRES: gameBoard must have consistent row and width sizes
    //MODIFIES: this, GameBoard
    //EFFECTS: will automatically change this status
    //         in accordance with minesweeper rules
    //         Assign a number to indicate adjacent mines
    public void updateStatus(GameBoard gameBoard) {
        List<List<GameTile>> board = gameBoard.getBoard();

        final int START_X = getEdge(xpos, gameBoard.getWidth(), true);
        final int START_Y = getEdge(ypos, gameBoard.getHeight(), true);
        final int END_X = getEdge(xpos, gameBoard.getWidth(), false);
        final int END_Y = getEdge(ypos, gameBoard.getHeight(), false);

        int numOfMines = 0;

        if (status == MINE) {
            return;
        }

        for (int i = START_Y; i <= END_Y; i++) {
            for (int j = START_X; j <= END_X; j++) {
                GameTile tile = board.get(i).get(j);
                if (tile.getStatus() == MINE) {
                    numOfMines++;
                }
            }
        }

        this.setStatus(numOfMines);
    }

    //REQUIRES: 0 <= status <= 10
    //MODIFIES: this
    //EFFECTS: changes this tile's status explicitly
    public void setStatus(int status) {
        this.status = status;
    }

    //MODIFIES: this
    //EFFECTS: makes the tile visible for rendering
    public void makeVisible() {
        this.isVisible = true;
    }

    // EFFECTS: gets the status of this tile
    public int getStatus() {
        return this.status;
    }

    public int getXpos() {
        return xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public boolean isTileVisible() {
        return isVisible;
    }

}

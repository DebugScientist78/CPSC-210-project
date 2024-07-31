package model;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;


/*
*  GameBoard class - Handles the minesweeper board, containing its size and appropriate tile states
*  It also handles input requests from the UI
*/

public class GameBoard {
    private final int width;
    private final int height;

    private List<List<GameTile>> board;

    // EFFECTS: Provides the board's size and initialize board
    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;

        board = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            List<GameTile> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                row.add(new GameTile(j,i));
            }
            board.add(row);
        }
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

    //REQUIRES: 0 <= xpos < width
    //          0 <= ypos < height
    //          0 <= newState <= 10
    //MODIFIES: this
    //EFFECTS: Takes a grid position and a state value and updates the board
    //         accordingly
    public void updateBoard(int xpos, int ypos, int newState) {
        GameTile tile = board.get(ypos).get(xpos);
        tile.setStatus(newState);

        if (newState == GameTile.MINE) {
            final int START_X = getEdge(xpos, width, true);
            final int START_Y = getEdge(ypos, height, true);
            final int END_X = getEdge(xpos, width, false);
            final int END_Y = getEdge(ypos, height, false);

            for (int i = START_Y; i <= END_Y; i++) {
                for (int j = START_X; j <= END_X; j++) {
                    if (j != xpos || i != ypos) {
                        getTile(j, i).updateStatus(this);
                    }
                }
            }
        }

    }

    //REQUIRES: 0 <= xpos < width
    //          0 <= ypos < height
    //Modifies: this
    //EFFECTS: makes a given xpos and ypos tile visible, extending to all other
    //         touching tiles that are of Non-MINE state
    //         Returns false if the position is a mine, otherwise true
    public boolean openTile(int xpos, int ypos) {
        if (board.get(ypos).get(xpos).getStatus() == GameTile.MINE) {
            return false;
        }

        board.get(ypos).get(xpos).makeVisible();
        board.get(ypos).get(xpos).updateStatus(this);
        if (board.get(ypos).get(xpos).getStatus() != GameTile.OPEN) {
            return true;
        }

        final int START_X = getEdge(xpos, width, true);
        final int START_Y = getEdge(ypos, height, true);
        final int END_X = getEdge(xpos, width, false);
        final int END_Y = getEdge(ypos, height, false);

        for (int i = START_Y; i <= END_Y; i++) {
            for (int j = START_X; j <= END_X; j++) {
                if (i != xpos || j != ypos) {
                    GameTile tile = board.get(i).get(j);
                    if (!tile.isTileVisible()) {
                        openTile(j,i);
                    }
                }
            }
        }
        return true;
    }

    //REQUIRES: numMines < width * height
    //MODIFIES: this
    //EFFECTS: places a number of Mines in the board at random positions
    public void initalizeMines(int numMines) {
        Random rand = new Random();

        List<int[]> existingMines = new ArrayList<>();

        for (int i = 0; i < numMines; i++) {
            int xpos = rand.nextInt(width);
            int ypos = rand.nextInt(height);

            boolean exists = false;

            for (int[] coord: existingMines) {
                if (coord[0] == xpos && coord[1] == ypos) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                updateBoard(xpos, ypos, GameTile.MINE);
                int[] coords = {xpos, ypos};
                existingMines.add(coords);
            } else {
                i--;
            }
        }
    }

    public List<List<GameTile>> getBoard() {
        return board;
    }

    //REQUIRES:  0 <= xpos < width
    //           0 <= ypos < height
    public GameTile getTile(int xpos, int ypos) {
        return board.get(ypos).get(xpos);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

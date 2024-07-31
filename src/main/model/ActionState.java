package model;

import java.util.ArrayList;
import java.util.List;

/*
*  ActionState class - represents one state of a Minesweeper game, stores information regarding
*                      an instance of player input, board information and turn # (i.e how far in
*                      said minesweeper game)
*/

public class ActionState {

    private GameBoard board;
    private List<int[]> flags;
    private int turnNumber;
    private int selectedX;
    private int selectedY;

    public ActionState(int xpos, int ypos) {
        flags = new ArrayList<>();
        board = null;
        selectedX = xpos;
        selectedY = ypos;
    }

    //EFFECTS: Provides a sentence describing what the player had performed
    public String playerMove(ActionState previous) {
        if (board == null) {
            return "Start of the game";
        }

        if (flags.size() < previous.getFlags().size()) {
            int size = previous.getFlags().size();
            int[] coord = previous.getFlags().get(size - 1);

            return "Flag removed at: " + coord[0] + ", " + coord[1];
        } else if (flags.size() > previous.getFlags().size()) {
            int size = flags.size();
            int[] coord = flags.get(size - 1);

            return "Flag added to: " + coord[0] + ", " + coord[1];
        }

        return "Position: " + selectedX + ", " + selectedY
                + " was opened";
    }

    //EFFECT: make copy of board for individual reference
    public void setBoard(GameBoard board) {
        GameBoard copy = new GameBoard(board.getWidth(), board.getHeight());
        for (List<GameTile> row: board.getBoard()) {
            for (GameTile tile: row) {
                copy.updateBoard(tile.getXpos(), tile.getYpos(), tile.getStatus());
                if (tile.isTileVisible()) {
                    copy.getTile(tile.getXpos(), tile.getYpos()).makeVisible();
                }
            }
        }

        this.board = copy;
    }

    //EFFECTS: Makes a unique copy of the board and returns it
    public GameBoard getBoard() {
        GameBoard copy = new GameBoard(board.getWidth(), board.getHeight());
        for (List<GameTile> row: board.getBoard()) {
            for (GameTile tile: row) {
                copy.updateBoard(tile.getXpos(), tile.getYpos(), tile.getStatus());
                if (tile.isTileVisible()) {
                    copy.getTile(tile.getXpos(), tile.getYpos()).makeVisible();
                }
            }
        }
        return copy;
    }

    //REQUIRES: int[] must be size 2
    //EFFECTS: sets the list of flag coordinates for this specifc ActionState
    //         instance
    public void setFlags(List<int[]> flags) {
        List<int[]> copy = new ArrayList<>();

        for (int[] item: flags) {
            copy.add(item);
        }

        this.flags = copy;
    }

    public List<int[]> getFlags() {
        return flags;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int getSelectedX() {
        return selectedX;
    }

    public int getSelectedY() {
        return selectedY;
    }
}

package model;

/*
* Static Class that provides functionality for the solver
*/

import model.exceptions.MineInTileException;
import model.exceptions.TileOutOfBoundsException;

import java.util.*;

public class BoardSolver {

    //EFFECTS: check if all non-mine tiles are open
    private static boolean isBaseCase(GameBoard board) {
        for (List<GameTile> row: board.getBoard()) {
            for (GameTile tile: row) {
                if (tile.getStatus() != GameTile.MINE && !tile.isTileVisible()) {
                    return false;
                }
            }
        }
        return true;
    }

    //REQUIRES: 0 <= xpos < replay.getBoard().getWidth()
    //          0 <= ypos < replay.getBoard().getHeight()
    //MODIFIES: replay
    //EFFECTS: tries to solve the board, returns true if solved, returns false if non solved
    public static boolean solve(Replay replay, int xpos, int ypos) {
        List<List<Integer>> todo = new ArrayList<>();
        GameBoard board = replay.listAllTurns().getLast().getBoard();

        for (List<GameTile> row: board.getBoard()) {
            for (GameTile tile: row) {
                if (!tile.isTileVisible() && !(tile.getXpos() == xpos && tile.getYpos() == ypos)) {
                    List<Integer> c = new ArrayList<>();
                    c.add(tile.getXpos());
                    c.add(tile.getYpos());
                    todo.add(c);
                }
            }
        }

        return backtrack(replay, xpos, ypos, todo);
    }

    //EFFECTS: tries to open a tile
    private static boolean play(Replay replay, int xpos, int ypos) {
        try {
            GameBoard board = replay.listAllTurns().getLast().getBoard();
            boolean res = Player.openTile(board,
                    replay.listAllTurns().getLast().getFlags(), xpos, ypos);
            if (!res) {
                return false;
            } else {
                ActionState newState = new ActionState(xpos, ypos);
                newState.setFlags(replay.listAllTurns().getLast().getFlags());
                newState.setBoard(board);
                replay.addPlayState(newState);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    //REQUIRES: 0 <= xpos < replay.getBoard().getWidth()
    //          0 <= ypos < replay.getBoard().getHeight()
    //MODIFIES: replay
    //EFFECTS: returns true if valid/complete, returns false if invalid action
    private static boolean backtrack(Replay replay, int xpos, int ypos, List<List<Integer>> todo) {
        //base case
        GameBoard board = replay.listAllTurns().getLast().getBoard();
        if (board.getTile(xpos, ypos).isTileVisible()) {
            return false;
        }

        if (!play(replay, xpos, ypos)) {
            return false;
        }

        board = replay.listAllTurns().getLast().getBoard();
        if (isBaseCase(board)) {
            return true;
        } else {
            for (List<Integer> coord: todo) {
                List<List<Integer>> newTodo = new ArrayList<>();
                newTodo.addAll(todo);
                newTodo.remove(coord);
                if (backtrack(replay, coord.get(0), coord.get(1), newTodo)) {
                    return true;
                }
            }
            return false;
        }
    }
}

package model;

/*
* Static class that acts as a utility middle ground between UI and the rest of the model classes
*/

import model.exceptions.MineInTileException;
import model.exceptions.TileOutOfBoundsException;

import java.util.*;

public class Player {

    //REQUIRES: flags must contain int[] of only size 2
    //MODIFIES: gameBoard
    //EFFECTS: opens a tile in a given gameboard,
    //         throws TileOutOfBoundsException first if given desiredX and desiredY are out of bounds
    //         throws MineInTileException if there is a mine at that spot
    //         Returns false if a flag exists already at that spot
    //         Returns true otherwise, signifying successful opening
    public static boolean openTile(GameBoard gameBoard, List<int[]> flags, int desiredX, int desiredY)
            throws MineInTileException, TileOutOfBoundsException {
        if (desiredX < 0 || desiredX > gameBoard.getWidth()) {
            throw new TileOutOfBoundsException();
        }

        if (desiredY < 0 || desiredY > gameBoard.getHeight()) {
            throw new TileOutOfBoundsException();
        }

        if (gameBoard.getTile(desiredX, desiredY).getStatus() == GameTile.MINE) {
            EventLog.getInstance().logEvent(new Event(
                    "Mine hit at: " + desiredX + ", " + desiredY
            ));
            throw new MineInTileException();
        }

        for (int[] coords: flags) {
            if (coords[0] == desiredX && coords[1] == desiredY) {
                return false;
            }
        }
        EventLog.getInstance().logEvent(new Event(
                "Tile opened at: " + desiredX + ", " + desiredY
        ));
        gameBoard.openTile(desiredX, desiredY);
        return true;
    }

    //REQUIRES: the int[] in flags must be size 2
    //MODIFIES: flags
    //EFFECTS: Adds flag to given list of flags and returns true if it does not already exist
    //         If position already exists in the List, return false
    public static boolean placeFlag(List<int[]> flags, int desiredX, int desiredY) {
        for (int[] coords: flags) {
            if (coords[0] == desiredX && coords[1] == desiredY) {
                EventLog.getInstance().logEvent(
                        new Event("Flag removed at: " + desiredX + ", " + desiredY));
                return false;
            }
        }
        int[] coord = {desiredX, desiredY};
        flags.add(coord);
        EventLog.getInstance().logEvent(
                new Event("Flag added at: " + desiredX + ", " + desiredY));
        return true;
    }
}

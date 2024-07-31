package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTileTest {
    GameTile tile;
    GameBoard gameBoard;
    GameBoard gameBoardOneByOne;

    @BeforeEach
    public void setup() {
        tile = new GameTile(0,0);
        gameBoard = new GameBoard(3,3);
        gameBoardOneByOne = new GameBoard(1,1);
    }

    @Test
    public void testFirstConstructor() {
        tile = new GameTile(0,0);
        assertEquals(0, tile.getXpos());
        assertEquals(0, tile.getYpos());
        assertEquals(GameTile.OPEN, tile.getStatus());
        assertFalse(tile.isTileVisible());
    }

    @Test
    public void testSetStatus() {
        tile.setStatus(GameTile.MINE);

        assertEquals(GameTile.MINE, tile.getStatus());
    }

    @Test
    public void testSetStatusTwo() {
        tile.setStatus(2);

        assertEquals(2, tile.getStatus());
    }

    @Test
    public void testUpdateStatusNoMines() {
        tile = gameBoard.getBoard().get(1).get(1);
        tile.updateStatus(gameBoard);

        assertEquals(0, tile.getStatus());
    }

    @Test
    public void testUpdateStatusOneByOne() {
        tile = gameBoardOneByOne.getBoard().get(0).get(0);
        tile.updateStatus(gameBoardOneByOne);

        assertEquals(0, tile.getStatus());
    }

    @Test
    public void testUpdateStatusSomeMines() {
        List<List<GameTile>> board;
        board = gameBoard.getBoard();

        board.get(0).get(0).setStatus(GameTile.MINE);
        assertEquals(GameTile.MINE, board.get(0).get(0).getStatus());

        board.get(0).get(2).setStatus(GameTile.MINE);
        assertEquals(GameTile.MINE, board.get(0).get(2).getStatus());

        board.get(1).get(0).setStatus(GameTile.MINE);
        assertEquals(GameTile.MINE, board.get(1).get(0).getStatus());

        tile = board.get(1).get(1);
        tile.updateStatus(gameBoard);

        assertEquals(3, tile.getStatus());
    }

    @Test
    public void testMakeVisible() {
        assertFalse(tile.isTileVisible());
        tile.makeVisible();
        assertTrue(tile.isTileVisible());
    }
}

package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    GameBoard oneByOne;
    GameBoard twoByTwo;
    GameBoard threeByThree;
    GameBoard gameBoard;

    @BeforeEach
    public void setup() {
        oneByOne = new GameBoard(1,1);
        twoByTwo = new GameBoard(2,2);
        threeByThree = new GameBoard(3,3);
        gameBoard = new GameBoard(4, 2);
    }

    @Test
    public void testConstructor() {
        assertEquals(1, oneByOne.getWidth());
        assertEquals(1, oneByOne.getHeight());
        assertEquals(1, oneByOne.getBoard().size());
        assertEquals(1, oneByOne.getBoard().get(0).size());

        assertEquals(2, twoByTwo.getWidth());
        assertEquals(2, twoByTwo.getHeight());
        assertEquals(2, twoByTwo.getBoard().size());
        assertEquals(2, twoByTwo.getBoard().get(0).size());

        assertEquals(4, gameBoard.getWidth());
        assertEquals(2, gameBoard.getHeight());
        assertEquals(2, gameBoard.getBoard().size());
        assertEquals(4, gameBoard.getBoard().get(0).size());
    }

    @Test
    public void testUpdateBoardOneNotMine() {
        oneByOne.updateBoard(0,0,1);
        assertEquals(1, oneByOne.getTile(0,0).getStatus());
    }

    @Test
    public void testUpdateBoardOne() {
        oneByOne.updateBoard(0,0,GameTile.MINE);
        assertEquals(GameTile.MINE, oneByOne.getTile(0,0).getStatus());
    }

    @Test
    public void testUpdateBoardTwo() {
        twoByTwo.updateBoard(0,0,GameTile.MINE);
        assertEquals(GameTile.MINE, twoByTwo.getTile(0,0).getStatus());

        List<List<GameTile>> board = twoByTwo.getBoard();
        assertEquals(1, board.get(0).get(1).getStatus());
        assertEquals(1, board.get(1).get(1).getStatus());
        assertEquals(1, board.get(1).get(0).getStatus());
    }

    @Test
    public void testUpdateBoard() {
        gameBoard.updateBoard(2,1,GameTile.MINE);
        assertEquals(GameTile.MINE, gameBoard.getBoard().get(1).get(2).getStatus());

        List<List<GameTile>> board = gameBoard.getBoard();
        assertEquals(1, board.get(1).get(1).getStatus());
        assertEquals(1, board.get(1).get(3).getStatus());
        assertEquals(1, board.get(0).get(2).getStatus());
        assertEquals(1, board.get(0).get(1).getStatus());
        assertEquals(1, board.get(0).get(3).getStatus());

        assertEquals(0, board.get(0).get(0).getStatus());
        assertEquals(0, board.get(1).get(0).getStatus());
    }

    @Test
    public void testOpenTileBaseCase() {
        GameTile tile = oneByOne.getTile(0,0);
        tile.setStatus(GameTile.MINE);

        assertFalse(oneByOne.openTile(0,0));
        assertFalse(tile.isTileVisible());
    }

    @Test
    public void testOpenTileBaseCaseOpen() {
        GameTile tile = oneByOne.getTile(0,0);
        tile.setStatus(GameTile.OPEN);

        assertTrue(oneByOne.openTile(0,0));
        assertTrue(tile.isTileVisible());
    }

    @Test
    public void testOpenTileOneLevel() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                GameTile tile = threeByThree.getTile(i,j);
                if (i == 1 && j == 1) {
                    tile.setStatus(GameTile.OPEN);
                } else {
                    tile.setStatus(GameTile.MINE);
                }
            }
        }

        GameTile center = threeByThree.getTile(1,1);

        assertTrue(threeByThree.openTile(1,1));
        assertTrue(center.isTileVisible());

    }

    @Test
    public void testOpenTileNoMinesTwo() {
        assertTrue(twoByTwo.openTile(0,0));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                GameTile tile = twoByTwo.getTile(i,j);
                assertTrue(tile.isTileVisible());
                assertEquals(GameTile.OPEN, tile.getStatus());
            }
        }
    }

    @Test
    public void testOpenTile() {
        gameBoard.getTile(3,0).setStatus(GameTile.MINE);
        gameBoard.getTile(3,1).setStatus(GameTile.MINE);

        assertTrue(gameBoard.openTile(0,0));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 1; j++) {
                assertTrue(gameBoard.getTile(i,j).isTileVisible());
            }
        }
        assertEquals(2, gameBoard.getTile(2,0).getStatus());
        assertEquals(2, gameBoard.getTile(2,1).getStatus());
    }

    @Test
    public void testInitializeMines() {
        gameBoard.initalizeMines(7);

        int count = 0;
        for (int i = 0; i < gameBoard.getWidth(); i++) {
            for (int j = 0; j <  gameBoard.getHeight(); j++) {
                if (gameBoard.getTile(i,j).getStatus() == GameTile.MINE) {
                    count++;
                }
            }
        }
        assertEquals(7, count);
    }
}
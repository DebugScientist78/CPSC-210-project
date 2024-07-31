package model;

import model.exceptions.MineInTileException;
import model.exceptions.TileOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    GameBoard oneByOne;
    GameBoard twoByTwo;

    @BeforeEach
    public void setup() {
        Player player = new Player();
        oneByOne = new GameBoard(1,1);
        twoByTwo = new GameBoard(2,2);
    }

    @Test
    public void testInvalidPositionOpen() {
        try {
            Player.openTile(oneByOne, null, -1, 0);
            fail("nope");
        } catch (MineInTileException e) {
            fail("wrong exception");
        } catch (TileOutOfBoundsException e) {
            //pass
        }
    }

    @Test
    public void testInvalidPositionOpenTwo() {
        try {
            Player.openTile(oneByOne, null, 2, 1);
            fail("nope");
        } catch (MineInTileException e) {
            fail("wrong exception");
        } catch (TileOutOfBoundsException e) {
            //pass
        }
    }

    @Test
    public void testInvalidPositionOpenThree() {
        try {
            Player.openTile(oneByOne, null, 0, -1);
            fail("nope");
        } catch (MineInTileException e) {
            fail("wrong exception");
        } catch (TileOutOfBoundsException e) {
            //pass
        }
    }

    @Test
    public void testInvalidPositionOpenFour() {
        try {
            Player.openTile(oneByOne, null, 1, 10);
            fail("nope");
        } catch (MineInTileException e) {
            fail("wrong exception");
        } catch (TileOutOfBoundsException e) {
            //pass
        }
    }

    @Test
    public void testInvalidMineOpen() {
        twoByTwo.initalizeMines(3);
        int x = 0;
        int y = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (twoByTwo.getTile(i, j).getStatus() == GameTile.MINE) {
                    x = i;
                    y = j;
                    break;
                }
            }
        }

        try {
            Player.openTile(twoByTwo, null, x, y);
            fail("nope");
        } catch (MineInTileException e) {
            //pass
        } catch (TileOutOfBoundsException e) {
            fail("wrong exception");
        }
    }

    @Test
    public void testFlagExitsOpen() {
        List<int[]> flags = new ArrayList<>();
        flags.add(new int[]{0,0});

        try {
            assertFalse(Player.openTile(oneByOne, flags, 0, 0));
        } catch (Exception e) {
            fail("nope");
        }
    }

    @Test
    public void testFlagExitsOpenTwo() {
        List<int[]> flags = new ArrayList<>();
        flags.add(new int[]{0,0});

        try {
            assertTrue(Player.openTile(twoByTwo, flags, 0, 1));
        } catch (Exception e) {
            fail("nope");
        }
    }

    @Test
    public void testOpen() {
        List<int[]> flags = new ArrayList<>();
        flags.add(new int[]{2,2});
        try {
            assertTrue(Player.openTile(twoByTwo, flags, 0, 0));
        } catch (Exception e) {
            fail("nope");
        }
    }

    @Test
    public void flagAlreadyExist() {
        List<int[]> flags = new ArrayList<>();
        flags.add(new int[]{0,0});
        assertFalse(Player.placeFlag(flags, 0 ,0));
    }

    @Test
    public void addFlag() {
        List<int[]> flags = new ArrayList<>();
        flags.add(new int[]{0,0});
        assertTrue(Player.placeFlag(flags, 1 ,0));
    }

    @Test
    public void addFlagTwo() {
        List<int[]> flags = new ArrayList<>();
        flags.add(new int[]{0,0});
        assertTrue(Player.placeFlag(flags, 0 ,1));
    }
}

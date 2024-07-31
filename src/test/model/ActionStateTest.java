package model;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class ActionStateTest {
    ActionState first;
    ActionState second;
    ActionState third;
    GameBoard board;

    @BeforeEach
    public void setup() {
        board = new GameBoard(3,3);
        first = new ActionState(0,0);
        first.setTurnNumber(1);

        board.initalizeMines(2);

        second = new ActionState(0,0);
        second.setTurnNumber(2);
        board.openTile(0,0);
        second.setBoard(board);

        third = new ActionState(1,1);
        third.setTurnNumber(3);
        List<int[]> flags = new ArrayList<>();
        int[] flag = {1,1};
        flags.add(flag);
        third.setFlags(flags);
        third.setBoard(board);
    }

    @Test
    public void testTurnNumber() {
        assertEquals(1, first.getTurnNumber());
        assertEquals(2, second.getTurnNumber());
        assertEquals(3, third.getTurnNumber());
    }

    @Test
    public void testSetBoard() {
        first.setBoard(board);
        assertFalse(first.getBoard() == board);
    }

    @Test
    public void testPlayerMove() {
        assertEquals("Start of the game", first.playerMove(second));
        assertEquals("Start of the game", first.playerMove(third));
    }

    @Test
    public void testPlayerMoveFlag() {
        assertEquals(1, third.getSelectedX());
        assertEquals(1, third.getSelectedY());
        assertEquals("Flag added to: 1, 1", third.playerMove(second));
    }

    @Test
    public void testPlayerMoveFlagTwo() {
        assertEquals(0, second.getSelectedX());
        assertEquals(0, second.getSelectedY());
        assertEquals("Flag removed at: 1, 1", second.playerMove(third));
    }

    @Test
    public void testPlayerMoveOpen() {
        third.setFlags(new ArrayList<>());
        assertEquals("Position: 1, 1 was opened", third.playerMove(second));
    }

    @Test
    public void getBoard() {
        board = new GameBoard(3,3);
        board.getTile(1,1).makeVisible();
        first.setBoard(board);

        assertFalse(first.getBoard() == board);
    }
}

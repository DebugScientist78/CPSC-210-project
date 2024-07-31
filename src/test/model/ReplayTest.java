package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReplayTest {
    Replay replay;
    GameBoard board;
    ActionState one;
    ActionState two;
    ActionState three;

    @BeforeEach
    public void setup() {
        board = new GameBoard(3, 3);
        replay = new Replay();
        one = new ActionState(0,0);
        one.setBoard(board);

        two = new ActionState(2, 1);
        board.updateBoard(2,1,GameTile.MINE);
        two.setBoard(board);

        three = new ActionState(2,2);
        board.openTile(2,2);
        three.setBoard(board);
    }

    @Test
    public void testConstructor() {
        assertEquals(0, replay.getTurnNumber());
    }

    @Test
    public void testAddOnce() {
        replay.addPlayState(one);
        assertEquals(1, replay.getTurnNumber());
        assertEquals(1, replay.listAllTurns().size());
        assertEquals(1, one.getTurnNumber());
    }

    @Test
    public void testAddMutliple() {
        replay.addPlayState(one);
        assertEquals(1, replay.getTurnNumber());
        assertEquals(1, replay.listAllTurns().size());
        assertEquals(1, one.getTurnNumber());

        replay.addPlayState(two);
        assertEquals(2, replay.getTurnNumber());
        assertEquals(2, replay.listAllTurns().size());
        assertEquals(2, two.getTurnNumber());

        replay.addPlayState(three);
        assertEquals(3, replay.getTurnNumber());
        assertEquals(3, replay.listAllTurns().size());
        assertEquals(3, three.getTurnNumber());
    }

    @Test
    public void testRevertOnce() {
        replay.addPlayState(one);
        replay.addPlayState(two);
        replay.addPlayState(three);

        assertEquals(two,replay.revertTo(2));
        assertEquals(2, replay.getTurnNumber());
        assertEquals(2, replay.listAllTurns().size());
    }

    @Test
    public void testRevertMultiple() {
        replay.addPlayState(one);
        replay.addPlayState(two);
        replay.addPlayState(three);

        assertEquals(two,replay.revertTo(2));
        assertEquals(2, replay.getTurnNumber());
        assertEquals(2, replay.listAllTurns().size());

        assertEquals(one,replay.revertTo(1));
        assertEquals(1, replay.getTurnNumber());
        assertEquals(1, replay.listAllTurns().size());
    }

    @Test
    public void testRevertLarge() {
        replay.addPlayState(one);
        replay.addPlayState(two);
        replay.addPlayState(three);

        assertEquals(one,replay.revertTo(1));
        assertEquals(1, replay.getTurnNumber());
        assertEquals(1, replay.listAllTurns().size());
    }
}

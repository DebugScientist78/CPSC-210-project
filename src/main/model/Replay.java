package model;

/*
*  Replay class - Tracks one minesweeper game instance, List of ActionState's
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Replay {
    private LinkedList<ActionState> playHistory;
    private int turnNumber;

    public Replay() {
        playHistory = new LinkedList<>();
        turnNumber = 0;
    }

    //MODIFIES: this, state
    //EFFECTS: adds an ActionState to list of plays recorded
    public void addPlayState(ActionState state) {
        playHistory.addLast(state);
        turnNumber++;
        state.setTurnNumber(turnNumber);
    }

    //REQUIRES: 0 <= turnNumber < this.turnNumber
    //MODIFIES: this
    //EFFECTS: removes actionStates from playHistory till the
    //         given turnNumber
    public ActionState revertTo(int turnNumber) {
        while (this.turnNumber > turnNumber) {
            playHistory.removeLast();
            this.turnNumber--;
        }
        return playHistory.getLast();
    }


    //EFFECTS: produces a list of all the player's turns taken currently
    public LinkedList<ActionState> listAllTurns() {
        LinkedList<ActionState> listOfTurns = new LinkedList<>();
        listOfTurns.addAll(playHistory);
        return listOfTurns;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    //EFFECTS: helper -> returns an independent copy of the playHistory list of Action States
    private List<ActionState> getPlayList() {
        List<ActionState> unmodifiable = List.copyOf(playHistory);
        List<ActionState> list = new ArrayList<>();

        for (ActionState as: unmodifiable) {
            list.add(as);
        }

        return list;
    }

    public List<ActionState> getUnmodifiableList() {
        EventLog.getInstance().logEvent(new Event(
                "View All ActionStates in Replay"
        ));
        return List.copyOf(playHistory);
    }

    //EFFECTS: grabs utmost the 3 most recent actionStates
    public List<ActionState> getRecent() {
        List<ActionState> list = getPlayList();
        Collections.reverse(list);
        List<ActionState> result = new LinkedList<>();

        int count = 1;
        for (ActionState as: list) {
            if (count > 3) {
                break;
            }
            result.add(as);
            count++;
        }
        Collections.reverse(result);
        //System.out.println(result.size());
        EventLog.getInstance().logEvent(new Event(
                "View 3 most recent ActionStates in Replay"
        ));
        return result;
    }

    //EFFECTS: grabs all turns that involve a flag input
    public List<ActionState> getAllFlag() {
        List<ActionState> list = getPlayList();
        List<ActionState> result = new ArrayList<>();

        ActionState prev = list.get(0);
        for (ActionState as: list) {
            if (as.playerMove(prev).contains("Flag")) {
                result.add(as);
            }
            prev = as;
        }
        EventLog.getInstance().logEvent(new Event(
                "View all Flag related ActionStates in Replay"
        ));
        return result;
    }

    //EFFECTS: grabs all actionStates that involve opening a tile
    public List<ActionState> getAllOpen() {
        List<ActionState> list = getPlayList();
        List<ActionState> result = new ArrayList<>();

        ActionState prev = list.get(0);
        for (ActionState as: list) {
            if (as.playerMove(prev).contains("Position")) {
                result.add(as);
            }
            prev = as;
        }
        EventLog.getInstance().logEvent(new Event(
                "View all tile opening related ActionStates in Replay"
        ));
        return result;
    }
}

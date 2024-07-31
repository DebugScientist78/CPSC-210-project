package persistence;

import model.*;
import org.json.*;
import java.util.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/*
* JsonReader class, reads and interprets the JSON data,
* Code Citation taken from: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
* As inspiration and base model for I/O JSON interaction
*/

public class JsonReader extends FileHandler {
    private JSONArray jsonArray;

    public JsonReader(String path, String fileName) {
        super(path, fileName);
        jsonObj = new JSONObject();
    }


    // EFFECTS: reads .json file and returns string version
    // CODE CITATION:
    // https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/persistence/JsonReader.java
    // LINES: 26 - 30
    private String openData() throws IOException {
        StringBuilder rawJson = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source + fileName), StandardCharsets.UTF_8)) {
            stream.forEach(s -> rawJson.append(s));
        }

        return rawJson.toString();
    }


    @Override
    protected JSONObject toJson() throws IOException {
        try {
            String rawJson = openData();
            return new JSONObject(rawJson);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    //EFFECTS: produces a gameBoard from a "board_state" JSONArray
    private GameBoard getBoard() {
        int width = jsonObj.getInt("width");
        int height = jsonObj.getInt("height");

        GameBoard gameBoard = new GameBoard(width, height);
        JSONArray rawBoard = jsonObj.getJSONArray("board_state");
        for (int i = 0; i < rawBoard.length(); i++) {
            JSONArray row = rawBoard.getJSONArray(i);
            for (int j = 0; j < row.length(); j++) {
                if (row.get(j).toString().equals("M")) {
                    gameBoard.getTile(j, i).setStatus(GameTile.MINE);
                } else {
                    gameBoard.getTile(j, i).setStatus(row.getInt(j));
                }
            }
        }
        return gameBoard;
    }

    // EFFECTS: Gets the list of flag coordinates from the JSON file, given the appropriate JSON field
    private List<int[]> getFlagCoords(JSONObject currentState) {
        List<int[]> flagList = new ArrayList<>();

        for (Object coordsObj: currentState.getJSONArray("list_flags")) {
            int[] coord = new int[2];
            JSONArray coords = (JSONArray) coordsObj;
            coord[0] = coords.getInt(0);
            coord[1] = coords.getInt(1);
            flagList.add(coord);
        }
        return flagList;
    }

    //EFFECTS: reads the visible_tiles field of the JSON file to set visibility to the
    //         corresponding tiles
    private void readBoardVisibility(GameBoard board, JSONObject jsonVisible) {
        for (int x = 0; x < jsonVisible.getJSONArray("visible_tiles").length(); x++) {
            JSONArray row = jsonVisible.getJSONArray("visible_tiles").getJSONArray(x);
            for (int y = 0; y < row.length(); y++) {
                if (row.getInt(y) == 1) {
                    board.getTile(y, x).makeVisible();
                }
            }
        }
    }

    //
    private ActionState getActionState(JSONObject stateJson)  {
        int xpos = stateJson.getInt("last_selected_x");
        int ypos = stateJson.getInt("last_selected_y");

        int turnNumber = stateJson.getInt("turn_number");

        ActionState state = new ActionState(xpos, ypos);
        state.setTurnNumber(turnNumber);

        List<int[]> flagList = getFlagCoords(stateJson);

        state.setFlags(flagList);
        GameBoard board = getBoard();
        readBoardVisibility(board, stateJson);
        state.setBoard(board);

        return state;
    }

    public Replay parseJsonGameState() throws IOException {
        this.jsonObj = toJson();

        Replay replay = new Replay();
        JSONArray log = jsonObj.getJSONArray("log");

        for (int i = 0; i < log.length(); i++) {
            replay.addPlayState(getActionState(log.getJSONObject(i)));
        }

        //System.out.println(jsonObj.getJSONObject("current_state").toString(4));
        replay.addPlayState(getActionState(jsonObj.getJSONObject("current_state")));

        return replay;
    }
}

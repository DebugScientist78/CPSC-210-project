package persistence;

import java.io.*;
import java.util.List;

import model.ActionState;
import model.GameBoard;
import model.GameTile;
import model.Replay;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * JsonWriter class, writes and converts models to JSON data,
 * Code Citation taken from: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
 * As inspiration and base model for I/O JSON interaction
 */

public class JsonWriter extends FileHandler {
    private Replay replay;
    private PrintWriter writer;
    private JSONObject jsonObj;

    public JsonWriter(String path, String fileName) {
        super(path, fileName);
    }


    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file fails
    //          to give permissions
    // CODE CITATION:
    // https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/persistence/JsonWriter.java
    // LINES: 19-24
    public void openFile() throws IOException {
        File file = new File(source, fileName);
        writer = new PrintWriter(file);
    }

    // MODIFIES: this
    // EFFECTS: converts a given replay to JSONObject and saves it to file
    public void saveReplay(Replay replay) throws IOException {
        this.replay = replay;
        writer = new PrintWriter(new File(source + fileName));

        jsonObj = toJson();

        writer.print(toJson().toString(4));
        writer.close();
    }

    // EFFECTS: takes GameBoard and creates JSONArray from it
    private JSONArray generateBoard() {
        JSONArray board = new JSONArray();
        GameBoard gameBoard = replay.listAllTurns().getLast().getBoard();
        for (List<GameTile> row: gameBoard.getBoard()) {
            JSONArray jsonRow = new JSONArray();
            for (GameTile tile: row) {
                if (tile.getStatus() == GameTile.MINE) {
                    jsonRow.put("M");
                } else {
                    jsonRow.put(tile.getStatus());
                }
            }
            board.put(jsonRow);
        }
        return board;
    }

    // EFFECTS: takes a list of flag coordinates and produces a JSON array
    private JSONArray convertFlagsToJsonArray(List<int[]> flags) {
        JSONArray jsonArr = new JSONArray();

        for (int i = 0; i < flags.size(); i++) {
            JSONArray row = new JSONArray(flags.get(i));
            jsonArr.put(row);
        }

        return jsonArr;
    }

    // EFFECTS: takes a GameBoard and produces a corresponding binary 2D
    //          array that indicates visibility
    private JSONArray getVisibleTiles(GameBoard board) {
        JSONArray jsonArr = new JSONArray();

        for (int i = 0; i < board.getHeight(); i++) {
            JSONArray row = new JSONArray();
            for (int j = 0; j < board.getWidth(); j++) {
                if (board.getTile(i,j).isTileVisible()) {
                    row.put(j, 1);
                } else {
                    row.put(j, 0);
                }
            }
            jsonArr.put(row);
        }

        return jsonArr;
    }

    // EFFECTS: generate jSONObject for a given actionState
    private JSONObject generateActionState(ActionState actionState) {
        JSONObject jsonState = new JSONObject();

        jsonState.put("turn_number", actionState.getTurnNumber());
        jsonState.put("last_selected_x", actionState.getSelectedX());
        jsonState.put("last_selected_y", actionState.getSelectedY());
        jsonState.put("list_flags", convertFlagsToJsonArray(actionState.getFlags()));
        jsonState.put("visible_tiles", getVisibleTiles(actionState.getBoard()));

        return jsonState;
    }

    // EFFECTS: writes the JSON object and deletes any empty Replays
    @Override
    protected JSONObject toJson() throws IOException {
        jsonObj = new JSONObject();

        if (replay.listAllTurns().isEmpty()) {
            File file = new File(source, fileName);
            file.delete();
            throw new IOException();
        }

        ActionState currentState = replay.listAllTurns().getLast();

        jsonObj.put("width", currentState.getBoard().getWidth());
        jsonObj.put("height", currentState.getBoard().getHeight());
        jsonObj.put("board_state", generateBoard());

        jsonObj.put("current_state", generateActionState(replay.listAllTurns().getLast()));

        JSONArray log = new JSONArray();
        for (int i = 0; i < replay.listAllTurns().size() - 1; i++) {
            log.put(i, generateActionState(replay.listAllTurns().get(i)));
        }
        jsonObj.put("log", log);

        return jsonObj;
    }

}

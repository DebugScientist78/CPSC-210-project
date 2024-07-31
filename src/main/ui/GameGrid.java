package ui;

import model.GameBoard;
import model.GameTile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

public class GameGrid {
    private GameBoard board;
    private List<int[]> flags;
    private GridLayout layout;
    private JPanel panel;
    private int maxSize;
    private int unitSize;


    //EFFECTS: in addition to instantiation, also setup size per unit tile
    public GameGrid(GameBoard board, List<int[]> flags, int maxSize) {
        this.board = board;
        this.flags = flags;
        this.maxSize = maxSize;
        layout = new GridLayout(board.getHeight(), board.getWidth());
        panel = new JPanel(layout);
        panel.setLayout(layout);
        unitSize = calculateUnitSize(board.getHeight(), board.getWidth());
        panel.setPreferredSize(new Dimension(
                unitSize * board.getWidth(),
                unitSize * board.getHeight()));
    }

    //EFFECTS: Determines the unit size to render a tile given a board's dimensions
    private int calculateUnitSize(int width, int height) {
        int length = min(width, height);
        return maxSize / length;
    }

    public void setFlags(List<int[]> flags) {
        this.flags = flags;
    }

    public void setBoard(GameBoard board) {
        this.board = board;
    }

    //EFFECTS: generates a Integer 2D array that reflects the AssetHolder asset Mapping
    private List<List<Integer>> generateBoard() {
        List<List<Integer>> printBoard = new ArrayList<>();

        for (List<GameTile> row: board.getBoard()) {
            List<Integer> r = new ArrayList<>();
            for (GameTile tile: row) {
                if (tile.isTileVisible()) {
                    r.add(tile.getStatus());
                } else {
                    r.add(-1);
                }
            }
            printBoard.add(r);
        }

        for (int[] coord: flags) {
            printBoard.get(coord[1]).set(coord[0], 9);
        }

        return printBoard;
    }

    //EFFECTS: wipes and reassigns corresponding JLabels which act as tile iamges, to a grid panel
    //        https://docs.oracle.com/javase/tutorial/uiswing/layout/grid.html
    public void renderBoard() {
        panel.removeAll();
        Map<Integer, ImageIcon> imageMap = AssetHolder.getTileImages();

        List<List<Integer>> printBoard = generateBoard();
        for (List<Integer> row: printBoard) {
            for (Integer tile: row) {
                panel.add(new JLabel(
                        AssetHolder.scaleImageIcon(imageMap.get(tile), unitSize, unitSize)
                ));
            }
        }
    }

    public int getCalculatedWidth() {
        return unitSize * board.getWidth();
    }

    public int getCalculatedHeight() {
        return unitSize * board.getHeight();
    }

    public JPanel getPanel() {
        return panel;
    }
}

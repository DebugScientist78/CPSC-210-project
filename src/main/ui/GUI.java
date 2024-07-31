package ui;

import model.*;
import model.Event;
import model.exceptions.MineInTileException;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;
import java.util.List;

import static java.lang.System.exit;

public class GUI {
    public static final int GAME_WIDTH = 1600;
    public static final int GAME_HEIGHT = 900;

    private static volatile boolean playing;
    private static boolean restarted;
    private static int inputCount;

    private static GameBoard gameBoard;
    private static Replay playHistory;

    private static JsonReader reader;
    private static JsonWriter writer;

    private static JTextField widthInput;
    private static JTextField heightInput;
    private static JTextField minesInput;
    private static Window setup;

    private static JTextField positionXInput;
    private static JTextField positionYInput;
    private static JTextField revertInput;

    private static GameGrid currentBoard;
    private static JLabel turnNum;
    private static ScrollPane textHistoryPane;
    private static JInternalFrame textFrame;

    private static Window view;
    private static Window main;

    private static JFileChooser fileChooser;

    private static boolean checkValidInteger(int num, int lowerBound, int higherBound) {
        return (num > lowerBound && num < higherBound);
    }

    public GUI() {
        restarted = false;
        AssetHolder.setup();
        runGame();
        //prompt();

    }

    //EFFECTS: setup for the Main window components
    private static void setupMainWindow() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.addComponent(new Panel(currentBoard.getPanel(),0,0));
        scrollPane.setScrollSize(GAME_HEIGHT, GAME_HEIGHT - 40);
        scrollPane.setPanelSize(currentBoard.getCalculatedWidth(), currentBoard.getCalculatedHeight());
        scrollPane.render();

        JInternalFrame inF = makeInternalFrame("Board", GAME_HEIGHT, GAME_HEIGHT - 40);
        inF.add(scrollPane.getScrollPane());

        textFrame = makeInternalFrame("Text Only - Play History",
                GAME_WIDTH - GAME_HEIGHT, (GAME_HEIGHT / 2) - 30);
        textHistoryPane = new ScrollPane();
        textHistoryPane.setScrollSize(GAME_WIDTH - GAME_HEIGHT, (GAME_HEIGHT / 2) - 30);
        textHistoryPane.setPanelSize(GAME_WIDTH - GAME_HEIGHT, (GAME_HEIGHT / 2) - 30);
        textHistoryPane.render();

        textFrame.add(textHistoryPane.getScrollPane());

        main.addComponent(new Panel(inF,0,0));
        main.addComponent(new Panel(new JLabel("test"), 810, 0));
        JPanel userInput = userInput();
        userInput.setPreferredSize(new Dimension(GAME_WIDTH - GAME_HEIGHT, GAME_HEIGHT / 2));
        main.addComponent(new Panel(userInput, GAME_HEIGHT, 0));
        main.addComponent(new Panel(textFrame, GAME_HEIGHT, (GAME_HEIGHT / 2) - 10));

        main.getWindow().setJMenuBar(saveAndLoad());
        main.setSize(GAME_WIDTH,GAME_HEIGHT);
        main.render();
    }

    //EFFECTS: starts the game and initializes it
    public static void runGame() {
        inputCount = 0;
        playing = false;
        if (!restarted) {
            prompt();
        } else {
            setup.render();
        }
        while (!playing) {
            Thread.onSpinWait();
            //do nothing
        }

        main = new Window("Minesweeper");
        main.getWindow().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        main.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                printLog();
            }
        });
        List<int[]> flags = new ArrayList<>();
        currentBoard = new GameGrid(gameBoard, flags, GAME_HEIGHT);
        currentBoard.renderBoard();
        setupMainWindow();
    }

    // EFFECTS: updates the text History for a given actionState
    private static void updateText(ActionState as) {
        inputCount++;
        turnNum.setText(Integer.toString(as.getTurnNumber()));
        ActionState state = as;
        ActionState prev;
        if (as.getTurnNumber() <= 1) {
            prev = new ActionState(state.getSelectedX(), state.getSelectedY());
        } else {
            prev = playHistory.listAllTurns().get(as.getTurnNumber() - 2);
        }

        textHistoryPane.addComponent(new Panel(
                new JLabel("Turn " + state.getTurnNumber() + ") " + state.playerMove(prev)),
                0,
                20 * (inputCount - 1)));
        textHistoryPane.setPanelSize(GAME_WIDTH - GAME_HEIGHT, 20 * inputCount);
        textHistoryPane.render();
        textFrame.revalidate();
        textFrame.repaint();
    }

    //EFFECTS: whenever an action is taken, the text frame regarding turns taken, updates,
    // as well, the gameboard and its grid for the latest action taken
    static void update() {
        currentBoard.setBoard(gameBoard);
        currentBoard.setFlags(playHistory.listAllTurns().getLast().getFlags());
        currentBoard.renderBoard();

        updateText(playHistory.listAllTurns().getLast());
    }

    // EFFECTS: helper -> grabs and throws exception for invalid positions from input fields
    static int[] getPos() throws Exception {
        int positionX = Integer.parseInt(positionXInput.getText());
        if (!checkValidInteger(positionX, -1, gameBoard.getWidth())) {
            throw new Exception("Invalid X position");
        }
        int positionY = Integer.parseInt(positionYInput.getText());
        if (!checkValidInteger(positionY, -1, gameBoard.getHeight())) {
            throw new Exception("Invalid Y Position");
        }
        return new int[]{positionX, positionY};
    }

    //EFFECTS: helper -> removes flag from an actionState given a 2D position
    private static List<int[]> removeFlag(ActionState newState, int positionX, int positionY) {
        List<int[]> flags = new ArrayList<>();
        for (int[] coord: newState.getFlags()) {
            if (coord[0] == positionX && coord[1] == positionY) {
                continue;
            }
            flags.add(coord);
        }
        return flags;
    }

    //EFFECTS: player action -> player can place/remove a flag on a given position. If a the flag already exists at
    // the requested position, remove it
    static void placeFlag() {
        try {
            int[] cords = getPos();
            int positionX = cords[0];
            int positionY = cords[1];

            ActionState newState = new ActionState(positionX, positionY);
            if (playHistory.getTurnNumber() != 0) {
                newState.setFlags(List.copyOf(playHistory.listAllTurns().getLast().getFlags()));
            }

            boolean result = Player.placeFlag(newState.getFlags(), positionX, positionY);

            if (!result) {
                newState.setFlags(removeFlag(newState, positionX, positionY));
            } else {
                newState.setFlags(List.copyOf(newState.getFlags()));
            }
            newState.setBoard(gameBoard);

            playHistory.addPlayState(newState);
            update();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //EFFECTS: player action -> Allows players to remove some actionStates by reverting the game state to an earlier
    //          iteration
    static void revertTurn() {
        try {
            int turn = Integer.parseInt(revertInput.getText());
            if (!checkValidInteger(turn, 0, playHistory.getTurnNumber())) {
                throw new Exception("Invalid Turn");
            }

            ActionState revertedState = playHistory.revertTo(turn);
            gameBoard = revertedState.getBoard();
            update();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void printLog() {
        for (Event event: EventLog.getInstance()) {
            System.out.println(event.toString());
        }
        exit(0);
    }

    //EFFECTS: upon a mine being opened, a pop up tells the player to close, as they must rerun the program for a new
    // instance
    static void gameOver() {
        main.getWindow().setVisible(false);
        view = new Window("Game Over");
        view.addComponent(new Panel(new JLabel("You hit mine :( \n Restart the program to try again"),
                0,0));
        view.setSize(400, 400);
        view.render();

        view.getWindow().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        view.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                printLog();
            }
        });
    }

    //EFFECTS: player action -> game will attempt to open a mine at the requested position
    static void openTile() {
        try {
            int[] cords = getPos();
            int positionX = cords[0];
            int positionY = cords[1];

            ActionState newState = new ActionState(positionX, positionY);
            if (playHistory.getTurnNumber() != 0) {
                newState.setFlags(List.copyOf(playHistory.listAllTurns().getLast().getFlags()));
            }
            boolean result = Player.openTile(gameBoard,
                    newState.getFlags(),
                    positionX, positionY);
            if (result) {
                newState.setBoard(gameBoard);
                playHistory.addPlayState(newState);
            }
            update();
        } catch (MineInTileException e) {
            //pop up, closes
            gameOver();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //EFFECTS: helper -> in addition to making an internal frame, initialize size
    static JInternalFrame makeInternalFrame(String name, int width, int height)  {
        JInternalFrame frame = new JInternalFrame(name,
                false, false, false, false);
        frame.setVisible(true);
        frame.setPreferredSize(new Dimension(width, height));
        return frame;
    }

    //EFFECTS: ActionListener -> retrieves inputs and initializes the game board and playHistory
    public static void setupBoard() {
        try {
            int width = Integer.parseInt(widthInput.getText());
            if (!checkValidInteger(width, 0, Integer.MAX_VALUE)) {
                throw new Exception("Invalid Width");
            }
            int height = Integer.parseInt(heightInput.getText());
            if (!checkValidInteger(height, 0, Integer.MAX_VALUE)) {
                throw new Exception("Invalid Height");
            }
            int numMines = Integer.parseInt(minesInput.getText());
            if (!checkValidInteger(numMines, 0, width * height)) {
                throw new Exception("Invalid number of mines");
            }

            gameBoard = new GameBoard(width, height);
            playHistory = new Replay();
            gameBoard.initalizeMines(numMines);
            playing = true;
            System.out.println("Board created");

            setup.getWindow().setVisible(false);
        } catch (Exception e) {
            System.out.println(e);
        }

    }


    //EFFECTS: sets the behaviour for the window showing all the turns
    private static void setupPopup() {
        view.getWindow().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        view.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                main.getWindow().setVisible(true);
                view.getWindow().dispose();
            }
        });
    }

    // EFFECTS: generic approach to rendering and showing a list of actionStates
    private static void view(List<ActionState> list, String name) {
        main.getWindow().setVisible(false);
        view = new Window(name);
        view.disableEndOnClose();
        setupPopup();

        ScrollPane scrollPane = new ScrollPane();

        int count = 0;
        for (ActionState as: list) {
            GameGrid grid = new GameGrid(as.getBoard(), as.getFlags(), 400);
            grid.renderBoard();

            scrollPane.addComponent(new Panel(new JLabel(Integer.toString(as.getTurnNumber())),
                    0, count * 420));
            scrollPane.addComponent(new Panel(grid.getPanel(), 0, (count * 420) + 20));
            count++;
        }

        scrollPane.setPanelSize(currentBoard.getCalculatedWidth(), count * 420);
        scrollPane.setScrollSize(400, 800);
        scrollPane.render();

        view.addComponent(new Panel(scrollPane.getScrollPane(), 0,0));
        view.setSize(410,830);
        view.render();
    }

    public static void viewRecent() {
        view(playHistory.getRecent(), "Last 3 Turns");
    }

    public static void viewFlag() {
        view(playHistory.getAllFlag(), "View All Flag related Turns");
    }

    public static void viewOpen() {
        view(playHistory.getAllOpen(), "View All Open related Turns");
    }

    public static void viewAll() {
        view(playHistory.getUnmodifiableList(), "View All turns");
    }

    //EFFECTS: helper -> simplifies placing components for GridBagLayout
    // https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
    static void placeItem(GridBagConstraints c, int x, int y, JPanel panel, JComponent component) {
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 50;
        c.gridx = x;
        c.gridy = y;
        c.weightx = 0.2;
        //c.weighty = 0.1;
        panel.add(component, c);
    }

    // EFFECTS: places user action inputs onto panel for main window
    static void placeUserActions(JPanel panel, GridBagConstraints constraints) {
        JLabel turnTxt = new JLabel("Turn #:");
        placeItem(constraints, 0, 0, panel, turnTxt);

        turnNum = new JLabel("0");
        placeItem(constraints, 1, 0, panel, turnNum);

        Button flagBtn = new Button("Place/Remove Flag");
        flagBtn.setAction("placeFlag");
        placeItem(constraints, 0, 1, panel, flagBtn.getButton());

        Button openBtn = new Button("Open Tile");
        openBtn.setAction("openTile");
        placeItem(constraints, 1, 1, panel, openBtn.getButton());

        positionXInput = new JTextField();
        placeItem(constraints, 0,2, panel, positionXInput);

        positionYInput = new JTextField();
        placeItem(constraints, 1, 2, panel, positionYInput);

        Button revertBtn = new Button("Revert to Turn: ");
        revertBtn.setAction("revertTurn");
        placeItem(constraints, 0,3, panel, revertBtn.getButton());

        revertInput = new JTextField();
        placeItem(constraints, 1,3, panel, revertInput);
    }

    //EFFECTS: sets up the user input section of the GUI
    static JPanel userInput() {

        JPanel panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        placeUserActions(panel, constraints);

        Button viewRecent = new Button("View last 3 Turns");
        viewRecent.setAction("viewRecent");
        placeItem(constraints, 0, 4, panel, viewRecent.getButton());

        Button viewOpen = new Button("View all Turns that opens a tile");
        viewOpen.setAction("viewOpen");
        placeItem(constraints, 1,4, panel, viewOpen.getButton());

        Button viewFlag = new Button("View all Turns that involves a flag");
        viewFlag.setAction("viewFlag");
        placeItem(constraints, 0, 5, panel, viewFlag.getButton());

        Button viewAll = new Button("View all");
        viewAll.setAction("viewAll");
        placeItem(constraints, 1, 5, panel, viewAll.getButton());

        return panel;
    }

    //EFFECTS: actionListener -> saves current game to a file view file explorer
    // https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
    static void saveFile() {
        System.out.println("Save");
        int returnCode = fileChooser.showSaveDialog(new JFrame("something"));

        if (returnCode == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //System.out.println(file.getParent());
            writer = new JsonWriter(file.getParent() + "/", file.getName());
            try {
                writer.openFile();
                writer.saveReplay(playHistory);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    //EFFECTS: actionListener -> opens a file and loads into the GUI
    // https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
    static void openFile() {
        System.out.println("Open");
        int returnCode = fileChooser.showOpenDialog(new JFrame("something"));

        if (returnCode == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            reader = new JsonReader(file.getParent() + "/", file.getName());
            try {
                playHistory = reader.parseJsonGameState();
                gameBoard = playHistory.listAllTurns().getLast().getBoard();
                currentBoard = new GameGrid(gameBoard,
                        playHistory.listAllTurns().getLast().getFlags(),
                        GAME_HEIGHT);
                currentBoard.renderBoard();
                main.getWindow().dispose();
                main = new Window("Minesweeper");
                setupMainWindow();
                update();
            } catch (Exception e) {
                //pass
            }
        }
    }

    //EFFECTS: helper -> handles displaying all turns taken in the solver
    private static void solveUpdate(int index) {
        for (ActionState as : playHistory.listAllTurns()) {
            if (as.getBoard().getTile(as.getSelectedX(),
                    as.getSelectedY()).getStatus() == GameTile.MINE) {
                playHistory.listAllTurns().remove(as);
            }
        }

        for (ActionState as: playHistory.listAllTurns()) {
            if (as.getTurnNumber() < playHistory.getTurnNumber() && as.getTurnNumber() > index) {
                updateText(as);
                //System.out.println(as.getTurnNumber());
            }
        }
    }

    //EFFECTS: implements the solve feature, solves the board and updates playHistory
    static void solve() {
        int latestTurn = playHistory.getTurnNumber() + 1;

        for (List<GameTile> row: gameBoard.getBoard()) {
            for (GameTile tile: row) {
                if (!tile.isTileVisible()) {
                    ActionState newState = new ActionState(tile.getXpos(), tile.getYpos());
                    newState.setBoard(gameBoard);
                    playHistory.addPlayState(newState);
                    BoardSolver.solve(playHistory, tile.getXpos(), tile.getYpos());
                }
            }
        }
        gameBoard = playHistory.listAllTurns().getLast().getBoard();
        solveUpdate(latestTurn);
        update();
    }

    //EFFECTS: menu bar that contains: save, load and solve buttons
    static JMenuBar saveAndLoad() {
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_ESCAPE);
        bar.add(menu);
        JMenuItem saveItem = new JMenuItem("Save",
                new ImageIcon("./data/Assets/save.png"));

        saveItem.addActionListener(e -> saveFile());

        JMenuItem openItem = new JMenuItem("Open",
                new ImageIcon("./data/Assets/load.png"));

        openItem.addActionListener(e -> openFile());

        JMenuItem solveItem = new JMenuItem("Solve");
        solveItem.addActionListener(e -> solve());

        menu.add(saveItem);
        menu.add(openItem);
        menu.add(solveItem);

        fileChooser = new JFileChooser();

        return bar;
    }

    //EFFECTS: first window that prompts for a game creation
    static void prompt() {
        setup = new Window("Setup - Minesweeper");
        setup.disableEndOnClose();

        widthInput = new JTextField();
        heightInput = new JTextField();
        minesInput = new JTextField();

        GridLayout layout = new GridLayout(4, 2);
        JPanel panel = new JPanel();
        panel.setLayout(layout);

        panel.add(new JLabel("Board Width"));
        panel.add(widthInput);

        panel.add(new JLabel("Board Height"));
        panel.add(heightInput);

        panel.add(new JLabel("Number of Mines"));
        panel.add(minesInput);

        Button button = new Button("ok");
        button.setSize(200,50);
        button.setAction("initializeGame");
        panel.add(button.getButton());

        setup.addComponent(new Panel(panel, 0, 0));
        setup.setSize(400,400);
        setup.render();
    }

}

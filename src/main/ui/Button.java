package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Button implements ActionListener {
    private String text;
    private String action;
    private int width;
    private int height;
    private JButton button;

    public Button(String text) {
        this.text = text;
        button = new JButton(text);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        button.setPreferredSize(new Dimension(this.width, this.height));
    }

    //EFFECTS: assigns an action listener command
    public void setAction(String action) {
        this.action = action;
        button.setActionCommand(this.action);
        button.addActionListener(this);
    }

    public JButton getButton() {
        return button;
    }

    //EFFECTS: checks a particular command to signal GUI to perform
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand() == "initializeGame") {
            GUI.setupBoard();
        } else if (actionEvent.getActionCommand() == "placeFlag") {
            GUI.placeFlag();
        } else if (actionEvent.getActionCommand() == "revertTurn") {
            GUI.revertTurn();
        } else if (actionEvent.getActionCommand() == "openTile") {
            GUI.openTile();
        } else if (actionEvent.getActionCommand() == "viewRecent") {
            GUI.viewRecent();
        } else if (actionEvent.getActionCommand() == "viewOpen") {
            GUI.viewOpen();
        } else if (actionEvent.getActionCommand() == "viewFlag") {
            GUI.viewFlag();
        } else if (actionEvent.getActionCommand() == "viewAll") {
            GUI.viewAll();
        }
    }
}

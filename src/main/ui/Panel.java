package ui;

import javax.swing.*;

// Panel classes simply acts as a placeholder for information regarding a panel's data and placement on some supposed
// content plane

public class Panel  {
    private JComponent panel;
    private int positionX;
    private int positionY;

    public Panel(JComponent panel, int positionX, int positionY) {
        this.panel = panel;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public JComponent getPanel() {
        return panel;
    }
}

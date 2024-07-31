package ui;

import javax.swing.*;
import java.awt.*;

public class Window extends ContentPane {
    private JFrame rawWindow;
    private String title;
    private int width;
    private int height;
    //private List<Panel> componentList;


    public Window(String title) {
        super();
        this.title = title;
        rawWindow = new JFrame(title);
        rawWindow.setResizable(false);
        width = 500;
        height = 500;
        rawWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;

        rawWindow.setPreferredSize(new Dimension(width, height));
        rawWindow.pack();
    }

    //EFFECTS: disables the window from terminating the program upon a particular windowing being closed
    public void disableEndOnClose() {
        rawWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void addComponent(Panel item) {
        componentList.add(item);
    }

    public JFrame getWindow() {
        return rawWindow;
    }

    //EFFECTS: places all components and prepares for rendering
    public void render() {
        container = rawWindow.getContentPane();
        compile();
        rawWindow.pack();
        rawWindow.revalidate();
        rawWindow.repaint();
        rawWindow.setVisible(true);
    }
}

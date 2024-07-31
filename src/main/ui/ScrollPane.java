package ui;

import javax.swing.*;
import java.awt.*;


// https://docs.oracle.com/javase/tutorial/uiswing/components/scrollpane.html
public class ScrollPane extends ContentPane {
    private JScrollPane scrollPane;
    private JPanel panel;

    public ScrollPane() {
        super();
        panel = new JPanel();
        panel.setOpaque(true);
        scrollPane = new JScrollPane(panel);
    }

    public void addComponent(Panel item) {
        componentList.add(item);
    }

    //EFFECTS: sets the content panel size, for which all content added should be bounded by
    public void setPanelSize(int width, int height) {
        panel.setPreferredSize(new Dimension(width, height));
    }

    //EFFECTS: sets the viewable size for the user.
    public void setScrollSize(int width, int height) {
        scrollPane.setPreferredSize(new Dimension(width, height));
    }


    ///EFFECTS: takes a panel and places it onto a JScrollPane that enables a scrollable content pane
    public void render() {
        container = panel;
        compile();
        scrollPane.setViewportView(panel);
        //scrollPane.setOpaque(true);
        //scrollPane.setVisible(true);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}

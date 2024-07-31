package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ContentPane {
    protected List<Panel> componentList;
    protected Container container;

    public ContentPane() {
        componentList = new ArrayList<>();
    }

    protected abstract void addComponent(Panel item);

    //EFFECTS: takes all components in componentList and places them in their respective positions via a
    // SpringLayout Manager (https://docs.oracle.com/javase/tutorial/uiswing/layout/spring.html)
    public void compile() {
        SpringLayout layout = new SpringLayout();
        container.setLayout(layout);
        for (Panel component: componentList) {
            container.add(component.getPanel());
            layout.putConstraint(SpringLayout.WEST,
                    component.getPanel(),
                    component.getPositionX(),
                    SpringLayout.WEST,
                    container);
            layout.putConstraint(SpringLayout.NORTH,
                    component.getPanel(),
                    component.getPositionY(),
                    SpringLayout.WEST,
                    container);
        }
    }
}

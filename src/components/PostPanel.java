/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a graphical canvas for system components
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class PostPanel extends JPanel {

    //Attributes
    private ArrayList<Object> jcomponents;

    //Default Constructors, Initialize all System components
    public PostPanel() {
        jcomponents = new ArrayList<>();
        setBackground(Color.white);
    }

    /**
     * <p>draw all graphical components on the canvas</p>
     *
     * @param g (Graphics object).
     * @since 1.1
     */
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        for (Object component : jcomponents) {
            if (component instanceof TruckGUI)
                ((TruckGUI) component).draw(g);

            if (component instanceof BranchGUI)
                ((BranchGUI) component).draw(g);

            if (component instanceof PackageGUI)
                ((PackageGUI) component).draw(g);
        }
    }

    /**
     * <p>get array list of all the graphical components</p>
     *
     * @return jcomponents (array list).
     * @since 1.1
     */
    protected ArrayList<Object> getJcomponents() {
        return jcomponents;
    }

    /**
     * <p>search for graphical branch component in the array list</p>
     *
     * @param (index,jcomponents)
     * @return BranchGUI (BranchGUI component).
     * @since 1.1
     */
    protected static BranchGUI getBranch(int index, ArrayList jcomponents) {
        for (Object item : jcomponents) {
            if (item instanceof BranchGUI) {
                if (((BranchGUI) item).getId() == index)
                    return (BranchGUI) item;
            }
        }
        return null;
    }
}

/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a graphical canvas for system components
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class PostPanel extends JPanel implements Cloneable {

    //Attributes
    private CopyOnWriteArrayList<Object> jcomponents;

    //Default Constructors, Initialize all System components
    public PostPanel() {
        this.jcomponents = new CopyOnWriteArrayList<>();
        this.setBackground(Color.white);
    }

    @Override
    protected PostPanel clone() throws CloneNotSupportedException {
        PostPanel tempPostPanel = null;
        try {
            tempPostPanel = (PostPanel) super.clone();
            CopyOnWriteArrayList<Object> componentsArray = new CopyOnWriteArrayList<>();
            for (Object item : jcomponents) {
                if (item instanceof TruckGUI)
                    componentsArray.add(((TruckGUI) item).clone());
                if (item instanceof BranchGUI)
                    componentsArray.add(((BranchGUI) item).clone());
                if (item instanceof PackageGUI)
                    componentsArray.add(((PackageGUI) item).clone());
                if (item instanceof LineGUI)
                    componentsArray.add(((LineGUI) item).clone());
            }
            tempPostPanel.jcomponents = componentsArray;
        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning PostPanel object!");
        }
        return tempPostPanel;
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
        synchronized (this) {
            for (Object component : jcomponents) {
                if (component instanceof TruckGUI)
                    ((TruckGUI) component).draw(g);

                if (component instanceof BranchGUI)
                    ((BranchGUI) component).draw(g);

                if (component instanceof PackageGUI)
                    ((PackageGUI) component).draw(g);
            }
        }
    }

    /**
     * <p>get array list of all the graphical components</p>
     *
     * @return jcomponents (array list).
     * @since 1.1
     */
    protected CopyOnWriteArrayList<Object> getJcomponents() {
        return jcomponents;
    }

    @Deprecated
    /*
     * <p>search for graphical branch component in the array list</p>
     *
     * @param (index,jcomponents)
     * @return BranchGUI (BranchGUI component).
     * @since 1.1
     * @deprecated since 1.2
     */
    protected static BranchGUI getBranch(int index, CopyOnWriteArrayList<Object> jcomponents) {
        for (Object item : jcomponents) {
            if (item instanceof BranchGUI) {
                if (((BranchGUI) item).getId() == index)
                    return (BranchGUI) item;
            }
        }
        return null;
    }

    public void setJcomponents(CopyOnWriteArrayList<Object> jcomponents) {
        this.jcomponents = jcomponents;
    }
}

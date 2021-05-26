/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Represent a local branch Graphics.
 * Maintains the colors and the location of the graphical object in real time
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class BranchGUI extends JComponent {

    //Statics
    private static int UID = 0;
    private final static int width = 40;
    private final static int height = 30;
    private static final Color LIGHT_BLUE = new Color(51, 204, 255);
    private static final Color DARK_BLUE = new Color(0, 58, 146);
    private static final Color VERY_DARK_GREEN = new Color(0, 102, 0);

    //Attributes
    private int id;
    private Color color;
    private LineGUI pointTo;
    private boolean isHUB;

    //Constructors
    public BranchGUI(int x, int y) {
        isHUB = false;
        this.id = UID++;
        setLocation(x, y);
        color = LIGHT_BLUE;
    }

    public BranchGUI(boolean isHUB) {
        this.isHUB = isHUB;
        this.id = UID++;
        setLocation(1140, 200);
        color = LIGHT_BLUE;
    }

    /**
     * <p>draw branch component</p>
     *
     * @param g (Graphics object).
     * @since 1.1
     */
    protected void draw(Graphics g) {
        if (isHUB) {
            g.setColor(VERY_DARK_GREEN);
            g.fillRect(1140, 200, 40, 200);
        } else {
            g.setColor(color);
            g.fillRect(getX(), getY(), width, height);

            g.setColor(VERY_DARK_GREEN);
            g.drawLine(pointTo.getStartX(), pointTo.getStartY(), pointTo.getEndX(), pointTo.getEndY());
        }
    }

    /**
     * <p>change the color of the branch</p>
     *
     * @param color (name of color).
     * @since 1.1
     */
    protected void changeColor(String color) {
        if (color.compareTo("LIGHT_BLUE") == 0)
            this.color = LIGHT_BLUE;
        if (color.compareTo("DARK_BLUE") == 0)
            this.color = DARK_BLUE;
    }

    /**
     * <p>reset the number of branches</p>
     * this method allows to change the number of graphical branches infinite times before the system runs.
     *
     * @since 1.1
     */
    protected static void resetID() {
        UID = 1;
    }

    /**
     * <p>return the id of the graphical component</p>
     *
     * @return (branch GUI id)
     * @since 1.1
     */
    protected int getId() {
        return this.id;
    }

    /**
     * <p>return the Line Gui between the branch and the hub</p>
     *
     * @return pointTo (Line GUI)
     * @since 1.1
     */
    public LineGUI getPointTo() {
        return pointTo;
    }

    /**
     * <p>Set the Line Gui between the branch and the hub</p>
     *
     * @param pointTo pointTo (Line GUI)
     * @since 1.1
     */
    public void setPointTo(LineGUI pointTo) {
        this.pointTo = pointTo;
    }
}
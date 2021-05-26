/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents a graphical component of truck
 *
 * @author Liran Smadja, Tamar Aminov
 */


public class TruckGUI extends JComponent {

    //Statics
    private static final Color LIGHT_GREEN = new Color(74, 255, 58, 255);
    private static final Color DARK_GREEN = new Color(64, 156, 64);
    private static final Color LIGHT_RED = new Color(246, 152, 152);
    private static final Color DARK_RED = new Color(243, 6, 6);

    //Attributes
    private Color color;
    private int truckSize;
    private int wheelSize;
    private int numPackages;
    private Branch startBranch, endBranch;

    //Constructors
    public TruckGUI(String type, int x, int y) {
        setName(type);
        setLocation(x, y);
        this.truckSize = 16;
        this.wheelSize = 10;
        this.numPackages = 0;
        if (type.compareTo("Van") == 0)
            color = Color.BLUE;
        if (type.compareTo("StandardTruck") == 0)
            color = LIGHT_GREEN;
        if (type.compareTo("NonStandardTruck") == 0)
            color = LIGHT_RED;
    }

    /**
     * <p>draw wheels for truck</p>
     *
     * @param g (Graphics object).
     * @since 1.1
     */
    private void paintWheels(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval(getX() - 3, getY() - 4, wheelSize, wheelSize);
        g.setColor(Color.BLACK);
        g.fillOval(getX() + 9, getY() + 9, wheelSize, wheelSize);
        g.setColor(Color.BLACK);
        g.fillOval(getX() + 9, getY() - 4, wheelSize, wheelSize);
        g.setColor(Color.BLACK);
        g.fillOval(getX() - 3, getY() + 9, wheelSize, wheelSize);
    }

    /**
     * <p>draw type of truck</p>
     *
     * @param g (Graphics object).
     * @since 1.1
     */
    protected void draw(Graphics g) {
        if (getName().compareTo("StandardTruck") == 0)
            if (numPackages != 0)
                g.drawString(String.valueOf(numPackages), getX() + 5, getY() - 4);
        g.setColor(color);
        g.fillRect(getX(), getY(), truckSize, truckSize);
        paintWheels(g);
    }

    /**
     * <p>change the color of the branch</p>
     *
     * @param color (name of color).
     * @since 1.1
     */
    protected void changeColor(String color) {
        if (color.compareTo("LIGHT_GREEN") == 0)
            this.color = LIGHT_GREEN;
        if (color.compareTo("DARK_GREEN") == 0)
            this.color = DARK_GREEN;
        if (color.compareTo("LIGHT_RED") == 0)
            this.color = LIGHT_RED;
        if (color.compareTo("DARK_RED") == 0)
            this.color = DARK_RED;
    }

    /**
     * <p>make truck component visible/invisible</p>
     *
     * @since 1.1
     */
    protected void visible(boolean status) {
        if (status) {
            this.truckSize = 16;
            this.wheelSize = 10;
        } else {
            this.truckSize = 0;
            this.wheelSize = 0;
        }
    }

    /**
     * <p>Set the branch which the truck is starting from</p>
     *
     * @param startBranch (Branch object).
     * @since 1.1
     */
    public void setStartBranch(Branch startBranch) {
        this.startBranch = startBranch;
    }

    /**
     * <p>Set the branch which the truck is ending from</p>
     *
     * @param endBranch (Branch object).
     * @since 1.1
     */
    public void setEndBranch(Branch endBranch) {
        this.endBranch = endBranch;
    }

    /**
     * <p>Set the number of packages</p>
     *
     * @param numPackages (int value).
     * @since 1.1
     */
    public void setNumPackages(int numPackages) {
        this.numPackages = numPackages;
    }

}
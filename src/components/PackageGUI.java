/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import javax.swing.*;
import java.awt.*;

/**
 * Represents red circle which will use as a package graphical component.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class PackageGUI extends JComponent implements Cloneable{

    //Statics
    private static final Color LIGHT_RED = new Color(246, 152, 152);
    private static final Color DARK_RED = new Color(243, 6, 6);

    //Attributes
    private Color color;
    private int radiusSize;
    private LineGUI pointTo;

    //Constructors
    public PackageGUI(int x, int y) {
        this.setLocation(x, y);
        this.color = LIGHT_RED;
        this.radiusSize = 30;

    }

    @Override
    protected PackageGUI clone() throws CloneNotSupportedException {
        PackageGUI tempPackageGUI = null;
        try {
            tempPackageGUI = (PackageGUI) super.clone();
            tempPackageGUI.color = this.color;
            tempPackageGUI.radiusSize = this.radiusSize;
            tempPackageGUI.pointTo = this.pointTo.clone();
        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning PackageGUI object!");
        }
        return tempPackageGUI;
    }

    /**
     * <p>draw package component</p>
     *
     * @param g (Graphics object).
     * @since 1.1
     */
    protected void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(getX(), getY(), radiusSize, radiusSize);

        g.setColor(pointTo.getColor());
        g.drawLine(pointTo.getStartX(), pointTo.getStartY(), pointTo.getEndX(), pointTo.getEndY());
    }

    /**
     * <p>change the color of the package</p>
     *
     * @param color (name of color).
     * @since 1.1
     */
    protected void changeColor(String color) {
        if (color.compareTo("LIGHT_RED") == 0)
            this.color = LIGHT_RED;
        if (color.compareTo("DARK_RED") == 0)
            this.color = DARK_RED;
    }

    /**
     * <p>return the Line Gui between the branch and the package</p>
     *
     * @return pointTo (package GUI)
     * @since 1.1
     */
    protected LineGUI getPointTo() {
        return this.pointTo;
    }

    /**
     * <p>Set the Line Gui between the branch and the package</p>
     *
     * @param pointTo pointTo (package GUI)
     * @since 1.1
     */
    protected void setPointTo(LineGUI pointTo) {
        this.pointTo = pointTo;
    }
}
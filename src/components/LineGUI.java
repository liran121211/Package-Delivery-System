/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Represent a line between 2 graphical components Graphics.
 * The line can be between a local branch and a package
 * The line can be between a local branch and the HUB
 * The line can be between Hub to package and NonStandardTruck to package
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class LineGUI extends JComponent {

    //Attributes
    private Color color;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    //Constructors
    public LineGUI(Color color, int startX, int startY, int endX, int endY) {
        this.color = color;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * <p>Get line color</p>
     *
     * @return color of line (Jcomponent)
     * @since 1.1
     */
    protected Color getColor() {
        return color;
    }

    /**
     * <p>Get line start x</p>
     *
     * @return line start x position (Jcomponent)
     * @since 1.1
     */
    protected int getStartX() {
        return startX;
    }

    /**
     * <p>Get line start y</p>
     *
     * @return line start y position (Jcomponent)
     * @since 1.1
     */
    protected int getStartY() {
        return startY;
    }

    /**
     * <p>Get line end x</p>
     *
     * @return line end x position (Jcomponent)
     * @since 1.1
     */
    protected int getEndX() {
        return endX;
    }

    /**
     * <p>Get line end y</p>
     *
     * @return line end y position (Jcomponent)
     * @since 1.1
     */
    protected int getEndY() {
        return endY;
    }

}
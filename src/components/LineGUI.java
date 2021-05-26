/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import javax.swing.*;
import java.awt.Color;

/**
 * Represent a line between 2 graphical components Graphics.
 * The line can be between a local branch and a package
 * The line can be between a local branch and the HUB
 * The line can be between Hub to package and NonStandardTruck to package
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class LineGUI extends JComponent implements Cloneable {

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

    /*
     * <p>Start thread activity</p>
     *
     * @since 1.1
     */
    @Override
    protected LineGUI clone() throws CloneNotSupportedException {
        LineGUI tempLineGUI = null;
        try {
            tempLineGUI = (LineGUI) super.clone();
            tempLineGUI.color = this.color;
            tempLineGUI.startX = this.startX;
            tempLineGUI.startY = this.startY;
            tempLineGUI.endX = this.endX;
            tempLineGUI.endY = this.endY;
        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning LineGUI object!");
        }
        return tempLineGUI;
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

    /**
     * <p>Get line end y</p>
     *
     * @param startX position (Jcomponent)
     * @since 1.2
     */
    protected void setStartX(int startX) {
        this.startX = startX;
    }

    /**
     * <p>Get line end y</p>
     *
     * @param startY position (Jcomponent)
     * @since 1.2
     */
    protected void setStartY(int startY) {
        this.startY = startY;
    }

    /**
     * <p>Get line end y</p>
     *
     * @param endX position (Jcomponent)
     * @since 1.2
     */
    protected void setEndX(int endX) {
        this.endX = endX;
    }

    /**
     * <p>Get line end y</p>
     *
     * @param endY position (Jcomponent)
     * @since 1.2
     */
    protected void setEndY(int endY) {
        this.endY = endY;
    }
}
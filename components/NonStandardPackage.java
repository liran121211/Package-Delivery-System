/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

/**
 * Represents the non-standard size packages.
 * All packages of this type are transported between clients only.
 * @author Liran Smadja
 */
public class NonStandardPackage extends Package {

    //Attributes
    private int width;
    private int length;
    private int height;

    //Constructors
    public NonStandardPackage(Priority priority, Address senderAddress, Address destinationAdress, int width, int length, int height) {
        super(priority, senderAddress, destinationAdress);
        this.width = width;
        this.length = length;
        this.height = height;
        System.out.println(this.toString());
    }

    //Methods
    //Todo (on demand)

    //Getters & Setters
    /**
     * <p>Get package width</p>
     * @return package width.
     * @since 1.0
     */
    protected int getWidth() {
        return width;
    }

    /**
     * <p>Update package width</p>
     * @param width
     * @since 1.0
     */
    protected void setWidth(int width) {
        this.width = width;
    }

    /**
     * <p>Get package length</p>
     * @return package length.
     * @since 1.0
     */
    protected int getLength() {
        return length;
    }

    /**
     * <p>Update package length</p>
     * @param length
     * @since 1.0
     */
    protected void setLength(int length) {
        this.length = length;
    }

    /**
     * <p>Get package height</p>
     * @return package height.
     * @since 1.0
     */
    protected int getHeight() {
        return height;
    }

    /**
     * <p>Update package height</p>
     * @param height
     * @since 1.0
     */
    protected void setHeight(int height) {
        this.height = height;
    }

    /**
     * <p>Output object details</p>
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "NonStandardPackage [" + super.toString() +
                "width=" + width +
                ", length=" + length +
                ", height=" + height +
                ']';
    }

    /**
     * <p>NonStandardPackage objects comparison</p>
     * @param obj (NonStandardPackage object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!super.equals(obj))
            return false;

        if (!(obj instanceof NonStandardPackage))
            return false;

        //Primitive Comparison
        if (this.width != ((NonStandardPackage) obj).height || this.width != ((NonStandardPackage) obj).height || this.length != ((NonStandardPackage) obj).length)
            return false;

        return true;
    }
}

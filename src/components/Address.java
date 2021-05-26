/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

/**
 * Contains the details of the package address.
 * The address consists of two integers, separated by a line.
 * The first number (zip) determines the branch to which the address belongs, the second is the street number.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class Address implements Cloneable {

    //Attributes
    private int zip;
    private int street;

    //Constructors
    public Address(int zip, int street) {
        this.zip = zip;
        this.street = street;
    }

    @Override
    protected Address clone() throws CloneNotSupportedException {
        Address tempAddress = null;
        try {
            tempAddress = (Address) super.clone();
            tempAddress.zip = this.zip;
            tempAddress.street = this.street;
        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning Address object!");
        }
        return tempAddress;
    }
    //Getters & Setters

    /**
     * <p>Package street address</p>
     *
     * @return Package street value.
     * @since 1.0
     */
    protected int getStreet() {
        return street;
    }

    /**
     * <p>Update the package street address</p>
     *
     * @param street (6 int digits)
     * @since 1.0
     */
    protected void setStreet(int street) {
        this.street = street;
    }

    /**
     * <p>Update the package branch address</p>
     *
     * @param zip (1 int digit)
     * @since 1.0
     */
    protected void setZip(int zip) {
        this.zip = zip;
    }

    /**
     * <p>Package branch address</p>
     *
     * @return Package branch value.
     * @since 1.0
     */
    protected int getZip() {
        return zip;
    }

    //Overriders

    /**
     * <p>Output object details</p>
     *
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return zip + "-" + street + " ";
    }

    /**
     * <p>Address objects comparison</p>
     *
     * @param obj (Address object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Address))
            return false;

        return this.zip == ((Address) obj).zip && this.street == ((Address) obj).street;
    }
}
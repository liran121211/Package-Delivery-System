/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

/**
 * A Truck for transferring packages from the HUB center to the local branches and back.
 * All Trucks of this type are in the HUB center.
 * @author Liran Smadja
 */


public class StandardPackage extends Package {

    //Attributes
    private double weight;

    //Constructors
    public StandardPackage(Priority priority, Address senderAddress, Address destinationAdress, double weight) {
        super(priority, senderAddress, destinationAdress);
        this.weight = weight;
        System.out.println(this.toString());
    }

    //Methods
    //Todo (on demand)

    //Getters & Setters
    /**
     * <p>Get get weight of package</p>
     * @return weight of package.
     * @since 1.0
     */
    protected double getWeight() {
        return weight;
    }

    /**
     * <p>Set weight of package</p>
     * @param weight  (weight of package).
     * @since 1.0
     */
    protected void setWeight(double weight) {
        this.weight = weight;
    }

    //Overriders
    /**
     * <p>Output object details</p>
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "StandardPackage [" + super.toString() +
                "weight=" + weight +
                ']';
    }

    /**
     * <p>StandardPackage objects comparison</p>
     * @param obj (StandardPackage object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!super.equals(obj))
            return false;

        if (!(obj instanceof StandardPackage))
            return false;

        //Primitive Comparison
        if (this.weight != ((StandardPackage) obj).weight)
            return false;

        return true;
    }
}

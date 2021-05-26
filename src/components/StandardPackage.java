/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A Truck for transferring packages from the HUB center to the local branches and back.
 * All Trucks of this type are in the HUB center.
 * @author Liran Smadja, Tamar Aminov
 */


public class StandardPackage extends Package implements Cloneable{

    //Attributes
    private double weight;

    //Constructors
    public StandardPackage(Priority priority, Address senderAddress, Address destinationAdress, double weight) {
        super(priority, senderAddress, destinationAdress);
        this.weight = weight;
        System.out.println(this.toString());
    }

    //Methods
    @Override
    protected StandardPackage clone() throws CloneNotSupportedException {
        StandardPackage tempStandardPackage = null;
        CopyOnWriteArrayList<Tracking> tempTracking = new CopyOnWriteArrayList<>();
        try {
            tempStandardPackage = (StandardPackage) super.clone();
            tempStandardPackage.setPackageID(super.getPackageID());
            tempStandardPackage.setStatus(super.getStatus());
            tempStandardPackage.setPriority(super.getPriority());
            tempStandardPackage.setSenderPackage(super.getSenderPackage().clone());
            tempStandardPackage.setDestinationPackage(super.getDestinationPackage().clone());
            for (int i = 0; i < super.getTracking().size(); i++)
                tempTracking.add(super.getTracking().get(i).clone());
            tempStandardPackage.setTracking(tempTracking);
            tempStandardPackage.weight = this.weight;

        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning StandardPackage object!");
        }
        return tempStandardPackage;
    }

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
        return this.weight == ((StandardPackage) obj).weight;
    }
}

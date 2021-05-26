/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represent small package.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class SmallPackage extends Package implements Cloneable {

    //Attributes
    boolean acknowledge;

    //Constructors
    public SmallPackage(Priority priority, Address senderAddress, Address destinationAdress, boolean acknowledge) {
        super(priority, senderAddress, destinationAdress);
        this.acknowledge = acknowledge;
        System.out.println(this.toString());
    }

    //Methods
    @Override
    protected SmallPackage clone() throws CloneNotSupportedException {
        SmallPackage tempSmallPackage = null;
        CopyOnWriteArrayList<Tracking> tempTracking = new CopyOnWriteArrayList<>();
        try {
            tempSmallPackage = (SmallPackage) super.clone();
            tempSmallPackage.setPackageID(super.getPackageID());
            tempSmallPackage.setStatus(super.getStatus());
            tempSmallPackage.setPriority(super.getPriority());
            tempSmallPackage.setSenderPackage(super.getSenderPackage().clone());
            tempSmallPackage.setDestinationPackage(super.getDestinationPackage().clone());
            for (int i = 0; i < super.getTracking().size(); i++)
                tempTracking.add(super.getTracking().get(i).clone());
            tempSmallPackage.setTracking(tempTracking);
            tempSmallPackage.acknowledge = this.acknowledge;

        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning SmallPackage object!");
        }
        return tempSmallPackage;
    }

    //Getters & Setters

    /**
     * <p>Get package delivery notification</p>
     *
     * @return package delivery notification.
     * @since 1.0
     */
    protected boolean isAcknowledge() {
        return acknowledge;
    }

    /**
     * <p>Update package delivery notification</p>
     *
     * @param acknowledge (package delivery notification)
     * @since 1.0
     */
    protected void setAcknowledge(boolean acknowledge) {
        this.acknowledge = acknowledge;
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
        return "SmallPackage [" + super.toString() +
                "acknowledge=" + acknowledge +
                ']';
    }

    /**
     * <p>SmallPackage objects comparison</p>
     *
     * @param obj (SmallPackage object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!super.equals(obj))
            return false;

        if (!(obj instanceof SmallPackage))
            return false;

        //Primitive Comparison
        return this.acknowledge == ((SmallPackage) obj).acknowledge;
    }
}

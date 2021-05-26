/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

/**
 * Represent small package.
 * @author Liran Smadja, Tamar Aminov
 */

public class SmallPackage extends Package {

    //Attributes
    boolean acknowledge;

    //Constructors
    public SmallPackage(Priority priority, Address senderAddress, Address destinationAdress, boolean acknowledge) {
        super(priority, senderAddress, destinationAdress);
        this.acknowledge = acknowledge;
        System.out.println(this.toString());
    }

    //Methods
    //Todo (on demand)

    //Getters & Setters
    /**
     * <p>Get package delivery notification</p>
     * @return package delivery notification.
     * @since 1.0
     */
    protected boolean isAcknowledge() {
        return acknowledge;
    }

    /**
     * <p>Update package delivery notification</p>
     * @param acknowledge (package delivery notification)
     * @since 1.0
     */
    protected void setAcknowledge(boolean acknowledge) {
        this.acknowledge = acknowledge;
    }

    //Overriders
    /**
     * <p>Output object details</p>
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
        if (this.acknowledge != ((SmallPackage) obj).acknowledge)
            return false;

        return true;
    }
}

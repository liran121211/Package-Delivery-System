/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

/**
 * Van that collects a package from the sender's address to the local branch.
 * Van that delivers the package from the destination branch to the customer address.
 * @author Liran Smadja
 */

public class Van extends Truck implements Node {

    //Attributes
    //None

    //Constructors
    public Van() {
        super();
        System.out.println("Creating " + this.toString());
    }

    public Van(String licensePlate, String truckModel) {
        super(licensePlate, truckModel);
        System.out.println("Creating " + this.toString());
    }

    //Overriders
    /**
     * <p>The Van is sent to customer address to pick up a package</p>
     * The package tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) {
        p.setStatus(Status.BRANCH_STORAGE); //TRACKING
        System.out.println("Van " + super.getTruckID() + " has collected package " + p.getPackageID() + ", and arrived back to branch " + p.getSenderAddress().getZip());
        super.setAvailable(true);
    }

    /**
     * <p>The Van is sent to customer address to deliver a package</p>
     * The package tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        p.setStatus(Status.DELIVERED); //TRACKING
        p.addTracking(null, Status.DELIVERED); //TRACKING
        if (p instanceof SmallPackage && ((SmallPackage) p).isAcknowledge()) //Delivery message for SmallPackage
            System.out.println("Delivery message sent to customer");
        super.getPackages().remove(p);
        super.setAvailable(true);
        System.out.println("Van " + super.getTruckID() + " delivered package " + p.getPackageID() + ", to the destination");
    }

    /**
     * <p>A work unit performed by a Van at every second of the system clock</p>
     * When the Van has reached the destination , it will perform an action.
     * A package with "COLLECTION" will be collected from the customer.
     * A package with "DISTRIBUTION" will be delivered by the Van to the customer.
     * @since 1.0
     */
    @Override
    public void work() {
        super.setTimeLeft(super.getTimeLeft() - 1);

        if (super.getTimeLeft() > 0 && super.getPackages().size() > 0) {
            System.out.println("Van " + super.getTruckID() + " is collecting package " + super.getPackages().get(0).getPackageID() + " time to arrive: " + super.getTimeLeft());
        }
        if (super.getTimeLeft() == 0) { //Collect package from Customer
            if (super.getPackages().get(0).getStatus() == Status.COLLECTION)
                collectPackage(super.getPackages().get(0));

            if (super.getPackages().get(0).getStatus() == Status.DISTRIBUTION) //Deliver package to customer
                deliverPackage(super.getPackages().get(0));
        }
    }

    /**
     * <p>Output object details</p>
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "Van [" + super.toString() + "]";
    }

    /**
     * <p>Van objects comparison</p>
     * @param obj (Van object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!super.equals(obj))
            return false;

        if (!(obj instanceof Van))
            return false;

        return true;
    }
}
/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;
import java.util.Random;

/**
 * A Truck for transferring packages from the HUB center to the local branches and back.
 * All Trucks of this type are in the HUB center.
 * @author Liran Smadja
 */

public class StandardTruck extends Truck implements Node {

    //Statics
    final static int MAX_WEIGHT = 80;

    //Attributes
    private int maxWeight;
    private Branch destination;


    //Constructors
    public StandardTruck() {
        super();
        this.maxWeight = MAX_WEIGHT;
        System.out.println("Creating " + this.toString());
    }

    public StandardTruck(String licensePlate, String truckModel, int maxWeight) {
        super(licensePlate, truckModel);
        this.maxWeight = maxWeight;
        System.out.println("Creating " + this.toString());
    }

    //Overriders
    /**
     * <p>The StandardTruck is sent to local branch to pick up packages </p>
     * The package tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) { //Collect package from local branch only
        if (p.getStatus() == Status.BRANCH_STORAGE) {
            super.getPackages().add(p);
            destination.getListPackages().remove(p);
            p.setStatus(Status.HUB_TRANSPORT);
            p.addTracking(this, Status.HUB_TRANSPORT);
        }
    }

    //Overriders
    /**
     * <p>The StandardTruck is sent to local branch to deliver packages </p>
     * The package tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        //Deliver package to local branch only
        if (p.getStatus() == Status.BRANCH_TRANSPORT) {
            destination.getListPackages().add(p);
            super.getPackages().remove(p);
            p.setStatus(Status.DELIVERY);
            p.addTracking(this.getDestination(), Status.DELIVERY);
        }

    }

    /**
     * <p>A work unit performed by a branch at every second of the system clock</p>
     * When the truck has reached the destination , it will perform an action.
     * A package with "BRANCH_STORAGE" will be collected to the truck.
     * A package with "BRANCH_TRANSPORT" will be delivered by the truck.
     * @since 1.0
     */
    @Override
    public void work() {
        if (super.getTimeLeft() > 0) {
            super.setTimeLeft(super.getTimeLeft() - 1);
            if (this.getDestination() != null)
                System.out.println("StandardTruck " + super.getTruckID() + " is on it's way to " + this.destination.getBranchName() + " time to arrive: " + super.getTimeLeft());
            else
                System.out.println("StandardTruck " + super.getTruckID() + " is on it's way to HUB time to arrive: " + super.getTimeLeft());

        }

        if (super.getTimeLeft() == 0) {
            if (this.getDestination() != null) { //Collect-Deliver packages only for local branches
                System.out.println("StandardTruck " + super.getTruckID() + " arrived to " + this.destination.getBranchName());

                //DELIVER PROCESS
                for (int i = 0; i < super.getPackages().size(); i++)
                    deliverPackage(super.getPackages().get(i));
                System.out.println("StandardTruck " + super.getTruckID() + " unloaded packages at " + this.getDestination().getBranchName());

                //COLLECT PROCESS
                for (int i = 0; i < destination.getListPackages().size(); i++) {
                    if (destinationWeight() < this.getMaxWeight())
                        collectPackage(destination.getListPackages().get(i));
                    System.out.println("StandardTruck " + super.getTruckID() + " loaded packages at " + this.getDestination().getBranchName());
                }

                super.setTimeLeft(new Random().nextInt(7) + 1);
                this.setDestination(null); // Equals to HUB destination
                super.setAvailable(true);
            }

        }
    }

    /**
     * <p>Get the sum of all packages at the destination</p>
     * @return sum of packages at the destination.
     * @since 1.0
     */
    private double destinationWeight() { //Calculate packages weight in destination
        double counter = 0;
        for (int i = 0; i < destination.getListPackages().size(); i++) {
            if (destination.getListPackages().get(i) instanceof SmallPackage)
                counter += 1.0;
            if (destination.getListPackages().get(i) instanceof StandardPackage)
                counter += ((StandardPackage) destination.getListPackages().get(i)).getWeight();
        }
        return counter;
    }

    /**
     * <p>Get destination Branch object</p>
     * @return destination Branch object.
     * @since 1.0
     */
    protected Branch getDestination() {
        return destination;
    }

    /**
     * <p>Set destination Branch object</p>
     * @param destination  (destination Branch object).
     * @since 1.0
     */
    protected void setDestination(Branch destination) {
        this.destination = destination;
    }

    /**
     * <p>Get max weight of the truck's cargo</p>
     * @return max weight of the truck's cargo.
     * @since 1.0
     */
    protected int getMaxWeight() {
        return maxWeight;
    }

    /**
     * <p>set get max weight of the truck's cargo</p>
     * @param maxWeight  (max weight of the truck's cargo).
     * @since 1.0
     */
    protected void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    /**
     * <p>Output object details</p>
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return " StandardTruck [" + super.toString() +
                ", maxWeight=" + maxWeight +
                ']';
    }

    /**
     * <p>StandardTruck objects comparison</p>
     * @param obj (StandardTruck object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!super.equals(obj))
            return false;

        if (!(obj instanceof StandardTruck))
            return false;

        //Primitive Comparison
        if (this.maxWeight != ((StandardTruck) obj).maxWeight || this.destination.equals(((StandardTruck) obj).destination) )
            return false;

        return true;
    }

}

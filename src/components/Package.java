/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import java.util.ArrayList;

import static components.MainOffice.getId; //main clock

/**
 * Represent an abstract object of package
 * Contains all of the attributes that each package should have.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public abstract class Package {

    //Statics
    static private int id = 1000;

    //Attributes
    private int packageID;
    private Priority priority;
    private Status status;
    private Address senderAddress, destinationAddress;
    private PackageGUI senderPackage, destinationPackage;
    private ArrayList<Tracking> tracking;

    //Constructors
    public Package(Priority priority, Address senderAddress, Address destinationAddress) {
        tracking = new ArrayList<>();
        this.tracking.add(new Tracking(getId(), null, Status.CREATION)); // null - package at customer position
        this.priority = priority;
        this.senderAddress = senderAddress;
        this.destinationAddress = destinationAddress;
        this.status = Status.CREATION;
        this.packageID = id;
        id++;
    }

    //Methods

    /**
     * <p>Update package tracking</p>
     *
     * @param node   (package location)
     * @param status (package status)
     * @since 1.0
     */
    protected void addTracking(Node node, Status status) {
        tracking.add(new Tracking(getId(), node, status));
    }

    /**
     * <p>Print all of the statuses changes that were made with the package</p>
     *
     * @since 1.0
     */
    protected void printTracking() {
        System.out.println("\nTRACKING " + this.toString());
        for (int i = 0; i < getTracking().size(); i++) {
            if (getTracking().get(i).getNode() instanceof StandardTruck)
                System.out.println(getTracking().get(i).getTime() + ": " + "StandardTruck " + ((StandardTruck) getTracking().get(i).getNode()).getTruck().getTruckID() + ",status=" + getTracking().get(i).getStatus());
            else if (getTracking().get(i).getNode() instanceof NonStandardTruck)
                System.out.println(getTracking().get(i).getTime() + ": " + "NonStandardTruck " + ((NonStandardTruck) getTracking().get(i).getNode()).getTruck().getTruckID() + ",status=" + getTracking().get(i).getStatus());
            else if (getTracking().get(i).getNode() instanceof Van)
                System.out.println(getTracking().get(i).getTime() + ": " + "Van " + ((Van) getTracking().get(i).getNode()).getTruck().getTruckID() + ",status=" + getTracking().get(i).getStatus());
            else if (getTracking().get(i).getNode() instanceof Branch)
                System.out.println(getTracking().get(i).getTime() + ": " + ((Branch) getTracking().get(i).getNode()).getBranchName() + ", status=" + getTracking().get(i).getStatus());
            else
                System.out.println(getTracking().get(i).getTime() + ": " + "Customer, status=" + getTracking().get(i).getStatus());
        }
    }

    //Getters & Setters

    /**
     * <p>Get package identifier</p>
     *
     * @return package identifier.
     * @since 1.0
     */
    protected int getPackageID() {
        return packageID;
    }

    /**
     * <p>Get package priority</p>
     *
     * @return package priority.
     * @since 1.0
     */
    protected Priority getPriority() {
        return priority;
    }

    /**
     * <p>Update package status</p>
     *
     * @param priority (package priority status)
     * @since 1.0
     */
    protected void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * <p>Get package status</p>
     *
     * @return package status.
     * @since 1.0
     */
    protected Status getStatus() {
        return status;
    }

    /**
     * <p>Update package status</p>
     *
     * @param status (package status)
     * @since 1.0
     */
    protected void setStatus(Status status) {
        this.status = status;
    }

    /**
     * <p>Get package sender Address</p>
     *
     * @return package sender Address.
     * @since 1.0
     */
    protected Address getSenderAddress() {
        return senderAddress;
    }

    /**
     * <p>Update package sender Address</p>
     *
     * @param senderAddress (package sender Address)
     * @since 1.0
     */
    protected void setSenderAddress(Address senderAddress) {
        this.senderAddress = senderAddress;
    }

    /**
     * <p>Get package destination Address</p>
     *
     * @return package destination Address.
     * @since 1.0
     */
    protected Address getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * <p>Update package setDestination Address</p>
     *
     * @param destinationAddress (package setDestination Address)
     * @since 1.0
     */
    protected void setDestinationAddress(Address destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    /**
     * <p>Get package tracking list</p>
     *
     * @return package tracking list.
     * @since 1.0
     */
    protected ArrayList<Tracking> getTracking() {
        return tracking;
    }

    /**
     * <p>Update tracking list</p>
     *
     * @param tracking (package tracking list)
     * @since 1.0
     */
    protected void setTracking(ArrayList<Tracking> tracking) {
        this.tracking = tracking;
    }

    /**
     * <p>Update package identifier</p>
     *
     * @param packageID (package identifier).
     * @since 1.0
     */
    protected void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    /**
     * <p>Output object details</p>
     *
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "packageID=" + this.packageID +
                ", priority=" + priority +
                ", status=" + status +
                ", senderAddress=" + senderAddress +
                ", destinationAddress=" + destinationAddress;
    }

    /**
     * <p>Package objects comparison</p>
     *
     * @param obj (Package object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Package))
            return false;

        //Primitive Comparison
        if (this.packageID != ((Package) obj).packageID || this.priority != ((Package) obj).priority || this.status != ((Package) obj).status || this.senderAddress.equals(((Package) obj).senderAddress) || this.destinationAddress.equals(((Package) obj).destinationAddress))
            return false;

        //Deep Comparison
        for (Tracking value : this.tracking)
            if (!value.equals(obj))
                return false;

        return true;
    }

    /**
     * <p>Get Customer sender package</p>
     *
     * @return senderPackage (Package apackage)
     * @since 1.1
     */
    public PackageGUI getSenderPackage() {
        return senderPackage;
    }

    /**
     * <p>Set Customer sender package</p>
     *
     * @param senderPackage senderPackage (Package apackage)
     * @since 1.1
     */
    public void setSenderPackage(PackageGUI senderPackage) {
        this.senderPackage = senderPackage;
    }

    /**
     * <p>Get customer destination package</p>
     *
     * @return senderPackage (Package apackage)
     * @since 1.1
     */
    public PackageGUI getDestinationPackage() {
        return destinationPackage;
    }

    /**
     * <p>Set Customer receiver package</p>
     *
     * @param destinationPackage destinationPackage (Package apackage)
     * @since 1.1
     */
    public void setDestinationPackage(PackageGUI destinationPackage) {
        this.destinationPackage = destinationPackage;
    }

}

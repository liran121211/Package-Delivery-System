/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;
import java.lang.Math;

/**
 * A vehicle for transporting packages of non-standard size.
 * All vehicles of this type are in the HUB center.
 * @author Liran Smadja
 */

public class NonStandardTruck extends Truck implements Node {

    //Statics
    private final static int MAX_WIDTH = 300;
    private final static int MAX_LENGTH = 400;
    private final static int MAX_HEIGHT = 900;

    //Attributes
    private int width;
    private int length;
    private int height;

    //Constructors
    public NonStandardTruck() {
        super();
        this.width = MAX_WIDTH;
        this.length = MAX_LENGTH;
        this.height = MAX_HEIGHT;
        System.out.println("Creating " + this.toString());
    }

    public NonStandardTruck(String licensePlate, String truckModel, int length, int width, int height) {
        super(licensePlate, truckModel);
        this.width = width;
        this.length = length;
        this.height = height;
        System.out.println("Creating " + this.toString());
    }

    //Overriders
    /**
     * <p>The NonStandardTruck is sent to the customer's address to pick up a package </p>
     * The package tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) {
        p.setStatus(Status.DISTRIBUTION); //TRACKING
        p.addTracking(this, Status.DISTRIBUTION); //TRACKING
        super.setTimeLeft((Math.abs(p.getSenderAddress().getStreet() - p.getDestinationAddress().getStreet() / 10) % 10) + 1); //Calculate driving time
        System.out.println("NonStandardTruck " + super.getTruckID() + " has collected package " + p.getPackageID());
    }

    /**
     * <p>The NonStandardTruck is sent to the customer's address to deliver a package </p>
     * The package tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        p.setStatus(Status.DELIVERED); //TRACKING
        p.addTracking(null, Status.DELIVERED); //TRACKING
        super.getPackages().remove(p);
        super.setAvailable(true);
        System.out.println("NonStandardTruck " + super.getTruckID() + " has delivered package " + p.getPackageID() + " to the destination");
    }


    //Methods
    /**
     * <p>A work unit performed by the truck at every second of the system clock</p>
     * When the truck has reached the destination , it will perform an action.
     * A package with "COLLECTION" will be collected to the truck.
     * A package with "DISTRIBUTION" will be delivered by the truck.
     * @since 1.0
     */
    @Override
    public void work() {
        if (super.getTimeLeft() > 0) {
            super.setTimeLeft(super.getTimeLeft() - 1);
            if (super.getPackages().size() == 0)
                System.out.println();
            if (super.getPackages().get(0).getStatus() == Status.COLLECTION)
                System.out.println("NonStandardTruck " + super.getTruckID() + " is collecting package " + super.getPackages().get(0).getPackageID() + " time to arrive: " + super.getTimeLeft());
            if (super.getPackages().get(0).getStatus() == Status.DISTRIBUTION)
                System.out.println("NonStandardTruck " + super.getTruckID() + " is delivering package " + super.getPackages().get(0).getPackageID() + " time to arrive: " + super.getTimeLeft());
        }

        if (super.getTimeLeft() == 0) {
            for (int i = 0; i < super.getPackages().size(); i++) {
                if (super.getPackages().get(i).getStatus() == Status.COLLECTION)
                    collectPackage(super.getPackages().get(i));
                else
                    deliverPackage(super.getPackages().get(i));
            }
        }
    }

    //Getters & Setters
    /**
     * <p>Get truck's container width</p>
     * @return truck's container width.
     * @since 1.0
     */
    protected int getWidth() {
        return width;
    }

    /**
     * <p>Update truck's container width</p>
     * @param  width (truck's container width)
     * @since 1.0
     */
    protected void setWidth(int width) {
        this.width = width;
    }

    /**
     * <p>Get truck's container length</p>
     * @return truck's container length.
     * @since 1.0
     */
    protected int getLength() {
        return length;
    }

    /**
     * <p>Update truck's container length</p>
     * @param  length (truck's container length)
     * @since 1.0
     */
    protected void setLength(int length) {
        this.length = length;
    }

    /**
     * <p>Get truck's container height</p>
     * @return truck's container height.
     * @since 1.0
     */
    protected int getHeight() {
        return height;
    }

    /**
     * <p>Update truck's container height</p>
     * @param  height (truck's container height)
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
        return " NonStandardTruck [" + super.toString() +
                ", width=" + width +
                ", length=" + length +
                ", height=" + height +
                ']';
    }

    /**
     * <p>NonStandardTruck objects comparison</p>
     * @param obj (NonStandardTruck object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof NonStandardTruck))
            return false;

        if (!super.equals(obj))
            return false;

        //Primitive Comparison
        if (this.width != ((NonStandardTruck) obj).width || this.height != ((NonStandardTruck) obj).height || this.length != ((NonStandardTruck) obj).length)
            return false;

        return true;
    }
}

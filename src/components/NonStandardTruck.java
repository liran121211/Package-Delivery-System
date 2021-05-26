/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import java.lang.Math;

/**
 * A vehicle for transporting packages of non-standard size.
 * All vehicles of this type are in the HUB center.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class NonStandardTruck implements Node, Runnable {

    //Statics
    private final static int MAX_WIDTH = 300;
    private final static int MAX_LENGTH = 400;
    private final static int MAX_HEIGHT = 900;

    //Attributes
    private int width;
    private int length;
    private int height;
    private Truck truck;
    private TruckGUI gui;
    private Boolean isSuspended;

    //Constructors
    public NonStandardTruck() {
        super();
        truck = new Truck();
        this.width = MAX_WIDTH;
        this.length = MAX_LENGTH;
        this.height = MAX_HEIGHT;
        isSuspended = true;
        System.out.println("Creating " + this.toString());

    }

    public NonStandardTruck(String licensePlate, String truckModel, int length, int width, int height) {
        super();
        truck = new Truck(licensePlate, truckModel);
        this.width = width;
        this.length = length;
        this.height = height;
        isSuspended = false;
        System.out.println("Creating " + this.toString());
    }

    //Overriders

    /**
     * <p>The NonStandardTruck is sent to the customer's address to pick up a package </p>
     * The package tracking and status is updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) {
        p.setStatus(Status.DISTRIBUTION); //TRACKING
        p.addTracking(this, Status.DISTRIBUTION); //TRACKING
        truck.setKeepTime(((Math.abs(p.getSenderAddress().getStreet() - p.getDestinationAddress().getStreet() / 10) % 10) + 1) * 10);
        gui.setLocation(truck.getPackages().get(0).getSenderPackage().getX() + 3, truck.getPackages().get(0).getSenderPackage().getY() + 10); //Fix Y position
        truck.setTimeLeft(truck.getKeepTime()); //Calculate driving time
        gui.changeColor("DARK_RED");
        p.getSenderPackage().changeColor("LIGHT_RED");
        gui.revalidate();
        gui.repaint();
        gui.visible(true);
        setResume(); // Continue thread activity
        System.out.println("NonStandardTruck " + truck.getTruckID() + " has collected package " + p.getPackageID());
    }

    /**
     * <p>The NonStandardTruck is sent to the customer's address to deliver a package </p>
     * The package tracking and status is updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        p.setStatus(Status.DELIVERED); //TRACKING
        p.addTracking(null, Status.DELIVERED); //TRACKING
        truck.getPackages().remove(p);
        truck.setAvailable(true);
        System.out.println("NonStandardTruck " + truck.getTruckID() + " has delivered package " + p.getPackageID() + " to the destination");
        gui.setLocation(1150, 185); //Return Truck GUI to HUB
        p.getDestinationPackage().changeColor("DARK_RED");
        setSuspend(); // Suspend thread activity
    }


    //Methods

    /**
     * <p>A work unit performed by the truck at every second of the system clock</p>
     * When the truck has reached the destination , it will perform an action.
     * A package with "COLLECTION" will be collected to the truck.
     * A package with "DISTRIBUTION" will be delivered by the truck.
     *
     * @since 1.0
     */
    @Override
    public void work() {
        if (truck.getPackages().size() > 0)
            if (truck.getTimeLeft() > 0) {
                truck.setTimeLeft(truck.getTimeLeft() - 1);
                if (truck.getPackages().size() == 0)
                    System.out.println();
                if (truck.getPackages().get(0).getStatus() == Status.COLLECTION)
                    System.out.println("NonStandardTruck " + truck.getTruckID() + " is collecting package " + truck.getPackages().get(0).getPackageID() + " time to arrive: " + truck.getTimeLeft());
                if (truck.getPackages().get(0).getStatus() == Status.DISTRIBUTION)
                    System.out.println("NonStandardTruck " + truck.getTruckID() + " is delivering package " + truck.getPackages().get(0).getPackageID() + " time to arrive: " + truck.getTimeLeft());
            }

        if (truck.getTimeLeft() == 0) {
            for (int i = 0; i < truck.getPackages().size(); i++) {
                if (truck.getPackages().get(i).getStatus() == Status.COLLECTION)
                    collectPackage(truck.getPackages().get(i));
                else
                    deliverPackage(truck.getPackages().get(i));
            }
        }
    }

    //Getters & Setters

    /**
     * <p>Get truck's container width</p>
     *
     * @return truck's container width.
     * @since 1.0
     */
    protected int getWidth() {
        return width;
    }

    /**
     * <p>Update truck's container width</p>
     *
     * @param width (truck's container width)
     * @since 1.0
     */
    protected void setWidth(int width) {
        this.width = width;
    }

    /**
     * <p>Get truck's container length</p>
     *
     * @return truck's container length.
     * @since 1.0
     */
    protected int getLength() {
        return length;
    }

    /**
     * <p>Update truck's container length</p>
     *
     * @param length (truck's container length)
     * @since 1.0
     */
    protected void setLength(int length) {
        this.length = length;
    }

    /**
     * <p>Get truck's container height</p>
     *
     * @return truck's container height.
     * @since 1.0
     */
    protected int getHeight() {
        return height;
    }

    /**
     * <p>Update truck's container height</p>
     *
     * @param height (truck's container height)
     * @since 1.0
     */
    protected void setHeight(int height) {
        this.height = height;
    }

    /**
     * <p>Get Turck object</p>
     *
     * @return Truck object.
     * @since 1.0
     */
    protected Truck getTruck() {
        return truck;
    }

    /**
     * <p>Set Turck object</p>
     *
     * @param truck (Turck object).
     * @since 1.0
     */
    protected void setTruck(Truck truck) {
        this.truck = truck;
    }

    /**
     * <p>Output object details</p>
     *
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return " NonStandardTruck [" + truck.toString() +
                ", width=" + width +
                ", length=" + length +
                ", height=" + height +
                ']';
    }

    /**
     * <p>NonStandardTruck objects comparison</p>
     *
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
        return this.width == ((NonStandardTruck) obj).width && this.height == ((NonStandardTruck) obj).height && this.length == ((NonStandardTruck) obj).length;
    }

    /*
     * <p>Start thread activity</p>
     *
     * @since 1.1
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                System.out.println("NonStandardTruck " + truck.getTruckID() + " thread was interrupted");
            }

            if (isSuspended) {
                try {
                    synchronized (this) { //Check if there are packages to deliver, else suspend the thread
                        wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println("NonStandardTruck " + truck.getTruckID() + " thread was interrupted");
                }

            } else {
                work();
                if (truck.isAvailable())
                    gui.visible(false);
                if (truck.getPackages().size() > 0) {
                    if (truck.getPackages().get(0).getStatus() == Status.COLLECTION) {
                        LineGUI senderLine = (truck.getPackages().get(0)).getSenderPackage().getPointTo();
                        int axis = gui.getX() - (int) (distance(senderLine) / truck.getKeepTime());
                        gui.setLocation(axis, (int) linear(axis, senderLine));
                    } else if (truck.getPackages().get(0).getStatus() == Status.DISTRIBUTION) {
                        LineGUI receiverLine = truck.getPackages().get(0).getDestinationPackage().getPointTo();
                        gui.setLocation(gui.getX(), gui.getY() + (int) distance(receiverLine) / truck.getKeepTime());
                    }
                }
            }
        }
    }

    /**
     * <p>calculate the linear equation: y =mx+n</p>
     *
     * @return (y) of linear equation
     * @since 1.1
     */
    private double linear(int xAxis, LineGUI line) {
        double linear_slope = slope(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        double linear_intercept = (line.getStartY() - (linear_slope * line.getStartX()));
        return linear_slope * xAxis + linear_intercept;
    }

    /**
     * <p>calculate the slope of linear equation: y =mx+n</p>
     *
     * @return (m) of linear equation
     * @since 1.1
     */
    private double slope(int x1, int y1, int x2, int y2) {
        return ((double) (y1 - y2)) / ((double) (x1 - x2));
    }

    /**
     * <p>calculate the distance between 2 points (x,y)</p>
     *
     * @return length of linear line
     * @since 1.1
     */
    private double distance(LineGUI line) {
        return Math.sqrt((line.getEndX() - line.getStartX()) * ((line.getEndX() - line.getStartX())) + ((line.getEndY() - line.getStartY()) * (line.getEndY() - line.getStartY())));
    }

    /**
     * <p>Suspend thread activity</p>
     *
     * @since 1.1
     */
    protected synchronized void setSuspend() {
        this.isSuspended = true;
    }

    /**
     * <p>Resume thread activity</p>
     *
     * @since 1.1
     */
    protected synchronized void setResume() {
        this.isSuspended = false;
        this.notify();
    }

    /**
     * <p>Get Truck graphical component</p>
     *
     * @return gui (Truck GUI)
     * @since 1.1
     */
    protected TruckGUI getGui() {
        return gui;
    }

    /**
     * <p>Set Truck graphical component</p>
     *
     * @param gui (Truck GUI)
     * @since 1.1
     */
    protected void setGui(TruckGUI gui) {
        this.gui = gui;
    }

}

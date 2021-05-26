/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.Math;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A vehicle for transporting packages of non-standard size.
 * All vehicles of this type are in the HUB center.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class NonStandardTruck implements Node, Runnable, Cloneable {

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
    private Boolean isTerminated;
    private PropertyChangeSupport support;

    //Constructors
    public NonStandardTruck() {
        super();
        this.truck = new Truck();
        this.width = MAX_WIDTH;
        this.length = MAX_LENGTH;
        this.height = MAX_HEIGHT;
        this.isSuspended = true;
        this.isTerminated = false;
        this.support = new PropertyChangeSupport(this);
        System.out.println("Creating " + this.toString());

    }

    public NonStandardTruck(String licensePlate, String truckModel, int length, int width, int height) {
        super();
        this.truck = new Truck(licensePlate, truckModel);
        this.width = width;
        this.length = length;
        this.height = height;
        this.isSuspended = false;
        this.isTerminated = false;
        this.support = new PropertyChangeSupport(this);
        System.out.println("Creating " + this.toString());
    }

    //Overriders
    @Override
    protected NonStandardTruck clone() throws CloneNotSupportedException {
        NonStandardTruck tempNonStandardTruck = null;
        CopyOnWriteArrayList<Package> tempPackages = new CopyOnWriteArrayList<>();
        try {
            tempNonStandardTruck = (NonStandardTruck) super.clone();
            tempNonStandardTruck.getTruck().setTruckID(this.truck.getTruckID());
            tempNonStandardTruck.getTruck().setLicensePlate(this.truck.getLicensePlate());
            tempNonStandardTruck.getTruck().setTruckModel(this.truck.getTruckModel());
            tempNonStandardTruck.getTruck().setAvailable(this.truck.isAvailable());
            tempNonStandardTruck.getTruck().setTimeLeft(this.truck.getTimeLeft());
            tempNonStandardTruck.getTruck().setKeepTime(this.truck.getKeepTime());
            for (int i = 0; i < this.truck.getPackages().size(); i++)
                if (this.truck.getPackages().get(i) instanceof NonStandardPackage)
                    tempPackages.add(((NonStandardPackage) this.truck.getPackages().get(i)).clone());

            tempNonStandardTruck.getTruck().setPackages(tempPackages);
            tempNonStandardTruck.setIsTerminated(this.isTerminated);
            tempNonStandardTruck.width = this.width;
            tempNonStandardTruck.length = this.length;
            tempNonStandardTruck.height = this.height;
            tempNonStandardTruck.isSuspended = this.isSuspended;
            tempNonStandardTruck.gui = this.gui.clone();
            tempNonStandardTruck.support = new PropertyChangeSupport(this);

        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning NonStandardTruck object!");
        }
        return tempNonStandardTruck;
    }
    /**
     * <p>The NonStandardTruck is sent to the customer's address to pick up a package </p>
     * The package tracking and status is updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) {
        support.firePropertyChange("Package #"+p.getPackageID(), p.getStatus(), Status.DISTRIBUTION);
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
        support.firePropertyChange("Package #"+p.getPackageID(), p.getStatus(), Status.DELIVERED);
        p.setStatus(Status.DELIVERED); //TRACKING
        p.addTracking(null, Status.DELIVERED); //TRACKING
        truck.getPackages().remove(p);
        truck.setAvailable(true);
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
            if (truck.getTimeLeft() > 0)
                truck.setTimeLeft(truck.getTimeLeft() - 1);



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
     * Get isTerminated status
     * @return (isTerminated) status.
     * @since 1.2
     */
    protected Boolean getIsTerminated() {
        return isTerminated;
    }

    /**
     * Set isTerminated status
     * @param (isTerminated) status.
     * @since 1.2
     */
    protected void setIsTerminated(Boolean isTerminated) {
        this.isTerminated = isTerminated;
    }

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
        while (!isTerminated) {
            try {
                Thread.sleep(50);
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
                        if (gui.getX() < senderLine.getStartX() && gui.getY() < senderLine.getStartY())  // If the vehicle deviates from the packageGUI then move to delivering.
                            truck.setTimeLeft(0);
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
     * Set Change Listener (send event when pack tracking has changed)
     * @param (pcl) listener
     * @since 1.2
     */
    protected void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
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

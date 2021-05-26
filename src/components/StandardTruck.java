/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A Truck for transferring packages from the HUB center to the local branches and back.
 * All Trucks of this type are in the HUB center.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class StandardTruck implements Node, Runnable, Cloneable {
    //Statics
    final static int MAX_WEIGHT = 8000;

    //Attributes
    private Boolean isSuspended;
    private Boolean isTerminated;
    private Branch destination;
    private Branch keepTrack;
    private Truck truck;
    private TruckGUI gui;
    private PropertyChangeSupport support;
    private int maxWeight;


    //Constructors
    public StandardTruck() {
        super();
        this.truck = new Truck();
        this.maxWeight = MAX_WEIGHT;
        this.isSuspended = false;
        this.isTerminated = false;
        this.support = new PropertyChangeSupport(this);
        System.out.println("Creating " + this.toString());
    }

    public StandardTruck(String licensePlate, String truckModel, int maxWeight) {
        super();
        this.truck = new Truck(licensePlate, truckModel);
        this.maxWeight = maxWeight;
        this.isSuspended = false;
        this.isTerminated = false;
        this.support = new PropertyChangeSupport(this);
        System.out.println("Creating " + this.toString());
    }

    //Overriders
    @Override
    protected synchronized StandardTruck clone() throws CloneNotSupportedException {
        StandardTruck tempStandardTruck = null;
        CopyOnWriteArrayList<Package> tempPackages = new CopyOnWriteArrayList<>();
        try {
            tempStandardTruck = (StandardTruck) super.clone();
            tempStandardTruck.getTruck().setTruckID(this.truck.getTruckID());
            tempStandardTruck.getTruck().setLicensePlate(this.truck.getLicensePlate());
            tempStandardTruck.getTruck().setTruckModel(this.truck.getTruckModel());
            tempStandardTruck.getTruck().setAvailable(this.truck.isAvailable());
            tempStandardTruck.getTruck().setTimeLeft(this.truck.getTimeLeft());
            tempStandardTruck.getTruck().setKeepTime(this.truck.getKeepTime());
            for (int i = 0; i < this.truck.getPackages().size(); i++) {
                if (this.truck.getPackages().get(i) instanceof SmallPackage)
                    tempPackages.add(((SmallPackage) this.truck.getPackages().get(i)).clone());
                if (this.truck.getPackages().get(i) instanceof StandardPackage)
                    tempPackages.add(((StandardPackage) this.truck.getPackages().get(i)).clone());
            }
            tempStandardTruck.getTruck().setPackages(tempPackages);
            if (this.destination == null) // if destination is HUB
                tempStandardTruck.destination = null;
            else
                tempStandardTruck.destination = this.destination;

            if (this.keepTrack == null) // if destination is HUB
                tempStandardTruck.keepTrack = null;
            else
                tempStandardTruck.keepTrack = this.keepTrack;

            tempStandardTruck.isSuspended = this.isSuspended;
            tempStandardTruck.isTerminated = this.isTerminated;
            tempStandardTruck.maxWeight = this.maxWeight;
            tempStandardTruck.gui = this.gui.clone();
            tempStandardTruck.support = new PropertyChangeSupport(this);

        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning StandardTruck object!");
        }
        return tempStandardTruck;
    }

    /**
     * <p>The StandardTruck is sent to local branch to pick up packages </p>
     * The package tracking and status is updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) { //Collect package from local branch only
        if (p.getStatus() == Status.BRANCH_STORAGE) {
            truck.getPackages().add(p);
            destination.getListPackages().remove(p);
            support.firePropertyChange("Package #" + p.getPackageID(), p.getStatus(), Status.HUB_TRANSPORT);
            p.setStatus(Status.HUB_TRANSPORT);
            p.addTracking(this, Status.HUB_TRANSPORT);
        }
    }

    //Overriders
    /**
     * <p>The StandardTruck is sent to local branch to deliver packages </p>
     * The package tracking and status is updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        //Deliver package to local branch only
        if (p.getStatus() == Status.BRANCH_TRANSPORT) {
            p.getDestinationPackage().changeColor("LIGHT_RED");
            destination.getListPackages().add(p);
            truck.getPackages().remove(p);
            support.firePropertyChange("Package #" + p.getPackageID(), p.getStatus(), Status.DELIVERY);
            p.setStatus(Status.DELIVERY);
            p.setStatus(Status.DELIVERY);
            p.addTracking(this.getDestination(), Status.DELIVERY);
        }

    }

    /**
     * <p>A work unit performed by a branch at every second of the system clock</p>
     * When the truck has reached the destination , it will perform an action.
     * A package with "BRANCH_STORAGE" will be collected to the truck.
     * A package with "BRANCH_TRANSPORT" will be delivered by the truck.
     *
     * @since 1.0
     */
    @Override
    public void work() {
            if (truck.getPackages().size() > 0)
                gui.changeColor("DARK_GREEN");
            else
                gui.changeColor("LIGHT_GREEN");
            gui.setNumPackages(this.truck.getPackages().size());
            gui.revalidate();
            gui.repaint();

            if (truck.getTimeLeft() > 0)
                truck.setTimeLeft(truck.getTimeLeft() - 1);

            if (truck.getTimeLeft() == 0) {
                if (this.getDestination() != null) { //Collect-Deliver packages only for local branches
                    gui.setLocation(destination.getGui().getPointTo().getStartX(), destination.getGui().getPointTo().getStartY());

                    //DELIVER PROCESS
                    for (int i = 0; i < truck.getPackages().size(); i++)
                        deliverPackage(truck.getPackages().get(i));

                    //COLLECT PROCESS
                    for (int i = 0; i < destination.getListPackages().size(); i++) {
                        if (destinationWeight() < this.getMaxWeight())
                            collectPackage(destination.getListPackages().get(i));
                    }
                    truck.setKeepTime((new Random().nextInt(7) + 1) * 10);
                    truck.setTimeLeft(truck.getKeepTime());
                    this.keepTrack = destination; //Keep destination for GUI movement
                    this.setDestination(null); // Equals to HUB destination
                    truck.setAvailable(true);
                }
            }
    }

    /**
     * Set isTerminated status
     * @param (isTerminated) status.
     * @since 1.2
     */
    public void setIsTerminated(Boolean isTerminated) {
        this.isTerminated = isTerminated;
    }

    /**
     * <p>Get the sum of all packages at the destination</p>
     *
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
     *
     * @return destination Branch object.
     * @since 1.0
     */
    protected Branch getDestination() {
        return destination;
    }

    /**
     * <p>Set destination Branch object</p>
     *
     * @param destination (destination Branch object).
     * @since 1.0
     */
    protected void setDestination(Branch destination) {
        this.destination = destination;
    }

    /**
     * <p>Get max weight of the truck's cargo</p>
     *
     * @return max weight of the truck's cargo.
     * @since 1.0
     */
    protected int getMaxWeight() {
        return maxWeight;
    }

    /**
     * <p>set get max weight of the truck's cargo</p>
     *
     * @param maxWeight (max weight of the truck's cargo).
     * @since 1.0
     */
    protected void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
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
        return " StandardTruck [" + truck.toString() +
                ", maxWeight=" + maxWeight +
                ']';
    }

    /**
     * <p>StandardTruck objects comparison</p>
     *
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
        return this.maxWeight == ((StandardTruck) obj).maxWeight && !this.destination.equals(((StandardTruck) obj).destination);
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
                System.out.println("StandardTruck " + truck.getTruckID() + " thread was interrupted");
            }
            if (isSuspended) {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println("StandardTruck " + truck.getTruckID() + " thread was interrupted");
                }
            } else {
                synchronized (this){
                    gui.visible(false);
                    work();
                    if (destination != null && truck.getTimeLeft() > 0) {
                        gui.visible(true);
                        int axis = gui.getX() - (int) (distance() / truck.getKeepTime());
                        gui.setLocation(axis, (int) linear(axis));
                    } else if (destination == null && truck.getTimeLeft() > 0) {
                        gui.visible(true);
                        int axis = gui.getX() + (int) (distance() / truck.getKeepTime());
                        gui.setLocation(axis, (int) linear(axis));
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

    public void setKeepTrack(Branch keepTrack) {
        this.keepTrack = keepTrack;
    }

    /**
     * <p>calculate the linear equation: y =mx+n</p>
     *
     * @return (y) of linear equation
     * @since 1.1
     */
    private double linear(int xAxis) {
        if (destination == null) {
            LineGUI line = keepTrack.getGui().getPointTo();
            double linear_slope = slope(keepTrack.getGui().getX(), keepTrack.getGui().getY(), line.getEndX(), line.getEndY());
            double linear_intercept = (keepTrack.getGui().getY() - (linear_slope * keepTrack.getGui().getX()));
            return linear_slope * xAxis + linear_intercept;
        } else {
            LineGUI line = destination.getGui().getPointTo();
            double linear_slope = slope(destination.getGui().getX(), destination.getGui().getY(), line.getEndX(), line.getEndY());
            double linear_intercept = (destination.getGui().getY() - (linear_slope * destination.getGui().getX()));
            return linear_slope * xAxis + linear_intercept;
        }
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
    private double distance() {
        if (destination == null) {
            LineGUI line = keepTrack.getGui().getPointTo();
            return Math.sqrt(((line.getEndX() - keepTrack.getGui().getX()) * ((line.getEndX() - keepTrack.getGui().getX())) + ((line.getEndY() - keepTrack.getGui().getY()) * (line.getEndY() - keepTrack.getGui().getY()))));

        } else {
            LineGUI line = destination.getGui().getPointTo();
            return Math.sqrt(((line.getEndX() - destination.getGui().getX()) * ((line.getEndX() - destination.getGui().getX())) + ((line.getEndY() - destination.getGui().getY()) * (line.getEndY() - destination.getGui().getY()))));
        }
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


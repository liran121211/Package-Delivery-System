/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

/**
 * Van that collects a package from the sender's address to the local branch.
 * Van that delivers the package from the destination branch to the customer address.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class Van implements Node, Runnable {

    //Attributes
    private Truck truck;
    private TruckGUI gui;
    private BranchGUI keepGui;
    private Boolean isSuspended;

    //Constructors
    public Van() {
        super();
        truck = new Truck();
        isSuspended = true;
        System.out.println("Creating " + this.toString());

    }

    public Van(String licensePlate, String truckModel) {
        super();
        truck = new Truck(licensePlate, truckModel);
        isSuspended = true;
        System.out.println("Creating " + this.toString());
    }

    //Overriders

    /**
     * <p>The Van is sent to customer address to pick up a package</p>
     * The package tracking and status is updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) {
        p.setStatus(Status.BRANCH_STORAGE); //TRACKING
        System.out.println("Van " + truck.getTruckID() + " has collected package " + p.getPackageID() + ", and arrived back to branch " + p.getSenderAddress().getZip());
        truck.getPackages().get(0).getSenderPackage().changeColor("LIGHT_RED");
        truck.getPackages().get(0).getSenderPackage().revalidate();
        truck.getPackages().get(0).getSenderPackage().repaint();
        truck.setAvailable(true);
        gui.visible(false);
        setSuspend(); // Suspend thread activity
    }

    /**
     * <p>The Van is sent to customer address to deliver a package</p>
     * The package tracking and status is updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        p.setStatus(Status.DELIVERED); //TRACKING
        p.addTracking(null, Status.DELIVERED); //TRACKING
        if (p instanceof SmallPackage && ((SmallPackage) p).isAcknowledge()) //Delivery message for SmallPackage
            System.out.println("Delivery message sent to customer");
        truck.getPackages().get(0).getDestinationPackage().changeColor("DARK_RED");
        truck.getPackages().get(0).getDestinationPackage().revalidate();
        truck.getPackages().get(0).getDestinationPackage().repaint();
        truck.getPackages().remove(p);
        truck.setAvailable(true);
        gui.visible(false);
        setSuspend(); // Suspend thread activity
        System.out.println("Van " + truck.getTruckID() + " delivered package " + p.getPackageID() + ", to the destination");
    }

    /**
     * <p>A work unit performed by a Van at every second of the system clock</p>
     * When the Van has reached the destination , it will perform an action.
     * A package with "COLLECTION" will be collected from the customer.
     * A package with "DISTRIBUTION" will be delivered by the Van to the customer.
     *
     * @since 1.0
     */
    @Override
    public void work() {
        if (truck.getTimeLeft() > 0) {
            truck.setTimeLeft(truck.getTimeLeft() - 1);
            if (truck.getPackages().size() > 0)
                System.out.println("Van " + truck.getTruckID() + " is collecting package " + truck.getPackages().get(0).getPackageID() + " time to arrive: " + truck.getTimeLeft());
        }
        if (truck.getTimeLeft() == 0) { //Collect package from Customer
            gui.setLocation(keepGui.getX(), keepGui.getY());

            if (truck.getPackages().get(0).getStatus() == Status.COLLECTION) //Collect package from customer
                collectPackage(truck.getPackages().get(0));

            if (truck.getPackages().get(0).getStatus() == Status.DISTRIBUTION) //Deliver package to customer
                deliverPackage(truck.getPackages().get(0));
        }
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
        return "Van [" + truck.toString() + "]";
    }

    /**
     * <p>Van objects comparison</p>
     *
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

        return obj instanceof Van;
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
                System.out.println("Van " + truck.getTruckID() + " thread was interrupted");
            }
            if (isSuspended) { //Check if there are packages to deliver, else suspend the thread
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println("Van " + truck.getTruckID() + " thread was interrupted");
                }

            } else {
                if (truck.getPackages().size() > 0 && truck.getPackages().get(0).getStatus() == Status.COLLECTION) {
                    LineGUI senderLine = truck.getPackages().get(0).getSenderPackage().getPointTo();
                    int axis = gui.getX() + (int) (distance(senderLine) / truck.getKeepTime());
                    gui.setLocation(axis, (int) linear(axis, senderLine));
                    if (gui.getX() > senderLine.getStartX() && gui.getY() < senderLine.getStartY()) { // If the vehicle deviates from the location of the package then finish ride
                        truck.setTimeLeft(0);
                    }
                    work();

                } else if (truck.getPackages().size() > 0 && truck.getPackages().get(0).getStatus() == Status.DISTRIBUTION) {
                    LineGUI receiverLine = truck.getPackages().get(0).getDestinationPackage().getPointTo();
                    int axis = gui.getX() + (int) (distance(receiverLine) / truck.getKeepTime());
                    gui.setLocation(axis, (int) linear(axis, receiverLine));
                    if (gui.getX() > receiverLine.getStartX() && gui.getY() > receiverLine.getStartY()) { // If the vehicle deviates from the location of the package then finish ride
                        truck.setTimeLeft(0);
                    }
                    work();
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

    /**
     * <p>Get Branch graphical component</p>
     *
     * @return keepGui (Branch GUI)
     * @since 1.1
     */
    protected BranchGUI getKeepGui() {
        return keepGui;
    }

    /**
     * <p>Set Branch graphical component</p>
     *
     * @param keepGui (Branch GUI)
     * @since 1.1
     */
    protected void setKeepGui(BranchGUI keepGui) {
        this.keepGui = keepGui;
    }
}
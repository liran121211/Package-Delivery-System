/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represent a local branch.
 * Maintains a list of local branches.
 * The HUB has trucks that collect and deliver the packages from and to the local branches.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class Hub implements Node, Runnable {

    //Attributes
    ArrayList<Branch> branches;
    private Boolean isSuspended;

    //Constructors
    public Hub() {
        super();
        branches = new ArrayList<>();
        isSuspended = false;
        branches.add(new Branch("HUB"));

    }

    //Overriders

    /**
     * <p>StandardTruck is sent to the local branch to pick up the packages</p>
     * StandardTruck is loaded with the destination packages.
     * The packages tracking and statuses are updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) {
        Branch tempBranch = this.getBranches().get(0); // This HUB
        int zip = p.getDestinationAddress().getZip() - 1; //Destination zip
        Truck tempTruck = ((StandardTruck) tempBranch.getListTrucks().get(zip)).getTruck(); //Get correlated truck match to brunch
        System.out.println("StandardTruck " + tempTruck.getTruckID() + " loaded packages at HUB");
        p.setStatus(Status.BRANCH_TRANSPORT); //TRACKING
        p.addTracking(((StandardTruck) tempBranch.getListTrucks().get(zip)), Status.BRANCH_TRANSPORT); //TRACKING
        tempTruck.getPackages().add(p); //Add package HUB ---> StandardTruck
        tempBranch.getListPackages().remove(p); //Remove package HUB ---> StandardTruck

    }

    /**
     * <p>NonStandardTruck is sent to the customer's address to deliver the package </p>
     * NonStandardTruck is loaded with the destination packages.
     * The packages tracking and statuses are updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        Branch tempBranch = this.getBranches().get(0); // This HUB
        Truck tempTruck = ((NonStandardTruck) tempBranch.getListTrucks().get(tempBranch.getListTrucks().size() - 1)).getTruck(); //Get NonStandardTruck

        if (p.getStatus() == Status.CREATION && p instanceof NonStandardPackage && tempTruck.getPackages().size() == 0) { //check if package is for NonStandardTruck
            p.setStatus(Status.COLLECTION); //TRACKING
            p.addTracking(((NonStandardTruck) tempBranch.getListTrucks().get(tempBranch.getListTrucks().size() - 1)), Status.COLLECTION); //TRACKING
            tempTruck.getPackages().add(p); //Add package HUB ---> NonStandardTruck
            tempBranch.getListPackages().remove(p); //Remove package HUB
            tempTruck.setKeepTime((new Random().nextInt(11) + 1) * 10);
            tempTruck.setTimeLeft(tempTruck.getKeepTime()); //Time set for customer travel
            ((NonStandardTruck) tempBranch.getListTrucks().get(tempBranch.getListTrucks().size() - 1)).getGui().changeColor("LIGHT_RED");
            ((NonStandardTruck) tempBranch.getListTrucks().get(tempBranch.getListTrucks().size() - 1)).getGui().revalidate();
            ((NonStandardTruck) tempBranch.getListTrucks().get(tempBranch.getListTrucks().size() - 1)).getGui().repaint();
            tempTruck.setAvailable(false);
            ((NonStandardTruck) tempBranch.getListTrucks().get(tempBranch.getListTrucks().size() - 1)).getGui().visible(true);
            System.out.println("NonStandardTruck " + tempTruck.getTruckID() + " loaded packages at HUB");
            ((NonStandardTruck) tempBranch.getListTrucks().get(tempBranch.getListTrucks().size() - 1)).setResume(); // Suspend thread activity
        }
    }

    @Override
    /*
     * <p>Iterate through all available StandardTruck from HUB</p>
     * We Check if Truck returned from local branch, and move the packages back to HUB
     * Load and Unload packages with the Trucks
     * The packages tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    public void work() {
        Branch thisHub = this.getBranches().get(0); //Get Hub object (Make the code readable)

        for (int i = 0; i < thisHub.getListTrucks().size(); i++) {
            Object currentTruck = thisHub.getListTrucks().get(i); // Make the code readable

            if (currentTruck instanceof StandardTruck && ((StandardTruck) currentTruck).getTruck().isAvailable() && ((StandardTruck) currentTruck).getTruck().getTimeLeft() == 0) { //Check if Truck returned from local branch
                ((StandardTruck) currentTruck).setDestination(this.getBranches().get(i + 1));//Correlate branch number with the truck (i)
                ((StandardTruck) currentTruck).getTruck().setKeepTime((new Random().nextInt(11) + 1) * 10);
                ((StandardTruck) currentTruck).getTruck().setTimeLeft(((StandardTruck) currentTruck).getTruck().getKeepTime()); //Time set for local branch travel
                ((StandardTruck) currentTruck).getGui().setLocation(((StandardTruck) currentTruck).getDestination().getGui().getPointTo().getEndX(), ((StandardTruck) currentTruck).getDestination().getGui().getPointTo().getEndY());// Fix starting point GUI
                ((StandardTruck) currentTruck).getTruck().setAvailable(false);

                for (int j = 0; j < thisHub.getListPackages().size(); j++) { //Load all packages in HUB to StandardTrucks
                    if (thisHub.getListPackages().get(j).getDestinationAddress().getZip() == i + 1 && !(thisHub.getListPackages().get(j) instanceof NonStandardPackage)) //Correlate package ID to Truck ID
                        if (destinationWeight(thisHub) < ((StandardTruck) currentTruck).getMaxWeight()) //Check if max weight exceeded
                            collectPackage(thisHub.getListPackages().get(j));
                }


                for (int k = 0; k < ((StandardTruck) currentTruck).getTruck().getPackages().size(); k++) { //Unload all packages in StandardTrucks to HUB
                    Package tempPackage = ((StandardTruck) currentTruck).getTruck().getPackages().get(k);
                    if (tempPackage.getStatus() == Status.HUB_TRANSPORT) { //Check if package the package came from local branch
                        tempPackage.setStatus(Status.HUB_STORAGE); //TRACKING
                        tempPackage.addTracking(thisHub, Status.HUB_STORAGE); //TRACKING
                        thisHub.getListPackages().add(tempPackage); //Add package from StandardTruck ---> HUB
                        ((StandardTruck) currentTruck).getTruck().getPackages().remove(tempPackage); //Remove package from StandardTruck
                        System.out.println("StandardTruck " + ((StandardTruck) currentTruck).getTruck().getTruckID() + " unloaded packages at HUB");
                    }
                }
            } else if (currentTruck instanceof NonStandardTruck && ((NonStandardTruck) currentTruck).getTruck().isAvailable()) { // check if truck is NonStandard
                for (int j = 0; j < thisHub.getListPackages().size(); j++) //Load a package in HUB to NonStandardTruck
                    deliverPackage(thisHub.getListPackages().get(j));
            }
        }
    }

    //Methods

    /**
     * <p>Get destination's weight of packages</p>
     *
     * @param destination (Branch object)
     * @return the sun of weight in the destination.
     * @since 1.0
     */
    private double destinationWeight(Branch destination) { //Calculate packages weight in destination
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
     * <p>Check if package is fit to the dimensions of the Truck</p>
     *
     * @param nst (Branch object)
     * @param p   (package object)
     * @return True if Fit else, False.
     * @since 1.0
     * @deprecated no longer used after 1.0 version
     */
    private boolean isFit(NonStandardTruck nst, Package p) { // check of package if the NonStandardTruck
        if (p instanceof NonStandardPackage) {
            NonStandardPackage nsp = (NonStandardPackage) p;
            return nsp.getHeight() < nst.getHeight() && nsp.getWidth() < nst.getWidth() && nsp.getLength() < nst.getLength();
        }
        return false;
    }

    //Getters & Setters

    /**
     * <p>Get branches list</p>
     *
     * @return branches list.
     * @since 1.0
     */
    protected ArrayList<Branch> getBranches() {
        return branches;
    }

    /**
     * <p>Set Branches list</p>
     *
     * @param branches (Array list).
     * @since 1.0
     */
    protected void setBranches(ArrayList<Branch> branches) {
        this.branches = branches;
    }

    //Overriders

    /**
     * <p>Branch objects comparison</p>
     *
     * @param obj (Branch object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Hub))
            return false;

        //Deep Comparison
        for (Branch branch : this.branches)
            if (!branch.equals(obj))
                return false;

        return true;
    }

    /**
     * <p>Output object details</p>
     *
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "Hub{" +
                "branches=" + branches +
                '}';
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
                System.out.println("HUB thread was interrupted");
            }

            if (isSuspended)// check if the hub is Suspended
            {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println("HUB thread was interrupted");
                }
            } else {
                work();
            }
        }
    }

    /**
     * <p>Suspend thread activity</p>
     *
     * @since 1.0
     */
    protected synchronized void setSuspend() {
        this.isSuspended = true;
    }

    /**
     * <p>Resume thread activity</p>
     *
     * @since 1.0
     */
    protected synchronized void setResume() {
        this.isSuspended = false;
        this.notify();
    }

}

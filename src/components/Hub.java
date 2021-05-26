/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represent a local branch.
 * Maintains a list of local branches.
 * The HUB has trucks that collect and deliver the packages from and to the local branches.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class Hub implements Node, Runnable, Cloneable {

    //Attributes
    private CopyOnWriteArrayList<Branch> branches;
    private PropertyChangeSupport support;
    private Boolean isSuspended;
    private Boolean isTerminated;

    //Constructors
    public Hub() {
        this.branches = new CopyOnWriteArrayList<>();
        this.isSuspended = false;
        this.isTerminated = false;
        this.branches.add(new Branch("HUB"));
        this.support = new PropertyChangeSupport(this);

    }

    //Overriders
    @Override
    protected synchronized Hub clone() throws CloneNotSupportedException {
        Hub tempHub = null;
        CopyOnWriteArrayList<Branch> tempBranches = new CopyOnWriteArrayList<>();
        try {
            tempHub = (Hub) super.clone();
            tempHub.support = new PropertyChangeSupport(this);
            tempHub.isSuspended = this.isSuspended;
            tempHub.isTerminated = this.isTerminated;
            tempBranches.add(branches.get(0));
            for (int i = 1; i < branches.size(); i++) {
                tempBranches.add(branches.get(i).clone());
            }
            tempHub.branches = tempBranches;

        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning Hub object!");
        }
        return tempHub;
    }

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
        support.firePropertyChange("Package #" + p.getPackageID(), p.getStatus(), Status.BRANCH_TRANSPORT);
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
            support.firePropertyChange("Package #" + p.getPackageID(), Status.CREATION, Status.COLLECTION);
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
            //System.out.println("NonStandardTruck " + tempTruck.getTruckID() + " loaded packages at HUB");
            ((NonStandardTruck) tempBranch.getListTrucks().get(tempBranch.getListTrucks().size() - 1)).setResume();
        }
    }


    /*
     * <p>Iterate through all available StandardTruck from HUB</p>
     * We Check if Truck returned from local branch, and move the packages back to HUB
     * Load and Unload packages with the Trucks
     * The packages tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void work() {
        Branch thisHub = this.getBranches().get(0); //Get Hub object (Make the code readable)

        for (int i = 0; i < thisHub.getListTrucks().size(); i++) {
            Object currentTruck = thisHub.getListTrucks().get(i); // Make the code readable

            if (currentTruck instanceof StandardTruck && ((StandardTruck) currentTruck).getTruck().isAvailable() && ((StandardTruck) currentTruck).getTruck().getTimeLeft() == 0) { //Check if Truck returned from local branch
                ((StandardTruck) currentTruck).setDestination(this.getBranches().get(i + 1));//Correlate branch number with the truck (i)
                ((StandardTruck) currentTruck).getTruck().setKeepTime((new Random().nextInt(11) + 1) * 10);
                ((StandardTruck) currentTruck).getTruck().setTimeLeft(((StandardTruck) currentTruck).getTruck().getKeepTime()); //Time set for local branch travel
                ((StandardTruck) currentTruck).getTruck().setAvailable(false);

                for (int j = 0; j < thisHub.getListPackages().size(); j++) { //Load all packages in HUB to StandardTrucks
                    if (thisHub.getListPackages().get(j).getDestinationAddress().getZip() == i + 1 && !(thisHub.getListPackages().get(j) instanceof NonStandardPackage)) //Correlate package ID to Truck ID
                        if (destinationWeight(thisHub) < ((StandardTruck) currentTruck).getMaxWeight()) //Check if max weight exceeded
                            collectPackage(thisHub.getListPackages().get(j));
                        else
                            System.out.println("Cant deliver a package, now stuck in HUB!");
                }


                for (int k = 0; k < ((StandardTruck) currentTruck).getTruck().getPackages().size(); k++) { //Unload all packages in StandardTrucks to HUB
                    Package tempPackage = ((StandardTruck) currentTruck).getTruck().getPackages().get(k);
                    if (tempPackage.getStatus() == Status.HUB_TRANSPORT) { //Check if package the package came from local branch
                        support.firePropertyChange("Package #" + tempPackage.getPackageID(), Status.HUB_TRANSPORT, Status.HUB_STORAGE);
                        tempPackage.setStatus(Status.HUB_STORAGE); //TRACKING
                        tempPackage.addTracking(thisHub, Status.HUB_STORAGE); //TRACKING
                        thisHub.getListPackages().add(tempPackage); //Add package from StandardTruck ---> HUB
                        ((StandardTruck) currentTruck).getTruck().getPackages().remove(tempPackage); //Remove package from StandardTruck
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
    @Deprecated
    private boolean isFit(NonStandardTruck nst, Package p) { // check of package if the NonStandardTruck
        if (p instanceof NonStandardPackage) {
            NonStandardPackage nsp = (NonStandardPackage) p;
            return nsp.getHeight() < nst.getHeight() && nsp.getWidth() < nst.getWidth() && nsp.getLength() < nst.getLength();
        }
        return false;
    }

    //Getters & Setters

    /**
     * Get isTerminated Status.
     *
     * @return isTerminated status
     * @since 1.2
     */
    public Boolean getIsTerminated() {
        return isTerminated;
    }

    /**
     * Set isTerminated Status.
     *
     * @param (isTerminated) status
     * @since 1.2
     */
    public void setIsTerminated(Boolean isTerminated) {
        this.isTerminated = isTerminated;
    }

    /**
     * <p>Get branches list</p>
     *
     * @return branches list.
     * @since 1.0
     */
    protected CopyOnWriteArrayList<Branch> getBranches() {
        return branches;
    }

    /**
     * <p>Set Branches list</p>
     *
     * @param branches (Array list).
     * @since 1.0
     */
    protected void setBranches(CopyOnWriteArrayList<Branch> branches) {
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
        while (!isTerminated) {
            try {
                Thread.sleep(50);
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
     * Set Change Listener (send event when pack tracking has changed)
     *
     * @param (pcl) listener
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    /**
     * Get Change Listener (send event when pack tracking has changed)
     *
     * @since 1.2
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
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

    /**
     * Get Change Listener (send event when pack tracking has changed)
     *
     * @return (pcl) listener
     */
    public PropertyChangeSupport getSupport() {
        return support;
    }


}

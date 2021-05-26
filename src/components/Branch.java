/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represent a local branch.
 * Maintains a list of packages stored at the branch or intended for collection from the sender's address to this branch
 * A list of Vans that collect and deliver the packages from and to the customers.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class Branch implements Node, Runnable, Cloneable {
    //Statics
    private static int id = 0;

    //Attributes
    private int brachId;
    private String branchName;
    private Boolean isSuspended;
    private Boolean isTerminated;
    private BranchGUI gui;
    private CopyOnWriteArrayList<Object> listTrucks;
    private CopyOnWriteArrayList<Package> listPackages;
    private PropertyChangeSupport support;


    //Constructors
    public Branch() {
        this.brachId = id;
        this.listTrucks = new CopyOnWriteArrayList<>();
        this.listPackages = new CopyOnWriteArrayList<>();
        this.support = new PropertyChangeSupport(this);
        this.branchName = "Branch " + this.brachId;
        this.isSuspended = false;
        this.isTerminated = false;
        System.out.println(this.toString());
        id++;
    }

    public Branch(String branchName) {
        this.branchName = branchName;
        this.listTrucks = new CopyOnWriteArrayList<>();
        this.listPackages = new CopyOnWriteArrayList<>();
        this.support = new PropertyChangeSupport(this);
        this.brachId = id;
        this.isSuspended = false;
        this.isTerminated = false;
        System.out.println(this.toString());
        id++;
    }

    @Override
    protected Branch clone() throws CloneNotSupportedException {
        Branch tempBranch = null;
        CopyOnWriteArrayList<Package> tempPackages = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Object> tempTrucks = new CopyOnWriteArrayList<>();
        try {
            tempBranch = (Branch) super.clone();
            tempBranch.brachId = this.brachId;
            tempBranch.branchName = this.branchName;
            tempBranch.isSuspended = this.isSuspended;
            tempBranch.isTerminated = this.isTerminated;
            for (Package aPackage : this.listPackages) {
                if (aPackage instanceof SmallPackage)
                    tempPackages.add(((SmallPackage) aPackage).clone());
                if (aPackage instanceof StandardPackage)
                    tempPackages.add(((StandardPackage) aPackage).clone());
                if (aPackage instanceof NonStandardPackage)
                    tempPackages.add(((NonStandardPackage) aPackage).clone());
            }
            for (Object aTruck : this.listTrucks) {
                if (aTruck instanceof Van)
                    tempTrucks.add(((Van) aTruck).clone());
                if (aTruck instanceof StandardTruck)
                    tempTrucks.add(((StandardTruck) aTruck).clone());
                if (aTruck instanceof NonStandardTruck)
                    tempTrucks.add(((NonStandardTruck) aTruck).clone());
            }
            tempBranch.listPackages = tempPackages;
            tempBranch.listTrucks = tempTrucks;
            tempBranch.gui = this.gui.clone();
            tempBranch.support = new PropertyChangeSupport(this);

        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning Branch object!");
        }
        return tempBranch;
    }

    //Overriders

    /**
     * <p>The Van is sent to the customer's address to pick up the package </p>
     * The first available Van is chosen to pickup the package.
     * Once an available Van is found, the package is added to the Van.
     * The package tracking and status is updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) {
        Object collectTurck = getAvailableTruck(); //Get available Van in branch
        if (collectTurck instanceof Van) { // Prevent Trucks from taking the package
            support.firePropertyChange("New Package arrived!", null, p);
            support.firePropertyChange("Package #" + p.getPackageID(), p.getStatus(), Status.COLLECTION);
            p.setStatus(Status.COLLECTION); //TRACKING
            p.addTracking((Van) collectTurck, Status.COLLECTION); //TRACKING
            ((Van) collectTurck).getTruck().getPackages().add(p);
            ((Van) collectTurck).getTruck().setKeepTime(((p.getSenderAddress().getStreet() / 10) % 10 + 1) * 10);
            ((Van) collectTurck).getTruck().setTimeLeft(((Van) collectTurck).getTruck().getKeepTime());
            ((Van) collectTurck).getGui().setLocation(gui.getX(), gui.getY()); //set TruckGUI location
            ((Van) collectTurck).getTruck().setAvailable(false);
            ((Van) collectTurck).getGui().visible(true);
            this.getListPackages().remove(p);
            ((Van) collectTurck).setResume(); // Continue thread activity
        }
    }

    /**
     * <p>The Van is sent to the customer's address to deliver the package </p>
     * The first available Van is chosen to deliver the package.
     * Once an available Van is found, the package is added to the Van.
     * The package tracking and status is updated.
     *
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        Object deliverTurck = getAvailableTruck();
        if (deliverTurck instanceof Van) { // Prevent Trucks from taking the package
            support.firePropertyChange("Package #" + p.getPackageID(), p.getStatus(), Status.DISTRIBUTION);
            p.setStatus(Status.DISTRIBUTION);
            p.addTracking((Van) deliverTurck, Status.DISTRIBUTION);
            ((Van) deliverTurck).getTruck().getPackages().add(p);
            ((Van) deliverTurck).getTruck().setKeepTime(((p.getDestinationAddress().getStreet() / 10) % 10 + 1) * 10);
            ((Van) deliverTurck).getTruck().setTimeLeft(((Van) deliverTurck).getTruck().getKeepTime());
            ((Van) deliverTurck).getGui().setLocation(gui.getX(), gui.getY()); //set TruckGUI location
            ((Van) deliverTurck).getTruck().setAvailable(false);
            ((Van) deliverTurck).getGui().visible(true);
            this.getListPackages().remove(p);
            ((Van) deliverTurck).setResume(); // Continue thread activity
        }
    }

    /**
     * <p>A work unit performed by a branch at every second of the system clock</p>
     * All packages in the Vans with the "BRANCH_STORAGE" are taken back to the branch.
     * A package with "CREATION" will be collected with a Van.
     * A package with "DELIVERY" will be delivered with a Van.
     *
     * @since 1.1
     */
    @Override
    public void work() {
        if (getListPackages().size() > 0) {
            support.firePropertyChange("Notification to Vans", null, null);
            gui.changeColor("DARK_BLUE");
        } else
            gui.changeColor("LIGHT_BLUE");

        for (int j = 0; j < this.getListTrucks().size(); j++) { //Scan for collected packages in Van
            if (this.getListTrucks().get(j) instanceof Van) {
                Truck tempTruck = ((Van) this.getListTrucks().get(j)).getTruck();
                for (int k = 0; k < tempTruck.getPackages().size(); k++) {
                    if (tempTruck.getPackages().get(k).getStatus() == Status.BRANCH_STORAGE) {
                        tempTruck.getPackages().get(k).addTracking(this, Status.BRANCH_STORAGE); //TRACKING
                        this.getListPackages().add(tempTruck.getPackages().get(k)); //Return package from Van back to branch
                        tempTruck.getPackages().remove(k);
                    }
                }
            }
        }

        for (int i = 0; i < this.getListPackages().size(); i++) { //Send Van to collect package
            if (this.getListPackages().get(i).getStatus() == Status.CREATION) {
                collectPackage(this.getListPackages().get(i));

            } else if (this.getListPackages().get(i).getStatus() == Status.DELIVERY) //Send Van to deliver the package
                deliverPackage(this.getListPackages().get(i));
        }
    }

    //Methods

    /**
     * <p>Get an available Van</p>
     *
     * @return Iterate thorugh all vans and return the first van with available status.
     * @since 1.0
     */
    protected Object getAvailableTruck() { //Find the first available Truck in branch
        for (int i = 0; i < this.getListTrucks().size(); i++) {
            if (this.getListTrucks().get(i) instanceof Van && ((Van) this.getListTrucks().get(i)).getTruck().isAvailable())
                return this.getListTrucks().get(i);
        }
        return null;
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
     * <p>Get Branch Name</p>
     *
     * @return object branch name.
     * @since 1.0
     */
    protected String getBranchName() {
        return branchName;
    }

    /**
     * <p>Set Branch Name</p>
     *
     * @param branchName String of Branch Name.
     * @since 1.0
     */
    protected void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * <p>Get List of Trucks</p>
     *
     * @return object (branch) List of Trucks.
     * @since 1.0
     */
    protected CopyOnWriteArrayList<Object> getListTrucks() {
        return listTrucks;
    }

    /**
     * <p>Set List of Trucks</p>
     *
     * @param listTrucks (branch) List of Trucks.
     * @since 1.0
     */
    protected void setListTrucks(CopyOnWriteArrayList<Object> listTrucks) {
        this.listTrucks = listTrucks;
    }

    /**
     * <p>Set List of Packages</p>
     *
     * @return object (branch) List of Packages.
     * @since 1.0
     */
    protected CopyOnWriteArrayList<Package> getListPackages() {
        return listPackages;
    }

    /**
     * <p>Set List of Packages</p>
     *
     * @param listPackages (branch) List of Packages.
     * @since 1.0
     */
    protected void setListPackages(CopyOnWriteArrayList<Package> listPackages) {
        this.listPackages = listPackages;
    }

    /**
     * <p>Get Branch ID</p>
     *
     * @return object branch id.
     * @since 1.0
     */
    protected int getBrachId() {
        return brachId;
    }

    /**
     * <p>Set Branch ID</p>
     *
     * @param brachId object branch id.
     * @since 1.0
     */
    protected void setBrachId(int brachId) {
        this.brachId = brachId;
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
     * <p>Get Branch visual component</p>
     *
     * @return Visual branch component (Jcomponent)
     * @since 1.0
     */
    protected BranchGUI getGui() {
        return gui;
    }

    /**
     * <p>Set Branch visual component</p>
     *
     * @param gui Visual branch component (Jcomponent)
     * @since 1.0
     */
    protected void setGui(BranchGUI gui) {
        this.gui = gui;
    }

    //Overriders

    /**
     * <p>Output object details</p>
     *
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "\nCreating Branch " + (this.brachId - 1) +
                ", branchName= " + branchName +
                ", trucks: " + listTrucks.size() +
                ", packages: " + listPackages.size();
    }

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

        if (!(obj instanceof Branch))
            return false;

        //Primitive Comparison
        if (this.brachId != ((Branch) obj).brachId || this.branchName.compareTo(((Branch) obj).branchName) != 0)
            return false;

        //Deep Comparison
        for (int i = 0; i < this.getListTrucks().size(); i++)
            if (!this.getListTrucks().get(i).equals(obj))
                return false;

        for (int j = 0; j < this.getListTrucks().size(); j++)
            if (!this.getListPackages().get(j).equals(obj))
                return false;

        return true;
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
                System.out.println(branchName + " thread was interrupted");
            }

            if (isSuspended)// check if the branch is Suspended
            {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println(branchName + " thread was interrupted");
                }
            } else {
                work();
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
     * Get Change Listener (send event when pack tracking has changed)
     * @return (pcl) listener
     * @since 1.2
     */
    protected PropertyChangeSupport getSupport() {
        return support;
    }

}

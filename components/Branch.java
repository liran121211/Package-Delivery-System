/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */


package components;
import java.util.ArrayList;

/**
 * Represent a local branch.
 * Maintains a list of packages stored at the branch or intended for collection from the sender's address to this branch
 * A list of Vans that collect and deliver the packages from and to the customers.
 * @author Liran Smadja
 */

public class Branch implements Node {
    //Statics
    private static int id = 0;

    //Attributes
    private int brachId;
    private String branchName;
    private ArrayList<Truck> listTrucks;
    private ArrayList<Package> listPackages;

    //Constructors
    public Branch() {
        this.brachId = id;
        listTrucks = new ArrayList<>();
        listPackages = new ArrayList<>();
        this.branchName = "Branch " + String.valueOf(this.brachId);
        System.out.println(this.toString());
        id++;
    }

    public Branch(String branchName) {
        this.branchName = branchName;
        listTrucks = new ArrayList<>();
        listPackages = new ArrayList<>();
        this.brachId = id;
        System.out.println(this.toString());
        id++;
    }

    //Overriders
    /**
     * <p>The Van is sent to the customer's address to pick up the package </p>
     * The first available Van is chosen to pickup the package.
     * Once an available Van is found, the package is added to the Van.
     * The package tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) {
        Truck getTurck = getAvailableTruck(); //Get available Van in branch
        if (getTurck instanceof Van) { // Prevent Trucks from taking the package
            p.setStatus(Status.COLLECTION); //TRACKING
            p.addTracking((Van) getTurck, Status.COLLECTION); //TRACKING
            getTurck.getPackages().add(p);
            this.getListPackages().remove(p);
            getTurck.setTimeLeft((p.getSenderAddress().getStreet() / 10) % 10 + 1);
            getTurck.setAvailable(false);
        }
    }

    /**
     * <p>The Van is sent to the customer's address to deliver the package </p>
     * The first available Van is chosen to deliver the package.
     * Once an available Van is found, the package is added to the Van.
     * The package tracking and status is updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        Truck getTurck = getAvailableTruck();
        if (getTurck instanceof Van) { // Prevent Trucks from taking the package
            p.setStatus(Status.DISTRIBUTION);
            p.addTracking((Van) getTurck, Status.DISTRIBUTION);
            getTurck.getPackages().add(p);
            this.getListPackages().remove(p);
            getTurck.setTimeLeft((p.getSenderAddress().getStreet() / 10) % 10 + 1);
            getTurck.setAvailable(false);
        }
    }

    /**
     * <p>A work unit performed by a branch at every second of the system clock</p>
     * All packages in the Vans with the "BRANCH_STORAGE" are taken back to the branch.
     * A package with "CREATION" will be collected with a Van.
     * A package with "DELIVERY" will be delivered with a Van.
     * @since 1.0
     */
    @Override
    public void work() {
        for (int j = 0; j < this.getListTrucks().size(); j++) { //Scan for collected packages in Van
            for (int k = 0; k < this.getListTrucks().get(j).getPackages().size(); k++) {
                if (this.getListTrucks().get(j).getPackages().get(k).getStatus() == Status.BRANCH_STORAGE) {
                    this.getListTrucks().get(j).getPackages().get(k).addTracking(this, Status.BRANCH_STORAGE); //TRACKING
                    this.getListPackages().add(this.getListTrucks().get(j).getPackages().get(k)); //Return package from Van back to branch
                    this.getListTrucks().get(j).getPackages().remove(k);
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
     * @return Iterate thorugh all vans and return the first van with available status.
     * @since 1.0
     */
    protected Truck getAvailableTruck() { //Find the first available Truck in branch
        for (int i = 0; i < this.getListTrucks().size(); i++) {
            if (this.getListTrucks().get(i).isAvailable())
                return this.getListTrucks().get(i);
        }
        return null;
    }

    //Getters & Setters
    /**
     * <p>Get Branch Name</p>
     * @return object branch name.
     * @since 1.0
     */
    protected String getBranchName() {
        return branchName;
    }

    /**
     * <p>Set Branch Name</p>
     * @param branchName  String of Branch Name.
     * @since 1.0
     */
    protected void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * <p>Get List of Trucks</p>
     * @return object (branch) List of Trucks.
     * @since 1.0
     */
    protected ArrayList<Truck> getListTrucks() {
        return listTrucks;
    }

    /**
     * <p>Set List of Trucks</p>
     * @param  listTrucks (branch) List of Trucks.
     * @since 1.0
     */
    protected void setListTrucks(ArrayList<Truck> listTrucks) {
        this.listTrucks = listTrucks;
    }

    /**
     * <p>Set List of Packages</p>
     * @return object (branch) List of Packages.
     * @since 1.0
     */
    protected ArrayList<Package> getListPackages() {
        return listPackages;
    }

    /**
     * <p>Set List of Packages</p>
     * @param  listPackages (branch) List of Packages.
     * @since 1.0
     */
    protected void setListPackages(ArrayList<Package> listPackages) {
        this.listPackages = listPackages;
    }

    /**
     * <p>Get Branch ID</p>
     * @return object branch id.
     * @since 1.0
     */
    protected int getBrachId() {
        return brachId;
    }

    /**
     * <p>Set Branch ID</p>
     * @param brachId  object branch id.
     * @since 1.0
     */
    protected void setBrachId(int brachId) {
        this.brachId = brachId;
    }

    //Overriders
    /**
     * <p>Output object details</p>
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "\nCreating Branch " + (this.brachId -1 )+
                ", branchName= " + branchName +
                ", trucks: " + listTrucks.size() +
                ", packages: " + listPackages.size();
    }

    /**
     * <p>Branch objects comparison</p>
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
}

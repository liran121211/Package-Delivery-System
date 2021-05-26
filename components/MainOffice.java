/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;
import java.util.ArrayList;
import java.util.Random;

/**
 * This department manages the entire system.
 * Operates the system clock, the branches and vehicles.
 * Simulates packages and transfers them to the appropriate branches and customers.
 * @author Liran Smadja
 */


public class MainOffice {
    //Statics
    private final static Random rand = new Random();
    private static int id = 0; //Use as package wide clock

    //Attributes
    private int clock;
    private Hub hub;
    ArrayList<Package> packages;

    //Constructors
    /**
     * <p>Initialize Hub, Branches and Trucks</p>
     * for sorting center (HUB), (trucksForBranch) amount of turcks are made
     * For every local branch that made, a (trucksForBranch) amount of vans are made.
     * @param branches (amount of branches)
     * @param trucksForBranch (amount of trucks)
     * @since 1.0
     */
    public MainOffice(int branches, int trucksForBranch) {
        this.clock = id;
        this.hub = new Hub();
        this.packages = new ArrayList<>();

        //Add StandardTrucks and 1 NonStandardTruck to HUB
        for (int i = 0; i < trucksForBranch; i++)
            hub.getBranches().get(0).getListTrucks().add(new StandardTruck());
        hub.getBranches().get(0).getListTrucks().add(new NonStandardTruck());

        //Initialize Branches with Vans
        for (int i = 1; i <= branches; i++) {
            hub.getBranches().add(new Branch());
            for (int j = 0; j < trucksForBranch; j++)
                hub.getBranches().get(i).getListTrucks().add(new Van());
        }
    }

    //Methods
    /**
     * <p>Repeat (playTime) number of iterations that the system will perform and activates.</p>
     * At the end of loop. Prints all the tracking history of all packages
     * @param playTime (number of seconds to operate)
     * @since 1.0
     */
    public void play(int playTime) {
        System.out.println("========================== START ==========================");

        for (int i = 0; i < playTime; i++)
            tick();
        System.out.println("========================== STOP ==========================");
        printReport(); //Print Tracking Timeline of Packages
    }

    /**
     * <p>Each time that function is called, The following actions are performed</p>
     * The clock is printed and promoted by one.
     * All branches, sorting center (HUB) and vehicles perform one work unit.
     * Every 5 seconds a new random package is created.
     * @since 1.0
     */
    protected void tick() {
        System.out.println(clockString());
        if (this.clock % 5 == 0) //Create random package each 5 seconds
            addPackage();
        hub.work(); //HUB iteration
        for (int i = 0; i < hub.branches.size(); i++) {
            hub.branches.get(i).work(); //Branches iteration
            for (int j = 0; j < hub.branches.get(i).getListTrucks().size(); j++) {
                if (hub.branches.get(i).getListTrucks().get(j) instanceof Van)
                    ((Van) hub.branches.get(i).getListTrucks().get(j)).work(); //Van iteration

                if (hub.branches.get(i).getListTrucks().get(j) instanceof StandardTruck)
                    ((StandardTruck) hub.branches.get(i).getListTrucks().get(j)).work(); //StandardTruck iteration

                if (hub.branches.get(i).getListTrucks().get(j) instanceof NonStandardTruck)
                    ((NonStandardTruck) hub.branches.get(i).getListTrucks().get(j)).work(); //NonStandardTruck iteration
            }
        }
        this.clock = id;
        id++;
    }

    //Methods
    /**
     * <p>Generate random address</p>
     * @return Address object.
     * @since 1.0
     */
    private Address generateAddress() { //Generate random address
        int randZip = rand.nextInt(hub.getBranches().size());
        if (randZip == 0) //Prevent package from being delivered to HUB at generation
            randZip = 1;
        int randStreet = rand.nextInt(899999) + 1000000;
        return new Address(randZip, randStreet);
    }

    protected void printReport() { //Print all packages timeline
        for (int i = 0; i < this.packages.size(); i++) {
            this.packages.get(i).printTracking();
        }
    }

    /**
     * <p>Generate random type of package</p>
     * @since 1.0
     */
    protected void addPackage() {
        //Initializers
        int randPackageType = rand.nextInt(3); // [Small, Standard, NonStandard]
        int randPriorityType = rand.nextInt(3); // [Low , Standard, High]
        boolean randAcknowledge = rand.nextBoolean(); // [False, True]
        int randStandardWeight = rand.nextInt(11) + 1; // Weight between 1-10
        int randNonStandardHeight = rand.nextInt(401) + 1; // Weight between 1-400
        int randNonStandardWidth = rand.nextInt(501) + 1; // Weight between 1-500
        int randNonStandardLength = rand.nextInt(1001) + 1; // Weight between 1-1000
        Package randPackage;

        //Create random package
        if (randPackageType == 0) { //Small Package
            randPackage = new SmallPackage(Priority.values()[randPriorityType], generateAddress(), generateAddress(), randAcknowledge);
            hub.branches.get(randPackage.getSenderAddress().getZip()).getListPackages().add(randPackage); //Move package to sender local branch
            this.packages.add(randPackage); //Add package to main packages list
        }
        if (randPackageType == 1) { //Standard Package
            randPackage = new StandardPackage(Priority.values()[randPriorityType], generateAddress(), generateAddress(), randStandardWeight);
            hub.branches.get(randPackage.getSenderAddress().getZip()).getListPackages().add(randPackage); //Move package to sender local branch
            this.packages.add(randPackage); //Add package to main packages list
        }
        if (randPackageType == 2) { //NonStandard Package
            randPackage = new NonStandardPackage(Priority.values()[randPriorityType], generateAddress(), generateAddress(), randNonStandardWidth, randNonStandardLength, randNonStandardHeight);
            hub.branches.get(0).getListPackages().add(randPackage); //Move package to HUB
            this.packages.add(randPackage); //Add package to main packages list
        }
    }

    /**
     * <p>Convert int value into clock format</p>
     * @return String clock format (MM:SS).
     * @since 1.0
     */
    protected String clockString() { //Create clock template
        int minutes = ((this.clock % 86400) % 3600) / 60;
        int seconds = ((this.clock % 86400) % 3600) % 60;
        if (minutes < 10)
            if (seconds < 10)
                return String.format("0%s:0%s", minutes, seconds);
            else
                return String.format("0%s:%s", minutes, seconds);
        else if (seconds < 10)
            return String.format("%s:0%s", minutes, seconds);
        else
            return String.format("%s:%s", minutes, seconds);
    }

    //Getters & Setters
    /**
     * <p>Get Hub object</p>
     * @return Hub object.
     * @since 1.0
     */
    protected Hub getHub() {
        return hub;
    }

    /**
     * <p>Set Hub object</p>
     * @param hub  (Hub object).
     * @since 1.0
     */
    protected void setHub(Hub hub) {
        this.hub = hub;
    }

    /**
     * <p>Get Hub's list of packages</p>
     * @return (list of packages).
     * @since 1.0
     */
    protected ArrayList<Package> getPackages() {
        return packages;
    }

    /**
     * <p>Set Hub's list of packages</p>
     * @param packages  (list of packages).
     * @since 1.0
     */
    protected void setPackages(ArrayList<Package> packages) {
        this.packages = packages;
    }

    /**
     * <p>Set program clock</p>
     * @param clock  (program clock).
     * @since 1.0
     */
    protected void setClock(int clock) {
        this.clock = clock;
    }

    /**
     * <p>Get program clock</p>
     * @return (program clock).
     * @since 1.0
     */
    protected static int getClock() {
        return id;
    }

    //Overriders

    /**
     * <p>Output object details</p>
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "MainOffice{" +
                "clock=" + clock +
                ", hub=" + hub +
                ", packages=" + packages +
                '}';
    }

    /**
     * <p>MainOffice objects comparison</p>
     * @param obj (MainOffice object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof MainOffice))
            return false;

        return true;
    }

    //Static Methods
    /**
     * <p>Get current running program clock</p>
     * @return current program clock.
     * @since 1.0
     */
    protected static int getId() {
        return id;
    }
}
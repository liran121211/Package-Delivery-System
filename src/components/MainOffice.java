/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package components;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This department manages the entire system.
 * Operates the system clock, the branches and vehicles.
 * Simulates packages and transfers them to the appropriate branches and customers.
 *
 * @author Liran Smadja, Tamar Aminov
 */


public class MainOffice implements PropertyChangeListener, ReadWriteLock, Cloneable {
    //Statics
    private final static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final static Lock writeLock = readWriteLock.writeLock();
    private final static Lock readLock = readWriteLock.readLock();
    private final static Random rand = new Random();
    private static final File trackingFile = new File("tracking.txt");
    private static int id = 0; //Use as package wide clock
    private static int lineNumber = 1;
    private static volatile MainOffice office;

    //Attributes
    private int clock;
    private boolean isSuspended;
    private Hub hub;
    private CopyOnWriteArrayList<Package> packages;
    private CopyOnWriteArrayList<Customer> customers;
    private ScheduledThreadPoolExecutor customerThreadPool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
    private CopyOnWriteArrayList<Thread> threads;

    //Constructors
    private MainOffice() {
    }

    /**
     * <p>A work unit performed by a branch at every second of the system clock</p>
     * All packages in the Vans with the "BRANCH_STORAGE" are taken back to the branch.
     * A package with "CREATION" will be collected with a Van.
     * A package with "DELIVERY" will be delivered with a Van.
     *
     * @since 1.1
     */
    @Deprecated
    protected MainOffice clone() throws CloneNotSupportedException {
        MainOffice tempMainOffice = null;
        CopyOnWriteArrayList<Package> tempPackages = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Customer> tempCustomers = new CopyOnWriteArrayList<>();
        try {
            tempMainOffice = (MainOffice) super.clone();
            tempMainOffice.clock = this.clock;
            tempMainOffice.isSuspended = this.isSuspended;
            tempMainOffice.hub = this.hub.clone();
            for (Package aPackage : this.packages) {
                if (aPackage instanceof SmallPackage)
                    tempPackages.add(((SmallPackage) aPackage).clone());
                if (aPackage instanceof StandardPackage)
                    tempPackages.add(((StandardPackage) aPackage).clone());
                if (aPackage instanceof NonStandardPackage)
                    tempPackages.add(((NonStandardPackage) aPackage).clone());
            }
            for (Customer aCustomer : this.customers)
                tempCustomers.add(aCustomer.clone());
            tempMainOffice.packages = tempPackages;
            tempMainOffice.customers = tempCustomers;
            tempMainOffice.customerThreadPool = new ScheduledThreadPoolExecutor(2);
        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning MainOffice object!");
        }
        return tempMainOffice;
    }

    /**
     * <p>Initialize Hub, Branches and Trucks</p>
     * for sorting center (HUB), (trucksForBranch) amount of turcks are made
     * For every local branch that made, a (trucksForBranch) amount of vans are made.
     *
     * @param branches        (amount of branches)
     * @param trucksForBranch (amount of trucks)
     * @since 1.1
     */
    protected void init(int branches, int trucksForBranch) {
        FileExistence(); //if file exist re-create, else create.
        this.packages = new CopyOnWriteArrayList<>();
        this.customers = new CopyOnWriteArrayList<>();
        this.threads = new CopyOnWriteArrayList<>();
        this.hub = new Hub();
        this.clock = id;
        this.isSuspended = false;

        hub.addPropertyChangeListener(this); //EventListener
        //Add StandardTrucks and 1 NonStandardTruck to HUB
        for (int i = 0; i < trucksForBranch; i++) {
            StandardTruck tempStandardTruck = new StandardTruck();
            hub.getBranches().get(0).getListTrucks().add(tempStandardTruck);
            tempStandardTruck.addPropertyChangeListener(this); //EventListener
        }

        NonStandardTruck tempNonStandardTruck = new NonStandardTruck();
        hub.getBranches().get(0).getListTrucks().add(tempNonStandardTruck);
        tempNonStandardTruck.addPropertyChangeListener(this);//EventListener

        //Initialize Branches with Vans
        for (int i = 1; i <= branches; i++) {
            Branch tempBranch = new Branch();
            hub.getBranches().add(tempBranch);
            tempBranch.addPropertyChangeListener(this);//EventListener

            for (int j = 0; j < trucksForBranch; j++) {
                Van tempVan = new Van();
                hub.getBranches().get(i).getListTrucks().add(tempVan);
                tempVan.addPropertyChangeListener(this); //EventListener
                tempBranch.addPropertyChangeListener(tempVan); //EventListener
            }
        }
        for (int i = 0; i < 10; i++) {
            Address temp = generateAddress();
            customers.add(new Customer(new Address(temp.getZip(), temp.getStreet())));
        }
    }

    //Methods

    /**
     * @return (office) Singleton Double-Check-Locking MainOffice instance
     * @since 1.2
     */
    protected static MainOffice getInstance() {
        if (office == null) {
            synchronized (MainOffice.class) {
                if (office == null) {
                    office = new MainOffice();
                }
            }
        }
        return office;
    }

    /**
     * <p>Repeat (infinite) number of iterations that the system will perform and activates.</p>
     * At the end of loop. Prints all the tracking history of all packages
     *
     * @since 1.1
     */
    public void play() {
        System.out.println("========================== START ==========================");
        startThreads();
        while (true) {
            try {
                if (!isSuspended) {
                    tick();
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                System.out.println("Clock Thread interrupted");
            }

        }
    }

    /**
     * Interrupt all of threads
     * If this thread is blocked in an invocation of the wait(), wait(long), or wait(long, int), This method will finish its life cycle
     *
     * @since 1.2
     */
    protected synchronized void interruptThreads() {
        for (Thread t : threads)
            t.interrupt();
    }


    /**
     * Set isTerminated status to True for all Threads classes.
     *
     * @since 1.2
     */
    protected synchronized void terminateThreads() {

        this.hub.setIsTerminated(true); //HUB iteration
        for (int i = 0; i < this.hub.getBranches().size(); i++) {
            this.hub.getBranches().get(i).setIsTerminated(true);//Branches iteration

            for (int j = 0; j < this.hub.getBranches().get(i).getListTrucks().size(); j++) {

                if (this.hub.getBranches().get(i).getListTrucks().get(j) instanceof StandardTruck)
                    ((StandardTruck) this.hub.getBranches().get(i).getListTrucks().get(j)).setIsTerminated(true); //StandardTruck iteration

                if (this.hub.getBranches().get(i).getListTrucks().get(j) instanceof Van)
                    ((Van) this.hub.getBranches().get(i).getListTrucks().get(j)).setIsTerminated(true); //StandardTruck iteration

                if (this.hub.getBranches().get(i).getListTrucks().get(j) instanceof NonStandardTruck)
                    ((NonStandardTruck) this.hub.getBranches().get(i).getListTrucks().get(j)).setIsTerminated(true); //StandardTruck iteration
            }
        }
        for (int k = 0; k < 10; k++)
            MainOffice.getInstance().getCustomers().get(k).setIsTerminated(true);
    }

    /**
     * Set isTerminated status to False for all Threads classes.
     *
     * @since 1.2
     */
    protected synchronized void startThreads() {
        Thread tempThread;

        hub.setIsTerminated(false);
        tempThread = new Thread(hub); //HUB thread initiation
        threads.add(tempThread);
        tempThread.start();
        for (int i = 0; i < hub.getBranches().size(); i++) {

            hub.getBranches().get(i).setIsTerminated(false);
            tempThread = new Thread(hub.getBranches().get(i)); //Branch thread initiation
            threads.add(tempThread);
            tempThread.start();
            for (int j = 0; j < hub.getBranches().get(i).getListTrucks().size(); j++) {

                if (hub.getBranches().get(i).getListTrucks().get(j) instanceof Van) {
                    ((Van) hub.getBranches().get(i).getListTrucks().get(j)).setIsTerminated(false);
                    tempThread = new Thread((Van) hub.getBranches().get(i).getListTrucks().get(j)); //Van thread initiation
                    threads.add(tempThread);
                    tempThread.start();
                }

                if (hub.getBranches().get(i).getListTrucks().get(j) instanceof StandardTruck) {
                    ((StandardTruck) hub.getBranches().get(i).getListTrucks().get(j)).setIsTerminated(false);
                    tempThread = new Thread((StandardTruck) hub.getBranches().get(i).getListTrucks().get(j)); //StandardTruck thread initiation
                    threads.add(tempThread);
                    tempThread.start();
                }

                if (hub.getBranches().get(i).getListTrucks().get(j) instanceof NonStandardTruck) {
                    ((NonStandardTruck) hub.getBranches().get(i).getListTrucks().get(j)).setIsTerminated(false);
                    tempThread = new Thread((NonStandardTruck) hub.getBranches().get(i).getListTrucks().get(j)); //NonStandardTruck thread initiation
                    threads.add(tempThread);
                    tempThread.start();
                }
            }
        }
        for (Customer c : customers) {
            c.setIsTerminated(false);
            tempThread = new Thread(c); //Customer thread initiation
            threads.add(tempThread);
            customerThreadPool.scheduleWithFixedDelay(new Thread(c), 100, 100, TimeUnit.MILLISECONDS);
        }

    }

    /**
     * <p>Print and advance the clock time</p>
     *
     * @since 1.1
     */
    protected void tick() {
        System.out.println(clockString());
        this.clock = id;
        id++;
    }

    //Methods

    /**
     * <p>Generate random address</p>
     *
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

    /**
     * Print all tracking of the packages that were made in the current run.
     *
     * @since 1.0
     */
    protected void printReport() { //Print all packages timeline
        for (Package aPackage : this.packages) {
            aPackage.printTracking();
        }
    }

    /**
     * <p>Generate random type of package</p>
     *
     * @since 1.0
     */
    protected void addPackage(Customer customer) {
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
            randPackage = new SmallPackage(Priority.values()[randPriorityType], customer.getAddress(), generateAddress(), randAcknowledge);
            hub.getBranches().get(randPackage.getSenderAddress().getZip()).getListPackages().add(randPackage); //Move package to sender local branch
            hub.getBranches().get(randPackage.getSenderAddress().getZip()).getSupport().firePropertyChange("Package #" + randPackage.getPackageID(), null, Status.CREATION);
            this.packages.add(randPackage); //Add package to main packages list
        }
        if (randPackageType == 1) { //Standard Package
            randPackage = new StandardPackage(Priority.values()[randPriorityType], customer.getAddress(), generateAddress(), randStandardWeight);
            hub.getBranches().get(randPackage.getSenderAddress().getZip()).getListPackages().add(randPackage); //Move package to sender local branch
            hub.getBranches().get(randPackage.getSenderAddress().getZip()).getSupport().firePropertyChange("Package #" + randPackage.getPackageID(), null, Status.CREATION);
            this.packages.add(randPackage); //Add package to main packages list
        }
        if (randPackageType == 2) { //NonStandard Package
            randPackage = new NonStandardPackage(Priority.values()[randPriorityType], customer.getAddress(), generateAddress(), randNonStandardWidth, randNonStandardLength, randNonStandardHeight);
            hub.getBranches().get(0).getListPackages().add(randPackage); //Move package to HUB
            hub.getSupport().firePropertyChange("Package #" + randPackage.getPackageID(), null, Status.CREATION);
            this.packages.add(randPackage); //Add package to main packages list
        }
    }

    /**
     * <p>Convert int value into clock format</p>
     *
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
     *
     * @return Hub object.
     * @since 1.0
     */
    protected Hub getHub() {
        return hub;
    }

    /**
     * <p>Set Hub object</p>
     *
     * @param hub (Hub object).
     * @since 1.0
     */
    protected void setHub(Hub hub) {
        this.hub = hub;
    }

    /**
     * <p>Get Hub's list of packages</p>
     *
     * @return (list of packages).
     * @since 1.0
     */
    protected CopyOnWriteArrayList<Package> getPackages() {
        return packages;
    }

    /**
     * <p>Set Hub's list of packages</p>
     *
     * @param packages (list of packages).
     * @since 1.0
     */
    protected void setPackages(CopyOnWriteArrayList<Package> packages) {
        this.packages = packages;
    }

    /**
     * <p>Set program clock</p>
     *
     * @param clock (program clock).
     * @since 1.0
     */
    protected void setClock(int clock) {
        this.clock = clock;
    }

    /**
     * <p>Get program clock</p>
     *
     * @return (program clock).
     * @since 1.0
     */
    protected int getClock() {
        return id;
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
        return "MainOffice{" +
                "clock=" + clock +
                ", hub=" + hub +
                ", packages=" + packages +
                '}';
    }

    /**
     * <p>MainOffice objects comparison</p>
     *
     * @param obj (MainOffice object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        return obj instanceof MainOffice;
    }

    //Static Methods

    /**
     * <p>Get current running program clock</p>
     *
     * @return current program clock.
     * @since 1.0
     */
    protected static int getId() {
        return id;
    }

    /**
     * <p>Suspend thread activity</p>
     *
     * @since 1.1
     */
    protected synchronized void setSuspend() {
        this.isSuspended = true;
    }

    protected boolean isSuspended() {
        return isSuspended;
    }

    /**
     * <p>Resume thread activity</p>
     *
     * @since 1.1
     */
    protected synchronized void setResume() {
        this.isSuspended = false;
    }

    /**
     * Get customers array
     *
     * @return customers array.
     */
    protected CopyOnWriteArrayList<Customer> getCustomers() {
        return customers;
    }

    /**
     * <p>Set thread suspension</p>
     *
     * @since 1.2
     */
    protected void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }

    /**
     * Get customers array
     *
     * @param customers array.
     * @since 1.2
     */
    protected void setCustomers(CopyOnWriteArrayList<Customer> customers) {
        this.customers = customers;
    }

    /**
     * Set ThreadPool of customers threads.
     *
     * @param customerThreadPool (customers threads).
     * @since 1.2
     */
    protected void setCustomerThreadPool(ScheduledThreadPoolExecutor customerThreadPool) {
        this.customerThreadPool = customerThreadPool;
    }

    /**
     * Get tracking file object.
     *
     * @return (trackingFile) object.
     * @since 1.2
     */
    protected static File getTrackingFile() {
        return trackingFile;
    }

    /**
     * check if (tracking.txt) is exist.
     *
     * @since 1.2
     */
    private void FileExistence() {
        if (trackingFile.exists()) {
            try {
                FileWriter file = new FileWriter(trackingFile, false);
                file.write("");
            } catch (IOException e) {
                System.out.println("File writing error!");
            }
        }
    }

    /**
     * Get ThreadPool of customers threads.
     *
     * @return (customerThreadPool) customer threads.
     */
    protected ScheduledThreadPoolExecutor getCustomerThreadPool() {
        return customerThreadPool;
    }

    /**
     * Write a new string line to tracking.txt file.
     *
     * @param value (String value)
     * @since 1.2
     */
    private static void writeLock(Object value) {
        writeLock.lock();
        try {
            try {
                FileWriter w = new FileWriter(trackingFile, true);
                w.write("\n" + lineNumber + ") " + value);
                lineNumber++;
                w.close(); //Close File
            } catch (IOException e) {
                System.out.println("File writing error!");
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Read the tracking.txt file.
     *
     * @param packageID (package ID)
     * @return (read lock).
     * @since 1.2
     */
    protected static boolean readLock(Object packageID) {
        readLock.lock();
        try {
            try {
                Scanner trackingFileReader = new Scanner(trackingFile);
                while (trackingFileReader.hasNextLine()) {
                    String line = trackingFileReader.nextLine();
                    if (line.contains((CharSequence) packageID) && line.contains("DELIVERED"))
                        return true;
                }
                trackingFileReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } finally {
            readLock.unlock();
        }
        return false;
    }

    /**
     * Get event of status change of a package.
     *
     * @param evt (package event).
     * @since 1.2
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().contains("#"))
            writeLock(evt.getPropertyName() + ": " + evt.getNewValue());
    }

    /**
     * Get read lock.
     *
     * @return readLock (lock object).
     * @since 1.2
     */
    @Override
    public Lock readLock() {
        return readLock;
    }

    /**
     * Get read lock.
     *
     * @return writeLock (lock object).
     * @since 1.2
     */
    @Override
    public Lock writeLock() {
        return writeLock;
    }

}

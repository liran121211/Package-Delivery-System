/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package components;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Represent a checkpoint of all objects in the system.
 * Memento will contain a cloned objects data of specific time.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class Memento {
    private Hub hub;
    private int clockHUB;
    private boolean isSuspendHUB;
    private int truckSize, branchSize;
    private CopyOnWriteArrayList<Package> packagesHUB;
    private CopyOnWriteArrayList<Customer> customersHUB;
    private ScheduledThreadPoolExecutor customerThreadPoolHUB = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);

    protected Memento(MainOffice mainOffice, int truckSize, int branchSize) {
        CopyOnWriteArrayList<Package> tempPackages = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Customer> tempCustomers = new CopyOnWriteArrayList<>();
        try {
            this.truckSize = truckSize;
            this.branchSize = branchSize;
            this.hub = mainOffice.getHub().clone();
            this.clockHUB = mainOffice.getClock();
            this.isSuspendHUB = mainOffice.isSuspended();
            for (Package aPackage : mainOffice.getPackages()) {
                if (aPackage instanceof SmallPackage)
                    tempPackages.add(((SmallPackage) aPackage).clone());
                if (aPackage instanceof StandardPackage)
                    tempPackages.add(((StandardPackage) aPackage).clone());
                if (aPackage instanceof NonStandardPackage)
                    tempPackages.add(((NonStandardPackage) aPackage).clone());
            }
            for (Customer aCustomer : mainOffice.getCustomers())
                tempCustomers.add(aCustomer.clone());
            this.customersHUB = tempCustomers;
            this.packagesHUB = tempPackages;
            this.customerThreadPoolHUB = mainOffice.getCustomerThreadPool();
        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning Memento object!");
        }
    }

    /**
     * Get HUB object
     * @return (hub) object
     *      * @since 1.2
     */
    protected Hub getHub() {
        return hub;
    }

    /**
     * Get HUB current clock.
     * @return (clockHUB) current system time.
     *      * @since 1.2
     */
    protected int getClockHUB() {
        return clockHUB;
    }

    /**
     * Get HUB suspended status.
     * @return (isSuspendHUB) current suspended status.
     *      * @since 1.2
     */
    protected boolean isSuspendHUB() {
        return isSuspendHUB;
    }

    /**
     * Get packages array of the system .
     * @return (packagesHUB) packages list.
     * @since 1.2
     */
    protected CopyOnWriteArrayList<Package> getPackagesHUB() {
        return packagesHUB;
    }

    /**
     * Get customers array of the system .
     * @return (customersHUB) customers list.
     * @since 1.2
     */
    protected CopyOnWriteArrayList<Customer> getCustomersHUB() {
        return customersHUB;
    }

    /**
     * Get customers array of threads (ThreadPool) .
     * @return (getCustomerThreadPoolHUB) customers ThreadPool threads list.
     * @since 1.2
     */
    protected ScheduledThreadPoolExecutor getCustomerThreadPoolHUB() {
        return customerThreadPoolHUB;
    }

    /**
     * Get system amount of trucks.
     * @return (truckSize) amount of trucks.
     * @since 1.2
     */
    protected int getTruckSize() {
        return truckSize;
    }

    /**
     * Set system amount of trucks.
     * @param  (truckSize) amount of trucks.
     * @since 1.2
     */
    protected void setTruckSize(int truckSize) {
        this.truckSize = truckSize;
    }

    /**
     * Set system amount of branches.
     * @return  (branchSize) amount of branches.
     * @since 1.2
     */
    protected int getBranchSize() {
        return branchSize;
    }

    /**
     * Set system amount of branches.
     * @param (branchSize) amount of branches.
     * @since 1.2
     */
    protected void setBranchSize(int branchSize) {
        this.branchSize = branchSize;
    }
}

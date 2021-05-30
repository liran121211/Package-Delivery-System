/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represent a customer with packages and address.
 * Every customer generate 5 packages .
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class Customer implements Runnable, Cloneable {
    //Statics
    private static PostFrame postFrame;
    private static int uid;

    //Attributes
    private String serial;
    private Address address;
    private int id, countSentPackages, countDeliveredPackages;
    private boolean isSuspended;
    private boolean packagesSent;
    private boolean packagesDelivered;
    private Boolean isTerminated;
    private CopyOnWriteArrayList<Package> customerPackages;


    protected Customer(Address address) {
        this.customerPackages = new CopyOnWriteArrayList<>();
        this.isSuspended = false;
        this.isTerminated = false;
        this.packagesSent = false;
        this.packagesDelivered = false;
        this.address = address;
        this.serial = randString();
        this.countSentPackages = 0;
        this.countDeliveredPackages = 0;
        this.id = uid++;
    }


    @Override
    protected Customer clone() throws CloneNotSupportedException {
        Customer tempCustomer = null;
        CopyOnWriteArrayList<Package> tempPackages = new CopyOnWriteArrayList<>();
        try {
            tempCustomer = (Customer) super.clone();
            tempCustomer.serial = this.serial;
            tempCustomer.address = this.address.clone();
            tempCustomer.id = this.id;
            tempCustomer.isTerminated = this.isTerminated;
            tempCustomer.isSuspended = this.isSuspended;
            tempCustomer.countSentPackages = this.countSentPackages;
            tempCustomer.countDeliveredPackages = this.countDeliveredPackages;
            tempCustomer.packagesSent = this.packagesSent;
            tempCustomer.packagesDelivered = this.packagesDelivered;
            for (Package aPackage : this.customerPackages) {
                if (aPackage instanceof SmallPackage)
                    tempPackages.add(((SmallPackage) aPackage).clone());
                if (aPackage instanceof StandardPackage)
                    tempPackages.add(((StandardPackage) aPackage).clone());
                if (aPackage instanceof NonStandardPackage)
                    tempPackages.add(((NonStandardPackage) aPackage).clone());
            }
            tempCustomer.customerPackages = tempPackages;
        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning Customer object!");
        }
        return tempCustomer;
    }

    /**
     * <p>Create new package every 5 seconds</p>
     *
     * @since 1.2
     */
    protected void packagesWorker(Customer customer, PostFrame postFrame, int packageCount) {
        SwingWorker<Object, Void> packagesWorker = new SwingWorker<>() {
            @Override
            public Object doInBackground() {
                if (!isSuspended && !isTerminated) {
                    MainOffice.getInstance().addPackage(customer);
                    Package latestPackage = MainOffice.getInstance().getPackages().get(MainOffice.getInstance().getPackages().size() - 1);
                    customerPackages.add(latestPackage);

                    BranchGUI sBranch = postFrame.getBranchGui(latestPackage.getSenderAddress().getZip());
                    BranchGUI dBranch = postFrame.getBranchGui(latestPackage.getDestinationAddress().getZip());
                    PackageGUI upper = new PackageGUI(100 + (900 / 5) * packageCount + (id * 70), 10);
                    PackageGUI lower = new PackageGUI(100 + (900 / 5) * packageCount + (id * 70), 580);
                    ((PostPanel) postFrame.getSystemPane()).getJcomponents().add(upper);
                    ((PostPanel) postFrame.getSystemPane()).getJcomponents().add(lower);
                    upper.changeColor("DARK_RED");

                    if (latestPackage instanceof NonStandardPackage) {
                        upper.setPointTo(new LineGUI(Color.red, upper.getX() + 25, upper.getY() + 25, 1740, 200));
                        lower.setPointTo(new LineGUI(Color.red, 115 + (900 / 5) * packageCount + (id * 70), 580, 115 + (900 / 5) * packageCount + (id * 70), 40));
                    } else {
                        upper.setPointTo(new LineGUI(Color.blue, 110 + (900 / 5) * packageCount + (id * 70), 40, dBranch.getX() + 25, dBranch.getY()));
                        lower.setPointTo(new LineGUI(Color.blue, 115 + (900 / 5) * packageCount + (id * 70), 580, sBranch.getX() + 10, sBranch.getY() + 30));

                    }
                    latestPackage.setSenderPackage(upper);
                    latestPackage.setDestinationPackage(lower);
                    postFrame.getMainFrame().repaint();
                }
                return null;
            }
        };
        packagesWorker.execute();
    }

    /**
     * Set customer Address
     *
     * @return address object
     * @since 1.2
     */
    protected Address getAddress() {
        return address;
    }

    /**
     * Create random string contains Capital/Non Capitcal letters
     *
     * @return String
     * @since 1.2
     */
    private String randString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * set PostFrame object for packagesGUI creation.
     *
     * @param (postFrame) object
     * @since 1.2
     */
    protected static void setPostFrame(PostFrame postFrame) {
        Customer.postFrame = postFrame;
    }

    /**
     * Get customer ID.
     *
     * @return customer ID.
     */
    public int getId() {
        return id;
    }

    /**
     * <p>Suspend thread activity</p>
     *
     * @since 1.2
     */
    protected synchronized void setSuspend() {
        this.isSuspended = true;
    }

    /**
     * <p>Resume thread activity</p>
     *
     * @since 1.2
     */
    protected synchronized void setResume() {
        this.isSuspended = false;
        this.notify();
    }

    /**
     * Get isTerminated status
     *
     * @return (isTerminated) status.
     * @since 1.2
     */
    protected Boolean getIsTerminated() {
        return isTerminated;
    }

    /**
     * Set isTerminated status
     *
     * @param (isTerminated) status.
     */
    protected void setIsTerminated(Boolean isTerminated) {
        this.isTerminated = isTerminated;
    }

    /**
     * Get packages of specific customers.
     * @return (customerPackages) object
     * @since 1.2
     */
    protected CopyOnWriteArrayList<Package> getCustomerPackages() {
        return customerPackages;
    }


    /*
     * <p>Start thread activity</p>
     *
     * @since 1.1
     */
    @Override
    public void run() { //ThreadPool Method
        if (!packagesDelivered) {
            while (!packagesSent && !isTerminated) {
                try {

                    //<---Suspended status handle--->
                    if (isSuspended)
                    {
                        try {
                            synchronized (this) {
                                wait();
                            }
                        } catch (InterruptedException ignored) { }
                    }
                    //<---Suspended status handle--->

                    //<---New Package creation handle--->
                    Thread.sleep(new Random().nextInt(3000) + 2000);
                    packagesWorker(this, postFrame, countSentPackages);
                    countSentPackages++;
                } catch (InterruptedException ignored) { }
                //<---New Package creation handle--->

                if (countSentPackages == 5) //Check if customer sent all 5 packages.
                    packagesSent = true;
            }
            try {
                //<---Check delivered Packages handle--->
                    if (!isTerminated && !isSuspended){
                        synchronized (this) { //Look up on tracking.txt to see if all packages DELIVERED.
                            Thread.sleep(10000);
                            for (Package aPackage : customerPackages) {
                                if (MainOffice.readLock("#" + aPackage.getPackageID())) //Check if current package DELIVERED.
                                    countDeliveredPackages++;
                            }
                            if (countDeliveredPackages == 5) //Check if all packages already delivered
                                packagesDelivered = true;
                        }
                    }
            } catch (InterruptedException ignored) { }
            //<---Check delivered Packages handle--->

        } else
            System.out.println("Customer #" + serial + " already delivered all of his packages!");
    }
}
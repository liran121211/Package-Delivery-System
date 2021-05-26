/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;
import java.util.ArrayList;
import java.util.Random;

/**
 * Represent a local branch.
 * Maintains a list of local branches.
 * The HUB has trucks that collect and deliver the packages from and to the local branches.
 * @author Liran Smadja
 */

public class Hub implements Node {

    //Attributes
    ArrayList<Branch> branches;

    //Constructors
    public Hub() {
        branches = new ArrayList<>();
        branches.add(new Branch("HUB"));
    }

    //Overriders
    /**
     * <p>StandardTruck is sent to the local branch to pick up the packages</p>
     * StandardTruck is loaded with the destination packages.
     * The packages tracking and statuses are updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void collectPackage(Package p) {
        Branch tempBranch = this.getBranches().get(0); // This HUB
        int zip = p.getDestinationAddress().getZip() - 1; //Destination zip
        Truck tempTruck = tempBranch.getListTrucks().get(zip); //Get correlated truck match to brunch
        System.out.println("StandardTruck " + tempTruck.getTruckID() + " loaded packages at HUB");
        p.setStatus(Status.BRANCH_TRANSPORT); //TRACKING
        p.addTracking((StandardTruck) tempTruck, Status.BRANCH_TRANSPORT); //TRACKING
        tempTruck.getPackages().add(p); //Add package HUB ---> StandardTruck
        tempBranch.getListPackages().remove(p); //Remove package HUB ---> StandardTruck

    }

    /**
     * <p>NonStandardTruck is sent to the customer's address to deliver the package </p>
     * NonStandardTruck is loaded with the destination packages.
     * The packages tracking and statuses are updated.
     * @param p (package object to collect)
     * @since 1.0
     */
    @Override
    public void deliverPackage(Package p) {
        Branch tempBranch = this.getBranches().get(0); // This HUB
        Truck tempTruck = tempBranch.getListTrucks().get(tempBranch.getListTrucks().size()-1); //Get NonStandardTruck

        if (p.getStatus() == Status.CREATION && p instanceof NonStandardPackage) { //check if package is for NonStandardTruck
            p.setStatus(Status.COLLECTION); //TRACKING
            p.addTracking((NonStandardTruck) tempTruck, Status.COLLECTION); //TRACKING
            tempTruck.getPackages().add(p); //Add package HUB ---> NonStandardTruck
            tempBranch.getListPackages().remove(p); //Remove package HUB
            tempTruck.setTimeLeft(new Random().nextInt(11) + 1); //Time set for customer travel
            tempTruck.setAvailable(false);
            System.out.println("NonStandardTruck " + tempTruck.getTruckID() + " loaded packages at HUB");
        }
    }

    @Override
    /**
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
            Truck currentTruck = thisHub.getListTrucks().get(i); // Make the code readable

            if (currentTruck.isAvailable() && currentTruck.getTimeLeft() == 0 && currentTruck instanceof StandardTruck) { //Check if Truck returned from local branch
                ((StandardTruck) currentTruck).setDestination(this.getBranches().get(i + 1));//Correlate branch number with the truck (i)
                currentTruck.setTimeLeft(new Random().nextInt(11) + 1); //Time set for local branch travel
                currentTruck.setAvailable(false);

                for (int j = 0; j < thisHub.getListPackages().size(); j++) { //Load all packages in HUB to StandardTrucks
                    if (thisHub.getListPackages().get(j).getDestinationAddress().getZip() == i + 1 && !(thisHub.getListPackages().get(j) instanceof NonStandardPackage)) //Correlate package ID to Truck ID
                        if (destinationWeight(thisHub) < ((StandardTruck) currentTruck).getMaxWeight()) //Check if max weight exceeded
                            collectPackage(thisHub.getListPackages().get(j));
                }


                for (int k = 0; k < currentTruck.getPackages().size(); k++) { //Unload all packages in StandardTrucks to HUB
                    if (currentTruck.getPackages().get(k).getStatus() == Status.HUB_TRANSPORT) { //Check if package the package came from local branch
                        currentTruck.getPackages().get(k).setStatus(Status.HUB_STORAGE); //TRACKING
                        currentTruck.getPackages().get(k).addTracking(thisHub, Status.HUB_STORAGE); //TRACKING
                        thisHub.getListPackages().add(currentTruck.getPackages().get(k)); //Add package from StandardTruck ---> HUB
                        currentTruck.getPackages().remove(currentTruck.getPackages().get(k)); //Remove package from StandardTruck
                        System.out.println("StandardTruck " + currentTruck.getTruckID() + " unloaded packages at HUB");
                    }
                }
            } else if (currentTruck.isAvailable() && currentTruck instanceof NonStandardTruck) { // check if truck is NonStandard
                for (int j = 0; j < thisHub.getListPackages().size(); j++) //Load all packages in HUB to NonStandardTruck
                    if (isFit((NonStandardTruck) currentTruck, thisHub.getListPackages().get(j)))
                        deliverPackage(thisHub.getListPackages().get(j));
            }
        }
    }

    //Methods
    /**
     * <p>Get destination's weight of packages</p>
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
     * @param nst (Branch object)
     * @param p (package object)
     * @return True if Fit else, False.
     * @since 1.0
     */
    private boolean isFit(NonStandardTruck nst, Package p) { // check of package if the NonStandardTruck
        if (p instanceof NonStandardPackage) {
            NonStandardPackage nsp = (NonStandardPackage) p;
            if (nsp.getHeight() < nst.getHeight() && nsp.getWidth() < nst.getWidth() && nsp.getLength() < nst.getLength())
                return true;
        }
        return false;
    }

    //Getters & Setters
    /**
     * <p>Get branches list</p>
     * @return branches list.
     * @since 1.0
     */
    protected ArrayList<Branch> getBranches() {
        return branches;
    }

    /**
     * <p>Set Branches list</p>
     * @param branches (Array list).
     * @since 1.0
     */
    protected void setBranches(ArrayList<Branch> branches) {
        this.branches = branches;
    }

    //Overriders
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

        if (!(obj instanceof Hub))
            return false;

        //Deep Comparison
        for (int i = 0; i < this.branches.size(); i++)
            if (!this.branches.get(i).equals(obj))
                return false;

        return true;
    }

    /**
     * <p>Output object details</p>
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "Hub{" +
                "branches=" + branches +
                '}';
    }
}

/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;
import java.util.ArrayList;
import java.util.Random;

/**
 * Represent the Trucks for transporting packages.
 * @author Liran Smadja, Tamar Aminov
 */

public class Truck {

    //Statics
    private final static Random rand = new Random();
    private static int id = 2000;

    //Attributes
    private int truckID;
    private String licensePlate;
    private String truckModel;
    private boolean available;
    private int timeLeft;
    private int keepTime;
    private ArrayList<Package> packages;

    //Constructors
    public Truck() {
        packages = new ArrayList<>();
        this.licensePlate = (rand.nextInt(900) + 100) + "-" + (rand.nextInt(90) + 10) + "-" + (rand.nextInt(900) + 100);
        this.truckModel = "M" + rand.nextInt(5);
        this.available = true;
        this.timeLeft = 0;
        this.truckID = id;
        id++;
    }

    public Truck(String licensePlate, String truckModel) {
        packages = new ArrayList<>();
        this.licensePlate = licensePlate;
        this.truckModel = truckModel;
        this.available = true;
        this.timeLeft = 0;
        this.truckID = id;
        id++;
    }

    //Methods
    //TODO (on demand)

    //Getters & Setters
    /**
     * <p>Get Truck identifier</p>
     * @return Truck identifier.
     * @since 1.0
     */
    protected int getTruckID() {
        return truckID;
    }

    /**
     * <p>Set Truck identifier</p>
     * @param truckID  (Truck identifier).
     * @since 1.0
     */
    protected void setTruckID(int truckID) {
        this.truckID = truckID;
    }

    /**
     * <p>Get Truck License Plate</p>
     * @return Truck License Plate.
     * @since 1.0
     */
    protected String getLicensePlate() {
        return licensePlate;
    }

    /**
     * <p>Set Truck License Plate</p>
     * @param licensePlate  (Truck identifier).
     * @since 1.0
     */
    protected void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /**
     * <p>Get Truck Model</p>
     * @return Truck Model.
     * @since 1.0
     */
    protected String getTruckModel() {
        return truckModel;
    }

    /**
     * <p>Set Truck Truck Model</p>
     * @param truckModel  (Truck Model).
     * @since 1.0
     */
    protected void setTruckModel(String truckModel) {
        this.truckModel = truckModel;
    }

    /**
     * <p>Get Truck Availability</p>
     * @return Truck Availability.
     * @since 1.0
     */
    protected boolean isAvailable() {
        return available;
    }

    /**
     * <p>Set Truck Availability</p>
     * @param available  (Truck Availability).
     * @since 1.0
     */
    protected void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * <p>Get time to reach destination</p>
     * @return Truck's time to reach the destination.
     * @since 1.0
     */
    protected int getTimeLeft() {
        return timeLeft;
    }

    /**
     * <p>Set time to reach destination</p>
     * @param timeLeft  (time to reach destination).
     * @since 1.0
     */
    protected void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    /**
     * <p>Get Truck packages list</p>
     * @return Truck packages list.
     * @since 1.0
     */
    protected ArrayList<Package> getPackages() {
        return packages;
    }

    /**
     * <p>Set Truck packages list</p>
     * @param packages (Truck packages list).
     * @since 1.0
     */
    protected void setPackages(ArrayList<Package> packages) {
        this.packages = packages;
    }

    //Overriders
    /**
     * <p>Output object details</p>
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return " truckID=" + (this.truckID) +
                ", licensePlate='" + licensePlate + '\'' +
                ", truckModel='" + truckModel + '\'' +
                ", available=" + available;
    }

    /**
     * <p>Truck objects comparison</p>
     * @param obj (Truck object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Truck))
            return false;

        //Primitive Comparison
        if (this.truckID != ((Truck) obj).truckID || this.licensePlate.compareTo(((Truck) obj).licensePlate) != 0 || this.truckModel.compareTo(((Truck) obj).truckModel) != 0 || this.available != ((Truck) obj).available || this.timeLeft != ((Truck) obj).timeLeft)
            return false;

        //Deep Comparison
        for (int i = 0; i < this.getPackages().size(); i++)
            if (!this.getPackages().get(i).equals(obj))
                return false;
        return true;
    }

    /**
     * <p>Keep the (Timeleft) variable for line distance calculation</p>
     *
     * @return keepTime (truck timeleft value)
     * @since 1.1
     */
    protected synchronized int getKeepTime() {
        return keepTime;
    }

    /**
     * <p>Keep the (Timeleft) variable for line distance calculation</p>
     *
     * @param keepTime (truck timeleft value)
     * @since 1.1
     */
    protected synchronized void setKeepTime(int keepTime) {
        this.keepTime = keepTime;
    }
}

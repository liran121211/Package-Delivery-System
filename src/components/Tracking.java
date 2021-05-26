/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;
/**
 * Represents a record in the package transfer history.
 * Each package contains a collection of records of this type.
 * Each time the status (and location) of a package is changed a new record is added to the collection
 * Each record includes the time the record was created, The point where the package is located and the status of the package.
 */
public class Tracking implements Cloneable{

    //Attributes
    private int time;
    private Node node; //Package Location
    private Status status;

    //Constructors
    public Tracking(int time, Node node, Status status) {
        this.time = time;
        this.node = node;
        this.status = status;
    }

    //Methods
    @Override
    protected Tracking clone() throws CloneNotSupportedException {
        Tracking tempTracking = null;
        try {
            tempTracking = (Tracking) super.clone();
            tempTracking.time = this.time;
            tempTracking.node = this.node;
            tempTracking.status = this.status;
        } catch (CloneNotSupportedException cns) {
            System.out.println("Error while cloning Tracking object!");
        }
        return tempTracking;
    }

    //Getters & Setters
    /**
     * <p>Get tracking Node (tracking current location)</p>
     * @return tracking Node.
     * @since 1.0
     */
    protected Node getNode() {
        return this.node;
    }

    /**
     * <p>Set tracking Node (tracking current location)</p>
     * @param n  (tracking Node).
     * @since 1.0
     */
    protected void setNode(Node n) {
        this.node = n;
    }

    /**
     * <p>Get Status (tracking current status)</p>
     * @return tracking Status.
     * @since 1.0
     */
    protected Status getStatus() {
        return status;
    }

    /**
     * <p>Update Status (tracking current status)</p>
     * @param status (tracking Status).
     * @since 1.0
     */
    protected void setStatus(Status status) {
        this.status = status;
    }

    /**
     * <p>Get tracking time (tracking current time)</p>
     * @return tracking time.
     * @since 1.0
     */
    protected int getTime() {
        return time;
    }

    /**
     * <p>Set tracking time (tracking current time)</p>
     * @param time  (package Status).
     * @since 1.0
     */
    protected void setTime(int time) {
        this.time = time;
    }

    //Overriders
    /**
     * <p>Output object details</p>
     * @return this object attributes details.
     * @since 1.0
     */
    @Override
    public String toString() {
        return "Tracking{" +
                "time=" + time +
                ", node=" + node +
                ", status=" + status +
                '}';
    }

    /**
     * <p>Tracking objects comparison</p>
     * @param obj (Tracking object)
     * @return True if objects are equal, else False.
     * @since 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Tracking))
            return false;

        return this.time == ((Tracking) obj).time && this.node == ((Tracking) obj).node && this.status == ((Tracking) obj).status;
    }
}

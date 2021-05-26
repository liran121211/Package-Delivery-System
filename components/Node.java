/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

/**
 * Represents the location of a package, can refer to branches and trucks.
 * All points where the package can be in the various stages of its transfer.
 * @author Liran Smadja
 */

public interface Node {
    //Methods
    /**
     * <p>Vehicle is sent to specific location to pick up the packages</p>
     * Handles the collection of a package by the implementing department.
     * @param p (package object)
     * @since 1.0
     */
    public void collectPackage(Package p); //Handles the collection of the package

    /**
     * <p>Vehicle is sent to specific location to deliver the packages</p>
     * Handles the delivery of the package to the next entity in the transfer chain.
     * @param p (package object)
     * @since 1.0
     */
    public void deliverPackage(Package p); //Handles the delivery of the package

    /**
     * <p>performs a work unit, depends on the implemented class</p>
     * @since 1.0
     */
    public void work(); // Work Per Clock


}

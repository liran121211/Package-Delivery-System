/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

/**
 * Status Legend:
 * CREATION - Initial status of every package
 * COLLECTION - Transport vehicle is on the way to collect the package
 * BRANCH_STORAGE - The package collected from a customer arrived at the customer's local branch
 * HUB_TRANSPORT - Package is being delivered from local branch to sorting center
 * BRANCH_TRANSPORT - Package is being delivered from sorting center to it's local branch
 * DELIVERY - Package is held in the local branch for final delivery
 * DISTRIBUTION - Package is being delivered to the customer by the Van
 * DELIVERD - The package has been delivered to the customer
 * @author Liran Smadja, Tamar Aminov
 */
public enum Status {
    CREATION, COLLECTION, BRANCH_STORAGE, HUB_TRANSPORT, HUB_STORAGE, BRANCH_TRANSPORT, DELIVERY, DISTRIBUTION, DELIVERED


}

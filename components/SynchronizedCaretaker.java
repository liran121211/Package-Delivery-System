/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import java.util.ArrayDeque;

/**
 * Represent a Stack of Memento (objects) checkpoints.
 * Every time a a Memento object is added, those items can be accessed through FIFO method.
 * This class is Thread safe.
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class SynchronizedCaretaker {
    //Attributes
    private final ArrayDeque<Memento> mementoStack;
    private int itemsLeft;

    //Constructor
    public SynchronizedCaretaker() {
        mementoStack = new ArrayDeque<>();
    }

    //Methods

    /**
     * Pull the last Memento (object) in the Stack.
     *
     * @return (Memento) object.
     * @since 1.2
     */
    protected synchronized Memento pop() {
        return this.mementoStack.pop();
    }

    /**
     * Push a new Memento (object) to the Stack.
     *
     * @param (checkPoint) Memento object
     * @since 1.2
     */
    protected synchronized void push(Memento checkPoint) {
        this.mementoStack.push(checkPoint);
    }

    /**
     * Retrieve the last Memento (object) in the Stack.
     *
     * @return (checkPoint) Memento object
     * @since 1.2
     */
    protected synchronized Memento peek() {
        return this.mementoStack.peek();
    }

    /**
     * Get the size of the stack.
     *
     * @return Stack size.
     * @since 1.2
     */
    protected synchronized int size() {
        return this.mementoStack.size();
    }

    /**
     * Check if the stack is empty.
     *
     * @return true/false if stack is empty.
     * @since 1.2
     */
    protected synchronized boolean isEmpty() {
        return this.mementoStack.isEmpty();
    }

    /**
     * Get the size of stored Memento (objects) in the stack.
     *
     * @return Amount of Memento objects
     * @since 1.2
     */
    protected int getItemsLeft() {
        return itemsLeft;
    }

    /**
     * Set the size of stored Memento (objects) in the stack.
     *
     * @param (itemsLeft) amount of Memento objects
     * @since 1.2
     */
    protected synchronized void setItemsLeft(int itemsLeft) {
        this.itemsLeft = itemsLeft;
    }
}


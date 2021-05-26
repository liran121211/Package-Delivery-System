/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package program;
import components.MainOffice;

public class Game {
    public static void main(String[] args) {

        //To avoid errors, Branches amount always greater by exactly 1 from Trucks amount.
        MainOffice game = new MainOffice(5, 4);
        game.play(60);
    }
}



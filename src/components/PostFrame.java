/*
 * Copyright (c) 2021, Liran Smadja, ID: 311370092, Tamar Aminov, ID: 209153501, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


import static components.MainOffice.getClock;

/**
 * Controls all of the processes in the program.
 * Responsible to operate the MainOffice class and manage the GUI components.
 * Initialize the graphical components and run the MainOffice object that initialize the the branches/trucks objects
 *
 * @author Liran Smadja, Tamar Aminov
 */

public class PostFrame implements ActionListener {
    //Statics
    final static ImageIcon img = new ImageIcon("src/images/icon.png");
    final static String[] buttonNames = {"Create System", "Start", "Stop", "Resume", "All Packages Info", "Branch Info"};

    //Attributes
    private MainOffice game;
    private JFrame mainFrame, settingsFrame, branchInfoFrame, trackingFrame;
    private JPanel systemPane;
    private JButton[] buttonsList;
    private JButton okButton, cancelButton;
    private JButton branchOkButton, branchCancelButton;
    private JSlider branch, truck, packages;
    private JComboBox branchesCombo;
    Timer timerGUI;

    /**
     * <p>Start the program</p>
     *
     * @since 1.1
     */
    private void runSystem() {
        ArrayList<Object> components = ((PostPanel) systemPane).getJcomponents();
        truck.setValue(branch.getValue());
        game = new MainOffice(branch.getValue(), truck.getValue());
        BranchGUI.resetID();

        //HUB Initialization
        game.getHub().getBranches().get(0).setGui(new BranchGUI(true));
        components.add(game.getHub().getBranches().get(0).getGui());

        //Local Branch Initialization
        for (int i = 0; i < branch.getValue(); i++) {
            BranchGUI localBranch = new BranchGUI(10, 85 + (500 / branch.getValue()) * i);
            localBranch.setPointTo(new LineGUI(new Color(0, 102, 0), localBranch.getX() + 40, localBranch.getY() + 15, 1138, 210 + (i * 20)));
            game.getHub().getBranches().get(i + 1).setGui(localBranch);//Add GUI to Branch
            components.add(localBranch);

            //StandardTruck Initialization
            TruckGUI tempStandardTruck = new TruckGUI("StandardTruck", localBranch.getPointTo().getEndX() + 50, localBranch.getPointTo().getEndY() + (i * 3) - 10);
            ((StandardTruck) game.getHub().getBranches().get(0).getListTrucks().get(i)).setGui(tempStandardTruck);//Add GUI to StandardTruck
            tempStandardTruck.setStartBranch(game.getHub().getBranches().get(0));
            tempStandardTruck.setEndBranch(game.getHub().getBranches().get(i + 1));
            components.add(tempStandardTruck);
            tempStandardTruck.visible(false);

            //Van Initialization
            for (int j = 0; j < truck.getValue(); j++) {
                TruckGUI tempVan = new TruckGUI("Van", localBranch.getX() + 45, localBranch.getY() + 5);
                ((Van) (game.getHub().getBranches().get(i + 1).getListTrucks().get(j))).setGui(tempVan);//Add GUI to Van
                ((Van) (game.getHub().getBranches().get(i + 1).getListTrucks().get(j))).setKeepGui(localBranch);//Add GUI to Van
                components.add(tempVan);
                tempVan.visible(false);
            }
        }
        //NonStandardTruck Initialization
        NonStandardTruck nst = (NonStandardTruck) game.getHub().getBranches().get(0).getListTrucks().get(game.getHub().getBranches().get(0).getListTrucks().size() - 1);
        TruckGUI tempNonStandardTruck = new TruckGUI("NonStandardTruck", 1150, 185);
        nst.setGui(tempNonStandardTruck); //Add GUI to NonStandardTruck
        components.add(tempNonStandardTruck);
        tempNonStandardTruck.visible(false);

        //Repaint basic components
        mainFrame.revalidate();
        mainFrame.repaint();
        settingsFrame.setVisible(false);
        timerGUI = new Timer(500, this);
        timerGUI.setRepeats(true);
        timerGUI.start();
    }

    /**
     * <p>Initialize the main frame and buttons</p>
     *
     * @since 1.1
     */
    public synchronized void init() {
        //Initializers
        mainFrame = new JFrame();
        JPanel buttonsPane = new JPanel();
        systemPane = new PostPanel();
        mainFrame.add(systemPane, BorderLayout.NORTH);
        mainFrame.add(buttonsPane, BorderLayout.SOUTH);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Terminate all threads and show tracking history
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (game != null) {
                    suspendThreads();
                    System.out.println("========================== STOP ==========================");
                    game.printReport();
                }
            }
        });

        //Frame Settings
        mainFrame.setTitle("Post Tracking System");
        mainFrame.setIconImage(img.getImage());
        mainFrame.setSize(1200, 700);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);

        //System Pane Layout Settings
        systemPane.setPreferredSize(new Dimension(1200, 635));

        //Buttons Layouts Settings
        buttonsPane.setLayout(new GridLayout(1, 6));
        buttonsList = new JButton[6];
        for (int i = 0; i < buttonsList.length; i++) {
            buttonsList[i] = new JButton(buttonNames[i]);
            buttonsPane.add(buttonsList[i]);
            buttonsList[i].addActionListener(this);
        }

    }

    /**
     * <p>Initialize the settings frame and buttons</p>
     *
     * @since 1.1
     */
    private void settingWindow() {
        //Initializers
        settingsFrame = new JFrame("Settings");
        JPanel sliderPane = new JPanel();
        JPanel settingsButtonPane = new JPanel();
        settingsFrame.add(sliderPane, BorderLayout.NORTH);
        settingsFrame.add(settingsButtonPane, BorderLayout.SOUTH);

        //Frame Settings
        settingsFrame.setIconImage(img.getImage());
        settingsFrame.setSize(600, 400);
        settingsFrame.setResizable(false);
        settingsFrame.setVisible(true);


        //Sliders Layouts Settings
        sliderPane.setLayout(new GridLayout(6, 1));
        sliderPane.setPreferredSize(new Dimension(400, 300));
        branch = new JSlider(1, 10);
        truck = new JSlider(1, 10);
        packages = new JSlider(2, 20);
        JLabel branchLabel = new JLabel("Number of Branches");
        JLabel truckLabel = new JLabel("Number of Trucks Per Brunch");
        JLabel packagesLabel = new JLabel("Number of Packages");
        branchLabel.setHorizontalAlignment(JLabel.CENTER);
        truckLabel.setHorizontalAlignment(JLabel.CENTER);
        packagesLabel.setHorizontalAlignment(JLabel.CENTER);
        sliderPane.add(branchLabel);
        sliderPane.add(branch);
        sliderPane.add(truckLabel);
        sliderPane.add(truck);
        sliderPane.add(packagesLabel);
        sliderPane.add(packages);

        branch.setPaintTrack(true);
        branch.setPaintTicks(true);
        branch.setPaintLabels(true);
        branch.setMajorTickSpacing(1);

        truck.setPaintTrack(true);
        truck.setPaintTicks(true);
        truck.setPaintLabels(true);
        truck.setMajorTickSpacing(1);

        packages.setPaintTrack(true);
        packages.setPaintTicks(true);
        packages.setPaintLabels(true);
        packages.setMajorTickSpacing(2);
        packages.setMinorTickSpacing(1);


        //Buttons Layouts Settings
        settingsButtonPane.setLayout(new GridLayout(1, 2, 5, 1));
        settingsButtonPane.setPreferredSize(new Dimension(50, 30));
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        settingsButtonPane.add(okButton);
        settingsButtonPane.add(cancelButton);

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

    }

    /**
     * <p>Initialize the branch selection frame and buttons</p>
     *
     * @since 1.1
     */
    private void branchInfoWindow() {
        //Initializers
        branchInfoFrame = new JFrame("Choose Branch");
        JPanel branchInfoPane = new JPanel();
        JPanel branchInfoButtons = new JPanel();
        branchInfoFrame.add(branchInfoPane, BorderLayout.NORTH);
        branchInfoFrame.add(branchInfoButtons, BorderLayout.SOUTH);

        //Buttons Settings
        branchOkButton = new JButton("OK");
        branchCancelButton = new JButton("Cancel");
        branchInfoButtons.add(branchOkButton);
        branchInfoButtons.add(branchCancelButton);

        //ComboBox Settings
        branchesCombo = new JComboBox();
        branchesCombo.addItem("Sorting Center");
        if (branch != null) {
            for (int i = 1; i <= branch.getValue(); i++)
                branchesCombo.addItem("Branch " + i);
        }
        branchInfoPane.add(branchesCombo);

        //Frame Settings
        branchInfoFrame.setIconImage(img.getImage());
        branchInfoFrame.setSize(300, 120);
        branchInfoFrame.setResizable(false);
        branchInfoFrame.setVisible(true);

        branchOkButton.addActionListener(this);
        branchCancelButton.addActionListener(this);
    }

    /**
     * <p>Initialize the tracking packages frame and buttons</p>
     *
     * @since 1.1
     */
    private void trackingWindow(boolean mode) {
        //Initializers
        trackingFrame = new JFrame("Tracking Panel");
        trackingFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        int countSpecificPackages = 0, j = 0;
        ArrayList<Package> packages = game.getPackages();
        String[] attributes = {"Package ID", "Sender", "Destination", "Priority", "Status"};
        String[][] tableData;
        //Count packages for destination point.
        if (mode) {
            for (Package item : packages)
                countSpecificPackages = (item.getDestinationAddress().getZip() == branchesCombo.getSelectedIndex()) ? countSpecificPackages + 1 : countSpecificPackages;
            tableData = new String[countSpecificPackages][];
            for (Package aPackage : packages) {
                if (aPackage.getDestinationAddress().getZip() == branchesCombo.getSelectedIndex()) {
                    tableData[j] = new String[5];
                    tableData[j][0] = Integer.toString(aPackage.getPackageID());
                    tableData[j][1] = aPackage.getSenderAddress().getZip() + "-" + aPackage.getSenderAddress().getStreet();
                    tableData[j][2] = aPackage.getDestinationAddress().getZip() + "-" + aPackage.getDestinationAddress().getStreet();
                    tableData[j][3] = aPackage.getPriority().name();
                    tableData[j][4] = aPackage.getStatus().name();
                    j++;
                }
            }
        } else {
            tableData = new String[packages.size()][];
            for (int i = 0; i < packages.size(); i++) {
                tableData[i] = new String[5];
                tableData[i][0] = Integer.toString(packages.get(i).getPackageID());
                tableData[i][1] = packages.get(i).getSenderAddress().getZip() + "-" + packages.get(i).getSenderAddress().getStreet();
                tableData[i][2] = packages.get(i).getDestinationAddress().getZip() + "-" + packages.get(i).getDestinationAddress().getStreet();
                tableData[i][3] = packages.get(i).getPriority().name();
                tableData[i][4] = packages.get(i).getStatus().name();
            }
        }
        JTable infoTable = new JTable(tableData, attributes);
        infoTable.setBounds(30, 40, 200, 300);
        JScrollPane scrollPane = new JScrollPane(infoTable);
        trackingFrame.add(scrollPane);


        //Frame Settings
        trackingFrame.setIconImage(img.getImage());
        trackingFrame.setSize(800, 300);
        trackingFrame.setResizable(false);
        trackingFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Refresh Gui every (t) time
        if (e.getSource() == timerGUI) {
            systemPane.repaint();

        }

        //Main Window
        if (e.getSource().equals(buttonsList[0]))
            if (settingsFrame == null)
                settingWindow();
            else settingsFrame.setVisible(!settingsFrame.isVisible());

        if (e.getSource().equals(buttonsList[1])) { //Start Game
            if (game != null) {
                buttonsList[0].setEnabled(false);
                buttonsList[1].setEnabled(false);
                mainOfficeWorker();
                packagesWorker();
            }
        }

        if (e.getSource().equals(buttonsList[2])) { //Suspend all threads
            if (game != null)
                suspendThreads();
        }

        if (e.getSource().equals(buttonsList[3])) { //Resume all threads
            if (game != null)
                resumeThreads();
        }

        if (e.getSource().equals(buttonsList[4])) { //Tracking Window
            trackingFrame = null;
            if (game != null) {
                trackingWindow(false);
                if (trackingFrame.isVisible())
                    trackingFrame.setVisible(false);
                if (!trackingFrame.isVisible())
                    trackingFrame.setVisible(true);
            }


        }

        if (e.getSource().equals(buttonsList[5])) //Branch Window
            if (game != null && branchInfoFrame == null)
                branchInfoWindow();
            else if (branchInfoFrame != null && branchInfoFrame.isVisible())
                branchInfoFrame.setVisible(false);
            else if (branchInfoFrame != null && !branchInfoFrame.isVisible())
                branchInfoFrame.setVisible(true);

        //Settings Window
        if (e.getSource().equals(cancelButton))
            settingsFrame.setVisible(false);

        if (e.getSource().equals(okButton)) {
            mainFrame.remove(systemPane);
            systemPane = new PostPanel();
            mainFrame.add(systemPane);
            runSystem();
        }

        //Branch Info Window
        if (e.getSource().equals(branchCancelButton))
            branchInfoFrame.setVisible(false);
        if (e.getSource().equals(branchOkButton)) {
            if (trackingFrame != null)
                trackingFrame.setVisible(false);
            trackingWindow(true);
            branchInfoFrame.setVisible(false);
        }

    }

    /**
     * <p>Initialize the MainOffice thread</p>
     *
     * @since 1.1
     */
    private void mainOfficeWorker() {
        SwingWorker mainOfficeWorker = new SwingWorker<Object, Void>() {
            @Override
            public Object doInBackground() {
                game.play();
                return null;
            }
        };
        mainOfficeWorker.execute();
    }

    /**
     * <p>Resume all threads</p>
     *
     * @since 1.1
     */
    private void resumeThreads() {
        game.setResume();
        game.getHub().setResume(); //HUB iteration
        for (int i = 0; i < game.getHub().getBranches().size(); i++) {
            game.getHub().getBranches().get(i).setResume();//Branches iteration

            for (int j = 0; j < game.getHub().getBranches().get(i).getListTrucks().size(); j++) {

                if (game.getHub().getBranches().get(i).getListTrucks().get(j) instanceof StandardTruck)
                    ((StandardTruck) game.getHub().getBranches().get(i).getListTrucks().get(j)).setResume(); //StandardTruck iteration

                if (game.getHub().getBranches().get(i).getListTrucks().get(j) instanceof Van)
                    ((Van) game.getHub().getBranches().get(i).getListTrucks().get(j)).setResume(); //StandardTruck iteration

                if (game.getHub().getBranches().get(i).getListTrucks().get(j) instanceof NonStandardTruck)
                    ((NonStandardTruck) game.getHub().getBranches().get(i).getListTrucks().get(j)).setResume(); //StandardTruck iteration
            }
        }
    }

    /**
     * <p>Suspend all threads</p>
     *
     * @since 1.1
     */
    private void suspendThreads() {
        game.setSuspend();
        game.getHub().setSuspend(); //HUB iteration
        for (int i = 0; i < game.getHub().getBranches().size(); i++) {
            game.getHub().getBranches().get(i).setSuspend();//Branches iteration

            for (int j = 0; j < game.getHub().getBranches().get(i).getListTrucks().size(); j++) {

                if (game.getHub().getBranches().get(i).getListTrucks().get(j) instanceof StandardTruck)
                    ((StandardTruck) game.getHub().getBranches().get(i).getListTrucks().get(j)).setSuspend(); //StandardTruck iteration

                if (game.getHub().getBranches().get(i).getListTrucks().get(j) instanceof Van)
                    ((Van) game.getHub().getBranches().get(i).getListTrucks().get(j)).setSuspend(); //StandardTruck iteration

                if (game.getHub().getBranches().get(i).getListTrucks().get(j) instanceof NonStandardTruck)
                    ((NonStandardTruck) game.getHub().getBranches().get(i).getListTrucks().get(j)).setSuspend(); //StandardTruck iteration
            }
        }
    }

    /**
     * <p>Create new package every 5 seconds</p>
     *
     * @since 1.1
     */
    private void packagesWorker() {
        SwingWorker packagesWorker = new SwingWorker<Object, Void>() {
            boolean alreadyCreated = false;

            @Override
            public Object doInBackground() {
                int i = 0;
                while (i != packages.getValue()) {
                    if (getClock() % 5 == 0 && !alreadyCreated) {
                        game.addPackage();
                        BranchGUI sBranch = PostPanel.getBranch(game.getPackages().get(i).getSenderAddress().getZip() + 1, ((PostPanel) systemPane).getJcomponents());
                        BranchGUI dBranch = PostPanel.getBranch(game.getPackages().get(i).getDestinationAddress().getZip() + 1, ((PostPanel) systemPane).getJcomponents());
                        PackageGUI upper = new PackageGUI(100 + 900 / packages.getValue() * i, 10);
                        PackageGUI lower = new PackageGUI(100 + 900 / packages.getValue() * i, 580);
                        ((PostPanel) systemPane).getJcomponents().add(upper);
                        ((PostPanel) systemPane).getJcomponents().add(lower);
                        upper.changeColor("DARK_RED");

                        if (game.getPackages().get(i) instanceof NonStandardPackage) {
                            upper.setPointTo(new LineGUI(Color.red, upper.getX() + 25, upper.getY() + 25, 1160, 200));
                            lower.setPointTo(new LineGUI(Color.red, 115 + (900 / packages.getValue()) * i, 580, 115 + (900 / packages.getValue()) * i, 40));
                        } else {
                            upper.setPointTo(new LineGUI(Color.blue, 110 + (900 / packages.getValue()) * i, 40, dBranch.getX() + 25, dBranch.getY()));
                            lower.setPointTo(new LineGUI(Color.blue, 115 + (900 / packages.getValue()) * i, 580, sBranch.getX() + 10, sBranch.getY() + 30));

                        }
                        game.getPackages().get(i).setSenderPackage(upper);
                        game.getPackages().get(i).setDestinationPackage(lower);
                        alreadyCreated = true;
                        i++;

                    } else if (getClock() % 5 != 0)
                        alreadyCreated = false;

                    mainFrame.repaint();
                }
                return null;
            }
        };
        packagesWorker.execute();
    }

}
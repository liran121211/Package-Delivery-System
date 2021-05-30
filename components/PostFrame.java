/*
 * Copyright (c) 2021, Liran Smadja, Tamar Aminov, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

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
    final static String[] buttonNames = {"Create System", "Start", "Stop", "Resume", "All Packages Info", "Branch Info", "Clone Branch", "Restore (No Checkpoints)", "Report"};
    private static final Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
    //Attributes
    private MainOffice game;
    private JFrame mainFrame;
    private JFrame settingsFrame;
    private JFrame branchInfoFrame;
    private JFrame trackingFrame;
    private JFrame branchCloneFrame;
    private JPanel systemPane;
    private JButton[] buttonsList;
    private JButton okButton, cancelButton;
    private JButton branchOkButton;
    private JButton branchCancelButton;
    private JButton branchCloneOkButton;
    private JSlider branch;
    private JSlider truck;
    private JComboBox<String> branchesCombo, branchesCloneCombo;
    private SynchronizedCaretaker checkPoints;
    private Timer timerGUI;

    /**
     * <p>Start the program</p>
     *
     * @since 1.1
     */
    private void runSystem() {
        CopyOnWriteArrayList<Object> components = ((PostPanel) systemPane).getJcomponents();
        truck.setValue(branch.getValue());

        //Singleton MainOffice
        MainOffice.getInstance();
        MainOffice.getInstance().init(branch.getValue(), truck.getValue());
        game = MainOffice.getInstance();
        BranchGUI.resetID();

        //HUB Initialization
        MainOffice.getInstance().getHub().getBranches().get(0).setGui(new BranchGUI(true));
        components.add(MainOffice.getInstance().getHub().getBranches().get(0).getGui());

        //Local Branch Initialization
        for (int i = 0; i < branch.getValue(); i++) {
            BranchGUI localBranch = new BranchGUI(10, 85 + (500 / branch.getValue()) * i);
            localBranch.setPointTo(new LineGUI(new Color(0, 102, 0), localBranch.getX() + 40, localBranch.getY() + 15, 1730, 200 + (i * 20)));
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

        Customer.setPostFrame(this);

        //Repaint basic components
        mainFrame.revalidate();
        mainFrame.repaint();
        settingsFrame.setVisible(false);
        timerGUI = new Timer(50, this);
        timerGUI.setRepeats(true);
        timerGUI.start();
    }

    /**
     * Restore system and objects to the last saved checkpoint.
     * @param checkPoint (Memento) object
     * @throws InterruptedException - throw exception if Thread was interrupted.
     * @since 1.2
     */
    private void restoreSystem(Memento checkPoint) throws InterruptedException {
        game.terminateThreads();
        Thread.sleep(100);
        game.setHub(checkPoint.getHub());
        game.setClock(checkPoint.getClockHUB());
        game.setSuspended(checkPoint.isSuspendHUB());
        game.setPackages(checkPoint.getPackagesHUB());
        game.setCustomers(checkPoint.getCustomersHUB());
        game.setCustomerThreadPool(checkPoint.getCustomerThreadPoolHUB());

        branch.setValue(checkPoint.getBranchSize());
        truck.setValue(checkPoint.getTruckSize());
        mainFrame.remove(systemPane);
        systemPane= new PostPanel();

        ((PostPanel) systemPane).setJcomponents(new CopyOnWriteArrayList<>());

        //HUB Initialization
        ((PostPanel) systemPane).getJcomponents().add(checkPoint.getHub().getBranches().get(0).getGui());

        //Local Branch Initialization
        for (int i = 0; i < branch.getValue(); i++) {
            ((PostPanel) systemPane).getJcomponents().add(checkPoint.getHub().getBranches().get(i+1).getGui());

            //StandardTruck Initialization
            if (checkPoint.getHub().getBranches().get(0).getListTrucks().get(i) instanceof StandardTruck) {
                ((PostPanel) systemPane).getJcomponents().add(((StandardTruck) checkPoint.getHub().getBranches().get(0).getListTrucks().get(i)).getGui());
                ((StandardTruck) checkPoint.getHub().getBranches().get(0).getListTrucks().get(i)).getGui().visible(true);
            }
            //Van Initialization
            for (int j = 0; j < truck.getValue(); j++) {
                if ((checkPoint.getHub().getBranches().get(i + 1).getListTrucks().get(j)) instanceof Van) {
                    ((Van) checkPoint.getHub().getBranches().get(i + 1).getListTrucks().get(j)).setIsTerminated(true);
                    ((PostPanel) systemPane).getJcomponents().add(((Van) checkPoint.getHub().getBranches().get(i + 1).getListTrucks().get(j)).getGui());
                    ((Van) checkPoint.getHub().getBranches().get(i + 1).getListTrucks().get(j)).getGui().visible(false);
                }

            }
        }
        ((PostPanel) systemPane).getJcomponents().add(((NonStandardTruck) checkPoint.getHub().getBranches().get(0).getListTrucks().get(checkPoint.getHub().getBranches().get(0).getListTrucks().size() - 1)).getGui());
        ((NonStandardTruck) checkPoint.getHub().getBranches().get(0).getListTrucks().get(checkPoint.getHub().getBranches().get(0).getListTrucks().size() - 1)).getGui().visible(false);

        for (Customer c: game.getCustomers()){
            for (Package p: c.getCustomerPackages()){
                ((PostPanel) systemPane).getJcomponents().add(p.getSenderPackage());
                ((PostPanel) systemPane).getJcomponents().add(p.getDestinationPackage());
            }
        }
        Customer.setPostFrame(this);

        //Repaint basic components
        mainFrame.add(systemPane);
        game.startThreads();
        buttonsList[6].setEnabled(true);
        mainFrame.revalidate();
        mainFrame.repaint();


    }

    /**
     * Packages report window
     * Choose which packages report to retrieve.
     * @since 1.2
     */
    private void ReportWindow() {
        //Report Frame
        JFrame reportFrame = new JFrame();
        reportFrame.setTitle("Tracking Report");
        reportFrame.setIconImage(img.getImage());
        reportFrame.setSize(300, 700);
        reportFrame.setResizable(false);
        reportFrame.setVisible(true);

        //Report Panel
        JPanel reportPane = new JPanel(new BorderLayout());
        JTextArea reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);
        JScrollPane reportScroll = new JScrollPane(reportTextArea);
        reportFrame.add(reportPane);
        reportFrame.add(reportScroll);

        Scanner trackingFileReader = null;
        StringBuilder allText = new StringBuilder();
        try {
            trackingFileReader = new Scanner(MainOffice.getTrackingFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            assert trackingFileReader != null;
            if (!trackingFileReader.hasNextLine()) break;
            allText.append(trackingFileReader.nextLine()).append("\n");
        }
        trackingFileReader.close();
        reportTextArea.setText(String.valueOf(allText));
    }

    /**
     * <p>Initialize the main frame and buttons</p>
     *
     * @since 1.1
     */
    public synchronized void init() {
        //Initializers
        checkPoints = new SynchronizedCaretaker();
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
        mainFrame.setSize(2200, 700);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);

        //System Pane Layout Settings
        systemPane.setPreferredSize(new Dimension(2200, 635));

        //Buttons Layouts Settings
        buttonsPane.setLayout(new GridLayout(1, 9));
        buttonsList = new JButton[9];
        for (int i = 0; i < buttonsList.length; i++) {
            buttonsList[i] = new JButton(buttonNames[i]);
            buttonsPane.add(buttonsList[i]);
            buttonsList[i].addActionListener(this);
        }
        buttonsList[1].setEnabled(false);
        buttonsList[2].setEnabled(false);
        buttonsList[3].setEnabled(false);
        buttonsList[4].setEnabled(false);
        buttonsList[5].setEnabled(false);
        buttonsList[6].setEnabled(false);
        buttonsList[7].setEnabled(false);
        buttonsList[8].setEnabled(false);

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
        JSlider packagesSlider = new JSlider(2, 20);
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
        sliderPane.add(packagesSlider);

        branch.setPaintTrack(true);
        branch.setPaintTicks(true);
        branch.setPaintLabels(true);
        branch.setMajorTickSpacing(1);

        truck.setPaintTrack(true);
        truck.setPaintTicks(true);
        truck.setPaintLabels(true);
        truck.setMajorTickSpacing(1);

        packagesSlider.setPaintTrack(true);
        packagesSlider.setPaintTicks(true);
        packagesSlider.setPaintLabels(true);
        packagesSlider.setMajorTickSpacing(2);
        packagesSlider.setMinorTickSpacing(1);


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
        branchesCombo = new JComboBox<>();
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
     * Branch cloning window
     * Choose which Branch to clone
     * @since 1.2
     */
    private void branchCloneWindow() {
        //Initializers
        branchCloneFrame = new JFrame("Clone Branch");
        JPanel branchCloneInfoPane = new JPanel();
        JPanel branchCloneInfoButtons = new JPanel();
        branchCloneFrame.add(branchCloneInfoPane, BorderLayout.NORTH);
        branchCloneFrame.add(branchCloneInfoButtons, BorderLayout.SOUTH);

        //Buttons Settings
        branchCloneOkButton = new JButton("OK");
        JButton branchCloneCancelButton = new JButton("Cancel");
        branchCloneInfoButtons.add(branchCloneOkButton);
        branchCloneInfoButtons.add(branchCloneCancelButton);

        //ComboBox Settings
        branchesCloneCombo = new JComboBox<>();
        if (branch != null) {
            for (int i = 1; i <= branch.getValue(); i++)
                branchesCloneCombo.addItem("Branch " + i);
        }
        branchCloneInfoPane.add(branchesCloneCombo);

        //Frame Settings
        branchCloneFrame.setIconImage(img.getImage());
        branchCloneFrame.setSize(300, 120);
        branchCloneFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        branchCloneFrame.setResizable(false);
        branchCloneFrame.setVisible(true);

        branchCloneOkButton.addActionListener(this);
        branchCloneCancelButton.addActionListener(this);
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
        CopyOnWriteArrayList<Package> packages = game.getPackages();
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
        //Refresh Gui every (t) seconds
        if (e.getSource() == timerGUI)
            systemPane.repaint();


        //Main Window
        if (e.getSource().equals(buttonsList[0]))
            if (settingsFrame == null)
                settingWindow();
            else settingsFrame.setVisible(!settingsFrame.isVisible());

        if (e.getSource().equals(buttonsList[1])) { //Start Game process
            if (MainOffice.getInstance() != null) {
                buttonsList[0].setEnabled(false);
                buttonsList[1].setEnabled(false);
                buttonsList[2].setEnabled(true);
                buttonsList[3].setEnabled(false);
                buttonsList[4].setEnabled(true);
                buttonsList[5].setEnabled(true);
                buttonsList[6].setEnabled(true);
                buttonsList[7].setEnabled(false);
                buttonsList[8].setEnabled(true);
                mainOfficeWorker();
            }
        }

        if (e.getSource().equals(buttonsList[2])) { //Suspend all threads
            suspendThreads();
            buttonsList[2].setEnabled(false);
            buttonsList[6].setEnabled(false);
            buttonsList[3].setEnabled(true);
        }

        if (e.getSource().equals(buttonsList[3])) { //Resume all threads
            resumeThreads();
            buttonsList[2].setEnabled(true);
            buttonsList[6].setEnabled(true);
            buttonsList[3].setEnabled(false);
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


        if (e.getSource().equals(buttonsList[6])) { //Choose Branch to Clone window
            System.out.println();
            branchCloneWindow();
        }

        if (e.getSource().equals(buttonsList[7])) { //Restore last checkpoint
            if (!checkPoints.isEmpty()) {
                try {
                    restoreSystem(checkPoints.pop());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                checkPoints.setItemsLeft(checkPoints.getItemsLeft() - 1);
                buttonsList[7].setText("Restore (" + checkPoints.getItemsLeft() + ")");
                if (checkPoints.isEmpty()) {
                    buttonsList[7].setEnabled(false);
                    buttonsList[7].setText("Restore (No Checkpoints)");
                }

            }
        }

        if (e.getSource().equals(buttonsList[8])) { //Report Window
            ReportWindow();
        }

        //Settings Window
        if (e.getSource().equals(cancelButton))
            settingsFrame.setVisible(false);

        if (e.getSource().equals(okButton)) { //init GUI
            mainFrame.remove(systemPane);
            systemPane = new PostPanel();
            mainFrame.add(systemPane);
            runSystem();
            buttonsList[1].setEnabled(true);
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
        //Clone Branch Button
        if (e.getSource().equals(branchCloneOkButton)) {
            cloneBranch(branchesCloneCombo.getSelectedIndex() + 1);
            branchesCloneCombo.removeItem(branchesCloneCombo.getSelectedItem());
            branchCloneFrame.dispose();
        }


    }

    /**
     * <p>Initialize the MainOffice thread</p>
     *
     * @since 1.1
     */
    private void mainOfficeWorker() {
        SwingWorker<Object, Void> mainOfficeWorker = new SwingWorker<>() {
            @Override
            public Object doInBackground() {
                game.play();
                return null;
            }
        };
        mainOfficeWorker.execute();
    }

    /**
     * Clone entire Branch object with given Branch ID number.
     * @param (branchIndex) Branch ID numbber.
     * @since 1.2
     */
    private synchronized void cloneBranch(int branchIndex) {
        if (branch.getValue() < 10) {
            checkPoints.push(new Memento(game, truck.getValue(), branch.getValue()));
            checkPoints.setItemsLeft(checkPoints.getItemsLeft() + 1);

            CopyOnWriteArrayList<Object> components = ((PostPanel) systemPane).getJcomponents();
            int oldBranchSize = branch.getValue();
            branch.setValue(branch.getValue() + 1);
            BranchGUI localBranch = null;


            Branch clonedBranch = null;
            int lastTruckID = ((Van) game.getHub().getBranches().get(game.getHub().getBranches().size() - 1).getListTrucks().get(game.getHub().getBranches().get(game.getHub().getBranches().size() - 1).getListTrucks().size() - 1)).getTruck().getTruckID();
            try {
                clonedBranch = game.getHub().getBranches().get(branchIndex).clone();
            } catch (CloneNotSupportedException ignored) {
            }

            game.getHub().getBranches().add(clonedBranch);
            BranchGUI.resetID();
            StandardTruck st = new StandardTruck();
            NonStandardTruck nst = (NonStandardTruck) game.getHub().getBranches().get(0).getListTrucks().get(game.getHub().getBranches().get(0).getListTrucks().size() - 1);
            game.getHub().getBranches().get(0).getListTrucks().set(game.getHub().getBranches().get(0).getListTrucks().size() - 1, st);
            st.setDestination(clonedBranch);
            st.setKeepTrack(game.getHub().getBranches().get(0));
            game.getHub().getBranches().get(0).getListTrucks().add(nst);
            game.getHub().getBranches().get(game.getHub().getBranches().size() - 1).setBranchName("Branch " + (branch.getValue())); //Rename Branch
            game.getHub().getBranches().get(game.getHub().getBranches().size() - 1).setBrachId(branch.getValue()); //Rename Branch
            assert clonedBranch != null;
            for (Object t : clonedBranch.getListTrucks())
                ((Van) t).getTruck().setTruckID(++lastTruckID);
            clonedBranch.getListTrucks().add(new Van());
            ((Van) clonedBranch.getListTrucks().get(clonedBranch.getListTrucks().size() - 1)).getTruck().setTruckID(++lastTruckID);


            //Branch Initialization GUI
            for (int i = oldBranchSize + 1; i < branch.getValue() + 1; i++) {
                localBranch = new BranchGUI(10, 85 + (500 / branch.getValue()) * i);
                localBranch.setPointTo(new LineGUI(new Color(0, 102, 0), localBranch.getX() + 40, localBranch.getY() + 15, 1730, 200 + (i * 20)));
                game.getHub().getBranches().get(i).setGui(localBranch);//Add GUI to Branch
                components.add(localBranch);

                //Van Initialization GUI
                for (int j = 0; j < truck.getValue(); j++) {
                    TruckGUI tempVan = new TruckGUI("Van", localBranch.getX() + 45, localBranch.getY() + 5);
                    ((Van) (game.getHub().getBranches().get(i).getListTrucks().get(j))).setGui(tempVan);//Add GUI to Van
                    ((Van) (game.getHub().getBranches().get(i).getListTrucks().get(j))).setKeepGui(localBranch);//Add GUI to Van
                    components.add(tempVan);
                    tempVan.visible(false);
                }
            }
            //Last Van Added
            assert localBranch != null;
            TruckGUI tempVan = new TruckGUI("Van", localBranch.getX() + 45, localBranch.getY() + 5);
            ((Van) (game.getHub().getBranches().get(game.getHub().getBranches().size() - 1).getListTrucks().get(game.getHub().getBranches().get(game.getHub().getBranches().size() - 1).getListTrucks().size() - 1))).setGui(tempVan);//Add GUI to Van
            ((Van) (game.getHub().getBranches().get(game.getHub().getBranches().size() - 1).getListTrucks().get(game.getHub().getBranches().get(game.getHub().getBranches().size() - 1).getListTrucks().size() - 1))).setKeepGui(localBranch);//Add GUI to Van
            components.add(tempVan);
            tempVan.visible(false);

            //Refresh Branches Location
            for (int i = 1; i < branch.getValue(); i++) {
                BranchGUI aBranch = game.getHub().getBranches().get(i).getGui();
                aBranch.setLocation(10, (85) + (500 / branch.getValue()) * (i - 1));
                (aBranch.getPointTo()).setStartX(aBranch.getX() + 40);
                (aBranch.getPointTo()).setStartY(aBranch.getY() + 15);

            }

            //Refresh Packages Delivery Lines
            for (int i = 0; i < game.getCustomers().size(); i++) {
                for (int j = 0; j < game.getCustomers().get(i).getCustomerPackages().size(); j++) {
                    if (!(game.getCustomers().get(i).getCustomerPackages().get(j) instanceof NonStandardPackage)) {
                        Package tempPackage = game.getCustomers().get(i).getCustomerPackages().get(j);
                        BranchGUI sBranch = this.getBranchGui(tempPackage.getSenderAddress().getZip());
                        BranchGUI dBranch = this.getBranchGui(tempPackage.getDestinationAddress().getZip());
                        tempPackage.getSenderPackage().getPointTo().setEndX(dBranch.getX() + 25);
                        tempPackage.getSenderPackage().getPointTo().setEndY(dBranch.getY());
                        tempPackage.getDestinationPackage().getPointTo().setEndX(sBranch.getX() + 10);
                        tempPackage.getDestinationPackage().getPointTo().setEndY(sBranch.getY() + 30);
                    }
                }
            }


            //StandardTruck Initialization
            TruckGUI tempStandardTruck = new TruckGUI("StandardTruck", clonedBranch.getGui().getPointTo().getEndX() + 50, clonedBranch.getGui().getPointTo().getEndY() + ((branch.getValue() + 1) * 3) - 10);
            st.setGui(tempStandardTruck);//Add GUI to StandardTruck
            tempStandardTruck.setStartBranch(game.getHub().getBranches().get(0));
            tempStandardTruck.setEndBranch(game.getHub().getBranches().get(branch.getValue()));
            components.add(tempStandardTruck);
            tempStandardTruck.visible(false);

            new Thread(st).start(); //Run Thread
            new Thread(clonedBranch).start(); //Run Thread
            clonedBranch.addPropertyChangeListener(game); //Add Listener (MainOffice listen to Branch)
            st.addPropertyChangeListener(game); //Add Listener (MainOffice listen to StandardTruck)
            for (Object v : clonedBranch.getListTrucks()) {
                clonedBranch.addPropertyChangeListener((Van) v); //Add Listener (Van listen to Branch)
                ((Van) v).addPropertyChangeListener(game); //Add Listener (MainOffice listen to Van)
                new Thread(((Van) v)).start(); //Run Thread
            }
            mainFrame.revalidate();
            mainFrame.repaint();

            buttonsList[7].setText("Restore (" + checkPoints.getItemsLeft() + ")");
            buttonsList[7].setEnabled(true);
        } else
            buttonsList[6].setEnabled(false);
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
        for (int k = 0; k < 10; k++) {
            MainOffice.getInstance().getCustomers().get(k).setResume();
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
        for (int k = 0; k < 10; k++)
            MainOffice.getInstance().getCustomers().get(k).setSuspend();
    }

    /**
     * Get mainFrame instance
     * @return (mainFrame) object
     * @since 1.2
     */
    protected JFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Get systemPane instance
     * @return (systemPane) object
     * @since 1.2
     */
    protected JPanel getSystemPane() {
        return systemPane;
    }

    /**
     * Get BranchGUI object based on Branch ID given.
     * @param (branchID) branch ID number
     * @return (BranchGUI) object.
     * @since 1.2
     */
    protected BranchGUI getBranchGui(int branchID) {
        return game.getHub().getBranches().get(branchID).getGui();
    }
}
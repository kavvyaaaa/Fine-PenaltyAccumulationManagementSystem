package main;

import gui.LoginScreen;

import javax.swing.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            String mode = args[0].toLowerCase().trim();
            if (mode.equals("gui")) {
                launchGUI();
            } else if (mode.equals("terminal") || mode.equals("cli")) {
                launchTerminal();
            } else {
                System.out.println("[ERROR] Unknown mode: " + mode);
                System.out.println("Usage: java main.Main [gui|terminal]");
            }
            return;
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("==========================================");
            System.out.println(" PFAMS - Penalty/Fine Management System");
            System.out.println("==========================================");
            System.out.println(" Select mode:");
            System.out.println("   1. GUI Mode (Graphical Interface)");
            System.out.println("   2. Terminal Mode (CLI)");
            System.out.println("   3. Exit");
            System.out.println("==========================================");
            System.out.print(" Enter choice (1/2/3): ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                case "gui":
                    launchGUI();
                    System.out.println("\n [GUI closed]");
                    break;
                case "2":
                case "terminal":
                case "cli":
                    launchTerminal();
                    break;
                case "3":
                case "exit":
                    System.out.println("\n Goodbye!");
                    scanner.close();
                    System.exit(0);
                    return;
                default:
                    System.out.println(" Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private static void launchGUI() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
        final Object lock = new Object();
        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
            loginScreen.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    synchronized (lock) { lock.notifyAll(); }
                }
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    synchronized (lock) { lock.notifyAll(); }
                }
            });
        });
        synchronized (lock) {
            try { lock.wait(); } catch (InterruptedException e) {  }
        }
    }

    private static void launchTerminal() {
        TerminalApp app = new TerminalApp();
        app.run();
    }
}

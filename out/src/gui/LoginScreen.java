package gui;

import controller.AuthController;
import model.Account;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel statusLabel;

    private final AuthController authController;

    public LoginScreen() {
        this.authController = new AuthController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("PFAMS - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 245, 250));
        JLabel titleLabel = new JLabel("Penalty/Fine Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        emailField = new JTextField(20);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30));
        formPanel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 70, 160));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(200, 35));
        formPanel.add(loginButton, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        signupButton = new JButton("Sign Up (Create New Account)");
        signupButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        signupButton.setBackground(new Color(60, 150, 60));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.setPreferredSize(new Dimension(200, 32));
        formPanel.add(signupButton, gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 5, 5, 5);
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());
        signupButton.addActionListener(e -> showSignupDialog());
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both email and password.");
            return;
        }

        statusLabel.setText("Authenticating...");
        loginButton.setEnabled(false);

        SwingWorker<Account, Void> worker = new SwingWorker<Account, Void>() {
            @Override
            protected Account doInBackground() {
                return authController.handleLogin(email, password);
            }

            @Override
            protected void done() {
                try {
                    Account account = get();
                    if (account != null) {
                        if (authController.isAdmin(account)) {
                            new AdminDashboard(account).setVisible(true);
                        } else if (authController.isAuthority(account)) {
                            new AuthorityDashboard(account).setVisible(true);
                        } else {
                            new UserDashboard(account).setVisible(true);
                        }
                        dispose();
                    } else {
                        statusLabel.setText("Invalid email or password.");
                        loginButton.setEnabled(true);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Login error. Check database connection.");
                    loginButton.setEnabled(true);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }


    private void showSignupDialog() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailRegField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JPasswordField passRegField = new JPasswordField();

        panel.add(new JLabel("First Name *:")); panel.add(firstNameField);
        panel.add(new JLabel("Last Name:")); panel.add(lastNameField);
        panel.add(new JLabel("Email *:")); panel.add(emailRegField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Address:")); panel.add(addressField);
        panel.add(new JLabel("Password *:")); panel.add(passRegField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sign Up - Create New Account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailRegField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String password = new String(passRegField.getPassword());

            if (firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "First Name, Email, and Password are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authController.isEmailTaken(email)) {
                JOptionPane.showMessageDialog(this,
                        "Email '" + email + "' is already registered.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account account = authController.handleSignup(firstName, lastName, email, address, phone, password, 2);
            if (account != null) {
                JOptionPane.showMessageDialog(this,
                        "Account created successfully!\nYou can now login with your email and password.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                emailField.setText(email);
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Signup failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

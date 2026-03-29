package gui;

import controller.AuthController;
import controller.FineController;
import controller.PaymentController;
import model.Account;
import model.Fine;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class UserDashboard extends JFrame {

    private final Account account;
    private final AuthController authController;
    private final FineController fineController;
    private final PaymentController paymentController;

    private JLabel nameLabel, emailLabel, phoneLabel, addressLabel, totalFineLabel;
    private JTable finesTable;
    private DefaultTableModel tableModel;

    public UserDashboard(Account account) {
        this.account = account;
        this.authController = new AuthController();
        this.fineController = new FineController();
        this.paymentController = new PaymentController();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setTitle("PFAMS - User Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 250));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 70, 160));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel headerLabel = new JLabel("User Dashboard");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        JButton searchButton = new JButton("Search Fines");
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> showSearchDialog());

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose();
        });

        headerRight.add(searchButton);
        headerRight.add(logoutButton);
        headerPanel.add(headerRight, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 160)),
                " User Information "));
        userPanel.setBackground(Color.WHITE);
        userPanel.setPreferredSize(new Dimension(250, 0));

        nameLabel = createInfoLabel("Name: ");
        emailLabel = createInfoLabel("Email: ");
        phoneLabel = createInfoLabel("Phone: ");
        addressLabel = createInfoLabel("Address: ");

        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(nameLabel);
        userPanel.add(Box.createVerticalStrut(8));
        userPanel.add(emailLabel);
        userPanel.add(Box.createVerticalStrut(8));
        userPanel.add(phoneLabel);
        userPanel.add(Box.createVerticalStrut(8));
        userPanel.add(addressLabel);
        userPanel.add(Box.createVerticalStrut(20));
        totalFineLabel = new JLabel("Total Fine: ₹0.00");
        totalFineLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalFineLabel.setForeground(new Color(180, 0, 0));
        totalFineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalFineLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        userPanel.add(totalFineLabel);

        mainPanel.add(userPanel, BorderLayout.WEST);
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 160)),
                " Your Fines "));
        tablePanel.setBackground(Color.WHITE);

        String[] columns = {"Fine ID", "Violation", "Location", "Issue Date", "Due Date", "Base (₹)", "Penalty", "Total (₹)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        finesTable = new JTable(tableModel);
        finesTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        finesTable.setRowHeight(25);
        finesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        finesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(finesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);

        JComboBox<String> paymentModeCombo = new JComboBox<>(
                new String[]{"UPI", "Card", "NetBanking", "Cash"});
        paymentModeCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton payButton = new JButton("Pay Selected Fine");
        payButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        payButton.setBackground(new Color(0, 140, 60));
        payButton.setForeground(Color.WHITE);
        payButton.setFocusPainted(false);
        payButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        refreshButton.setFocusPainted(false);

        actionPanel.add(new JLabel("Payment Mode: "));
        actionPanel.add(paymentModeCombo);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(payButton);
        actionPanel.add(Box.createHorizontalStrut(5));
        actionPanel.add(refreshButton);

        tablePanel.add(actionPanel, BorderLayout.SOUTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel);
        payButton.addActionListener(e -> {
            int selectedRow = finesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a fine to pay.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String status = (String) tableModel.getValueAt(selectedRow, 8);
            if ("Paid".equals(status)) {
                JOptionPane.showMessageDialog(this,
                        "This fine is already paid.", "Already Paid", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int fineId = (int) tableModel.getValueAt(selectedRow, 0);
            double amount = (double) tableModel.getValueAt(selectedRow, 7);
            String mode = (String) paymentModeCombo.getSelectedItem();

            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format("Pay ₹%.2f for Fine ID %d via %s?", amount, fineId, mode),
                    "Confirm Payment", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                boolean success = paymentController.payFine(fineId, amount, mode);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Payment successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Payment failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        refreshButton.addActionListener(e -> loadData());
    }


    private void loadData() {
        User user = authController.getUserDetails(account.getUserId());
        if (user != null) {
            nameLabel.setText("  Name: " + user.getFullName());
            emailLabel.setText("  Email: " + user.getEmail());
            phoneLabel.setText("  Phone: " + (user.getPhoneNo() != null ? user.getPhoneNo() : "N/A"));
            addressLabel.setText("  Address: " + (user.getAddress() != null ? user.getAddress() : "N/A"));
        }
        double totalFine = fineController.getTotalFine(account.getAccountId());
        totalFineLabel.setText(String.format("  Total Fine: ₹%.2f", totalFine));
        tableModel.setRowCount(0);
        List<Fine> fines = fineController.getFinesForAccount(account.getAccountId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Fine fine : fines) {
            tableModel.addRow(new Object[]{
                fine.getFineId(),
                fine.getViolationName(),
                fine.getLocation(),
                fine.getIssueDate() != null ? sdf.format(fine.getIssueDate()) : "",
                fine.getDueDate() != null ? sdf.format(fine.getDueDate()) : "",
                fine.getFineAmount(),
                fine.getPenaltyAmount(),
                fine.getTotalAmount(),
                fine.getStatus()
            });
        }
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel("  " + text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        return label;
    }


    private void showSearchDialog() {
        String keyword = JOptionPane.showInputDialog(this,
                "Search your fines (by violation, location):",
                "Search Fines", JOptionPane.PLAIN_MESSAGE);
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Object[]> allResults = authController.searchFines(account.getAccountId(), keyword.trim());
            List<Fine> myFines = fineController.getFinesForAccount(account.getAccountId());
            java.util.Set<Integer> myFineIds = new java.util.HashSet<>();
            for (Fine f : myFines) myFineIds.add(f.getFineId());
            allResults.removeIf(r -> !myFineIds.contains((int) r[0]));

            if (allResults.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No results found for: " + keyword,
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-5s | %-12s | %-12s | %-6s | %-7s | %-6s | %s%n",
                    "ID", "Violation", "Location", "Base", "Penalty", "Total", "Status"));
            sb.append("-".repeat(75)).append("\n");
            for (Object[] r : allResults) {
                sb.append(String.format("%-5d | %-12s | %-12s | %-6.0f | %-7.0f | %-6.0f | %s%n",
                        (int) r[0], r[2], r[3], (double) r[4], (double) r[5], (double) r[6], r[7]));
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 250));
            JOptionPane.showMessageDialog(this, scrollPane,
                    "Search Results for: " + keyword, JOptionPane.PLAIN_MESSAGE);
        }
    }
}

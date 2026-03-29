package gui;

import controller.AuthController;
import controller.FineController;
import model.Account;
import model.Fine;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AuthorityDashboard extends JFrame {

    private final Account account;
    private final AuthController authController;
    private final FineController fineController;

    private JTable finesTable;
    private DefaultTableModel tableModel;

    public AuthorityDashboard(Account account) {
        this.account = account;
        this.authController = new AuthController();
        this.fineController = new FineController();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setTitle("PFAMS - Authority Dashboard");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 250));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(140, 80, 30));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel headerLabel = new JLabel("Authority Dashboard (Account #" + account.getAccountId() + ")");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        JButton issueBtn = new JButton("Issue New Fine");
        issueBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        issueBtn.setBackground(new Color(200, 60, 60));
        issueBtn.setForeground(Color.WHITE);
        issueBtn.setFocusPainted(false);
        issueBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> { new LoginScreen().setVisible(true); dispose(); });

        headerRight.add(issueBtn);
        headerRight.add(logoutButton);
        headerPanel.add(headerRight, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(140, 80, 30)),
                " All System Fines "));
        tablePanel.setBackground(Color.WHITE);

        String[] columns = {"Fine ID", "User", "Violation", "Location", "Issue Date",
                            "Due Date", "Base", "Penalty", "Total", "Status", "Authority"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        finesTable = new JTable(tableModel);
        finesTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        finesTable.setRowHeight(25);
        finesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        tablePanel.add(new JScrollPane(finesTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadData());
        bottomPanel.add(refreshButton);
        tablePanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel);
        issueBtn.addActionListener(e -> showIssueFineDialog());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Fine> fines = fineController.getAllFines();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Fine f : fines) {
            tableModel.addRow(new Object[]{
                f.getFineId(),
                f.getUserName() != null ? f.getUserName() : "N/A",
                f.getViolationName() != null ? f.getViolationName() : "N/A",
                f.getLocation() != null ? f.getLocation() : "N/A",
                f.getIssueDate() != null ? sdf.format(f.getIssueDate()) : "",
                f.getDueDate() != null ? sdf.format(f.getDueDate()) : "",
                f.getFineAmount(),
                f.getPenaltyAmount(),
                f.getTotalAmount(),
                f.getStatus(),
                f.getAuthorityName() != null ? f.getAuthorityName() : "N/A"
            });
        }
    }

    private void showIssueFineDialog() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 8));
        JTextField emailField = new JTextField();
        List<Object[]> types = authController.getViolationTypes();
        JComboBox<String> typeCombo = new JComboBox<>();
        for (Object[] t : types) {
            typeCombo.addItem(t[0] + " - " + t[1] + " (Rs." + t[2] + ")");
        }
        List<Object[]> auths = authController.getAuthorities();
        JComboBox<String> authCombo = new JComboBox<>();
        for (Object[] a : auths) {
            authCombo.addItem(a[0] + " - " + a[1] + " (" + a[2] + ")");
        }

        JTextField locationField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField dueDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(
                new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)));
        JTextField penaltyField = new JTextField("0");

        panel.add(new JLabel("User Email *:")); panel.add(emailField);
        panel.add(new JLabel("Violation Type *:")); panel.add(typeCombo);
        panel.add(new JLabel("Authority *:")); panel.add(authCombo);
        panel.add(new JLabel("Location:")); panel.add(locationField);
        panel.add(new JLabel("Fine Amount (blank=base):")); panel.add(amountField);
        panel.add(new JLabel("Due Date (yyyy-MM-dd):")); panel.add(dueDateField);
        panel.add(new JLabel("Penalty/Day (0=none):")); panel.add(penaltyField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Issue New Fine",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "User email is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = authController.findUserByEmail(email);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "No user found with email: " + email, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int targetAccountId = authController.getAccountId(user.getUserId());
            if (targetAccountId <= 0) {
                JOptionPane.showMessageDialog(this, "No account found for this user.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedTypeIdx = typeCombo.getSelectedIndex();
            int violationTypeId = (int) types.get(selectedTypeIdx)[0];
            double baseFine = (double) types.get(selectedTypeIdx)[2];

            int selectedAuthIdx = authCombo.getSelectedIndex();
            int authorityId = (int) auths.get(selectedAuthIdx)[0];

            String location = locationField.getText().trim();
            if (location.isEmpty()) location = "Not specified";

            double fineAmount = baseFine;
            if (!amountField.getText().trim().isEmpty()) {
                try { fineAmount = Double.parseDouble(amountField.getText().trim()); }
                catch (NumberFormatException ex) {  }
            }

            Date dueDate;
            try {
                dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateField.getText().trim());
            } catch (ParseException ex) {
                dueDate = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
            }

            double penaltyPerDay = 0;
            try { penaltyPerDay = Double.parseDouble(penaltyField.getText().trim()); }
            catch (NumberFormatException ex) {  }

            int fineId = fineController.issueFine(targetAccountId, violationTypeId, location,
                    authorityId, fineAmount, dueDate, penaltyPerDay);

            if (fineId > 0) {
                JOptionPane.showMessageDialog(this,
                        "Fine issued successfully!\nFine ID: " + fineId +
                        "\nUser: " + user.getFullName() +
                        "\nAmount: Rs." + fineAmount,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to issue fine.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

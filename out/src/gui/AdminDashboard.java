package gui;

import controller.AuthController;
import controller.FineController;
import model.Account;
import model.Fine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final Account account;
    private final AuthController authController;
    private final FineController fineController;

    private JTable finesTable;
    private DefaultTableModel tableModel;
    private JLabel totalFinesLabel, totalRevenueLabel, pendingAmountLabel;

    public AdminDashboard(Account account) {
        this.account = account;
        this.authController = new AuthController();
        this.fineController = new FineController();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setTitle("PFAMS - Admin Dashboard");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 250));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 50, 120));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel headerLabel = new JLabel("Admin Dashboard");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        JButton searchBtn = new JButton("Search Fines");
        searchBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchBtn.setFocusPainted(false);

        JButton auditBtn = new JButton("Audit Log");
        auditBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        auditBtn.setFocusPainted(false);

        JButton searchHistBtn = new JButton("Search History");
        searchHistBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchHistBtn.setFocusPainted(false);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> { new LoginScreen().setVisible(true); dispose(); });

        headerRight.add(searchBtn);
        headerRight.add(auditBtn);
        headerRight.add(searchHistBtn);
        headerRight.add(logoutButton);
        headerPanel.add(headerRight, BorderLayout.EAST);
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(new Color(245, 245, 250));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        totalFinesLabel = createReportCard("Total Fines", "0", new Color(70, 130, 180));
        totalRevenueLabel = createReportCard("Total Revenue", "Rs.0.00", new Color(60, 150, 60));
        pendingAmountLabel = createReportCard("Pending Amount", "Rs.0.00", new Color(200, 80, 60));

        JPanel topSection = new JPanel(new BorderLayout(0, 5));
        topSection.setBackground(new Color(245, 245, 250));
        topSection.add(headerPanel, BorderLayout.NORTH);
        summaryPanel.add(totalFinesLabel.getParent());
        summaryPanel.add(totalRevenueLabel.getParent());
        summaryPanel.add(pendingAmountLabel.getParent());
        topSection.add(summaryPanel, BorderLayout.SOUTH);
        mainPanel.add(topSection, BorderLayout.NORTH);
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 120)),
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
        refreshButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadData());
        bottomPanel.add(refreshButton);
        tablePanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel);
        searchBtn.addActionListener(e -> showSearchDialog());
        auditBtn.addActionListener(e -> showAuditLogDialog());
        searchHistBtn.addActionListener(e -> showSearchHistoryDialog());
    }

    private void loadData() {
        double[] report = fineController.getAdminReport();
        totalFinesLabel.setText(String.valueOf((int) report[0]));
        totalRevenueLabel.setText(String.format("Rs.%.2f", report[1]));
        pendingAmountLabel.setText(String.format("Rs.%.2f", report[2]));

        tableModel.setRowCount(0);
        List<Fine> fines = fineController.getAllFines();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Fine fine : fines) {
            tableModel.addRow(new Object[]{
                fine.getFineId(),
                fine.getUserName(), fine.getViolationName(), fine.getLocation(),
                fine.getIssueDate() != null ? sdf.format(fine.getIssueDate()) : "",
                fine.getDueDate() != null ? sdf.format(fine.getDueDate()) : "",
                fine.getFineAmount(), fine.getPenaltyAmount(), fine.getTotalAmount(),
                fine.getStatus(), fine.getAuthorityName()
            });
        }
    }


    private void showSearchDialog() {
        String keyword = JOptionPane.showInputDialog(this, "Search fines (by violation, user, location):", "Search Fines", JOptionPane.PLAIN_MESSAGE);
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Object[]> results = authController.searchFines(account.getAccountId(), keyword.trim());
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No results found for: " + keyword, "Search Results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-5s | %-12s | %-12s | %-10s | %-6s | %-7s | %-6s | %s%n", "ID", "User", "Violation", "Location", "Base", "Penalty", "Total", "Status"));
            sb.append("-".repeat(90)).append("\n");
            for (Object[] r : results) {
                sb.append(String.format("%-5d | %-12s | %-12s | %-10s | %-6.0f | %-7.0f | %-6.0f | %s%n",
                        (int) r[0], r[1], r[2], r[3], (double) r[4], (double) r[5], (double) r[6], r[7]));
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(700, 300));
            JOptionPane.showMessageDialog(this, scrollPane, "Search Results for: " + keyword, JOptionPane.PLAIN_MESSAGE);
        }
    }


    private void showAuditLogDialog() {
        List<Object[]> logs = authController.getAuditLogs();
        List<Object[]> summary = authController.getAuditSummary();

        StringBuilder sb = new StringBuilder();
        sb.append("=== AUDIT SUMMARY ===\n");
        for (Object[] s : summary) {
            sb.append(String.format("  %-25s : %d times%n", s[0], (int) s[1]));
        }
        sb.append("\n=== RECENT ACTIVITY ===\n");
        sb.append(String.format("%-5s | %-25s | %-10s | %s%n", "ID", "Action", "Table", "Time"));
        sb.append("-".repeat(70)).append("\n");
        for (Object[] l : logs) {
            sb.append(String.format("%-5d | %-25s | %-10s | %s%n", (int) l[0], l[1], l[2], l[3]));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Audit Log — System Activity Trail", JOptionPane.PLAIN_MESSAGE);
    }


    private void showSearchHistoryDialog() {
        List<Object[]> requests = authController.getAllSearchRequests();
        if (requests.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No search requests recorded yet.", "Search History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s | %-18s | %-25s | %s%n", "ID", "User", "Search Text", "Date"));
        sb.append("-".repeat(75)).append("\n");
        for (Object[] r : requests) {
            sb.append(String.format("%-5d | %-18s | %-25s | %s%n", (int) r[0], r[1], r[2], r[3]));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "All Search Requests", JOptionPane.PLAIN_MESSAGE);
    }

    private JLabel createReportCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLbl.setForeground(Color.GRAY);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLbl.setForeground(color);
        valueLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLbl);

        return valueLbl;
    }
}

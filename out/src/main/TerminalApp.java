package main;

import controller.AuthController;
import controller.FineController;
import controller.PaymentController;
import model.Account;
import model.Fine;
import model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TerminalApp {

    private final Scanner scanner;
    private final AuthController authController;
    private final FineController fineController;
    private final PaymentController paymentController;
    private Account loggedInAccount;

    public TerminalApp() {
        this.scanner = new Scanner(System.in);
        this.authController = new AuthController();
        this.fineController = new FineController();
        this.paymentController = new PaymentController();
    }

    public void run() {
        printBanner();

        while (true) {
            if (loggedInAccount == null) {
                if (!loginMenu()) break;
            } else {
                if (authController.isAdmin(loggedInAccount)) {
                    adminMenu();
                } else if (authController.isAuthority(loggedInAccount)) {
                    authorityMenu();
                } else {
                    userMenu();
                }
            }
        }

        System.out.println("\n Thank you for using PFAMS. Goodbye!\n");
    }

    private boolean loginMenu() {
        System.out.println("\n==========================================");
        System.out.println(" PFAMS - LOGIN / SIGNUP");
        System.out.println("==========================================");
        System.out.println(" 1. Login");
        System.out.println(" 2. Sign Up (Create New Account)");
        System.out.println(" 3. Exit");
        System.out.println("==========================================");
        System.out.print(" Enter choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": return performLogin();
            case "2": performSignup(); return true;
            case "3": return false;
            default: System.out.println(" Invalid choice."); return true;
        }
    }

    private boolean performLogin() {
        System.out.println("\n------------------------------------------");
        System.out.println(" LOGIN");
        System.out.println("------------------------------------------");
        System.out.print(" Email    : ");
        String email = scanner.nextLine().trim();
        if (email.equalsIgnoreCase("exit")) return false;
        System.out.print(" Password : ");
        String password = scanner.nextLine().trim();

        Account account = authController.handleLogin(email, password);
        if (account != null) {
            loggedInAccount = account;
            String role = authController.getRole(account);
            User user = authController.getUserDetails(account.getUserId());
            String name = (user != null) ? user.getFullName() : "User";
            System.out.println("\n Login successful! Welcome, " + name + " [" + role + "]");
        } else {
            System.out.println("\n [ERROR] Invalid email or password.");
        }
        return true;
    }

    private void performSignup() {
        System.out.println("\n------------------------------------------");
        System.out.println(" SIGN UP - Create New Account");
        System.out.println("------------------------------------------");
        System.out.print(" First Name : ");
        String firstName = scanner.nextLine().trim();
        if (firstName.isEmpty()) { System.out.println(" First name is required."); return; }

        System.out.print(" Last Name  : ");
        String lastName = scanner.nextLine().trim();

        System.out.print(" Email      : ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) { System.out.println(" Email is required."); return; }

        if (authController.isEmailTaken(email)) {
            System.out.println("\n [ERROR] Email '" + email + "' is already registered.");
            return;
        }

        System.out.print(" Phone No   : ");
        String phone = scanner.nextLine().trim();
        System.out.print(" Address    : ");
        String address = scanner.nextLine().trim();
        System.out.print(" Password   : ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) { System.out.println(" Password is required."); return; }
        System.out.print(" Confirm    : ");
        String confirm = scanner.nextLine().trim();
        if (!password.equals(confirm)) { System.out.println("\n [ERROR] Passwords do not match."); return; }

        Account account = authController.handleSignup(firstName, lastName, email, address, phone, password, 2);
        if (account != null) {
            System.out.println("\n Account created successfully!");
            System.out.printf(" Your Account ID: %d%n", account.getAccountId());
            System.out.println(" You can now login with your email and password.");
        } else {
            System.out.println("\n [ERROR] Signup failed. Please try again.");
        }
    }

    private void logout() {
        loggedInAccount = null;
        System.out.println("\n Logged out successfully.");
    }

    private void userMenu() {
        while (loggedInAccount != null) {
            System.out.println("\n==========================================");
            System.out.println(" USER DASHBOARD");
            System.out.println("==========================================");
            System.out.println(" 1. View My Profile");
            System.out.println(" 2. View My Fines");
            System.out.println(" 3. View Pending (Unpaid) Fines");
            System.out.println(" 4. View Total Fine");
            System.out.println(" 5. Pay a Fine");
            System.out.println(" 6. Search Fines");
            System.out.println(" 7. View My Search History");
            System.out.println(" 8. Logout");
            System.out.println("==========================================");
            System.out.print(" Enter choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": viewProfile(); break;
                case "2": viewMyFines(); break;
                case "3": viewPendingFines(); break;
                case "4": viewTotalFine(); break;
                case "5": payFine(); break;
                case "6": searchFines(); break;
                case "7": viewSearchHistory(); break;
                case "8": logout(); return;
                default: System.out.println(" Invalid choice.");
            }
        }
    }

    private void viewProfile() {
        User user = authController.getUserDetails(loggedInAccount.getUserId());
        if (user == null) { System.out.println("\n [ERROR] Could not fetch user details."); return; }
        System.out.println("\n------------------------------------------");
        System.out.println(" USER PROFILE");
        System.out.println("------------------------------------------");
        System.out.printf(" %-12s : %s%n", "Name", user.getFullName());
        System.out.printf(" %-12s : %s%n", "Email", user.getEmail());
        System.out.printf(" %-12s : %s%n", "Phone", user.getPhoneNo() != null ? user.getPhoneNo() : "N/A");
        System.out.printf(" %-12s : %s%n", "Address", user.getAddress() != null ? user.getAddress() : "N/A");
        System.out.printf(" %-12s : %s%n", "Join Date", user.getJoinDate() != null ? user.getJoinDate().toString() : "N/A");
        System.out.printf(" %-12s : %d%n", "Account ID", loggedInAccount.getAccountId());
        System.out.println("------------------------------------------");
    }

    private void viewMyFines() {
        List<Fine> fines = fineController.getFinesForAccount(loggedInAccount.getAccountId());
        if (fines.isEmpty()) { System.out.println("\n No fines found for your account."); return; }
        System.out.println("\n------------------------------------------");
        System.out.println(" YOUR FINES");
        System.out.println("------------------------------------------");
        printFineTable(fines, false);
    }

    private void viewPendingFines() {
        List<Fine> fines = fineController.getPendingFines(loggedInAccount.getAccountId());
        if (fines.isEmpty()) {
            System.out.println("\n No pending (unpaid) fines.");
            return;
        }
        System.out.println("\n------------------------------------------");
        System.out.println(" PENDING FINES");
        System.out.println("------------------------------------------");
        System.out.printf(" %-8s | %-8s | %-8s | %-9s | %-8s%n", "FineID", "Base", "Penalty", "Total", "Status");
        System.out.println(" ---------+----------+----------+-----------+---------");
        for (Fine f : fines) {
            System.out.printf(" %-8d | %-8.0f | %-8.0f | Rs.%-6.2f | %s%n", f.getFineId(), f.getFineAmount(), f.getPenaltyAmount(), f.getTotalAmount(), f.getStatus());
        }
    }

    private void viewTotalFine() {
        double total = fineController.getTotalFine(loggedInAccount.getAccountId());
        System.out.println("\n------------------------------------------");
        System.out.println(" TOTAL FINE");
        System.out.println("------------------------------------------");
        System.out.printf(" Your Total Fine: Rs.%.2f%n", total);
        System.out.println("------------------------------------------");
    }

    private void payFine() {
        List<Fine> fines = fineController.getFinesForAccount(loggedInAccount.getAccountId());
        if (fines.isEmpty()) { System.out.println("\n No fines to pay."); return; }

        System.out.println("\n------------------------------------------");
        System.out.println(" PAY A FINE");
        System.out.println("------------------------------------------");
        printFineTable(fines, false);

        System.out.print("\n Enter Fine ID to pay (0 to cancel): ");
        int fineId = readInt();
        if (fineId == 0) return;

        Fine selectedFine = null;
        for (Fine f : fines) { if (f.getFineId() == fineId) { selectedFine = f; break; } }

        if (selectedFine == null) { System.out.println(" Fine not found in your fines."); return; }
        if ("Paid".equals(selectedFine.getStatus())) { System.out.println(" This fine is already paid."); return; }

        System.out.println("\n Payment Modes: 1.UPI  2.Card  3.NetBanking  4.Cash");
        System.out.print(" Select mode (1-4): ");
        String[] modes = {"UPI", "Card", "NetBanking", "Cash"};
        int modeIdx = readInt() - 1;
        if (modeIdx < 0 || modeIdx >= modes.length) { System.out.println(" Invalid mode."); return; }

        System.out.printf("\n Confirm payment of Rs.%.2f for Fine #%d via %s? (y/n): ",
                selectedFine.getTotalAmount(), fineId, modes[modeIdx]);
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            boolean success = paymentController.payFine(fineId, selectedFine.getTotalAmount(), modes[modeIdx]);
            if (success) {
                System.out.println("\n Payment successful! Fine status updated to 'Paid'.");
            } else {
                System.out.println("\n [ERROR] Payment failed.");
            }
        } else {
            System.out.println(" Payment cancelled.");
        }
    }

    private void searchFines() {
        searchFines(false);
    }

    private void searchFines(boolean adminMode) {
        System.out.print("\n Enter search keyword (violation, location, or user name): ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) { System.out.println(" Keyword is required."); return; }

        List<Object[]> results = authController.searchFines(loggedInAccount.getAccountId(), keyword);
        if (!adminMode) {
            int myAccountId = loggedInAccount.getAccountId();
            List<Fine> myFines = fineController.getFinesForAccount(myAccountId);
            java.util.Set<Integer> myFineIds = new java.util.HashSet<>();
            for (Fine f : myFines) myFineIds.add(f.getFineId());
            results.removeIf(r -> !myFineIds.contains((int) r[0]));
        }

        System.out.println("\n------------------------------------------");
        System.out.println(" SEARCH RESULTS for '" + keyword + "'");
        System.out.println("------------------------------------------");

        if (results.isEmpty()) {
            System.out.println(" No results found.");
            return;
        }

        System.out.printf(" %-6s | %-15s | %-15s | %-12s | %-6s | %-7s | %-6s | %s%n",
                "FineID", "User", "Violation", "Location", "Base", "Penalty", "Total", "Status");
        System.out.println(" " + "-".repeat(95));
        for (Object[] r : results) {
            System.out.printf(" %-6d | %-15s | %-15s | %-12s | %-6.0f | %-7.0f | %-6.0f | %s%n",
                    (int) r[0], r[1], r[2], r[3], (double) r[4], (double) r[5], (double) r[6], r[7]);
        }
    }

    private void viewSearchHistory() {
        List<Object[]> history = authController.getSearchHistory(loggedInAccount.getAccountId());
        if (history.isEmpty()) { System.out.println("\n No search history."); return; }

        System.out.println("\n------------------------------------------");
        System.out.println(" YOUR SEARCH HISTORY");
        System.out.println("------------------------------------------");
        System.out.printf(" %-5s | %-25s | %s%n", "ID", "Search Text", "Date");
        System.out.println(" ------+---------------------------+-----------");
        for (Object[] h : history) {
            System.out.printf(" %-5d | %-25s | %s%n", (int) h[0], h[1], h[2]);
        }
    }

    private void adminMenu() {
        while (loggedInAccount != null) {
            System.out.println("\n==========================================");
            System.out.println(" ADMIN DASHBOARD");
            System.out.println("==========================================");
            System.out.println(" 1. View System Report");
            System.out.println(" 2. View All Fines");
            System.out.println(" 3. View Pending Fines");
            System.out.println(" 4. View Total Fine Per User");
            System.out.println(" 5. View Overdue Fines");
            System.out.println(" 6. Lookup User Total Fine");
            System.out.println(" 7. Search Fines");
            System.out.println(" 8. View Audit Log");
            System.out.println(" 9. View All Search Requests");
            System.out.println(" 10. View All Users");
            System.out.println(" 11. Logout");
            System.out.println("==========================================");
            System.out.print(" Enter choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": viewAdminReport(); break;
                case "2": viewAllFines(); break;
                case "3": viewAllPendingFines(); break;
                case "4": viewTotalFinePerUser(); break;
                case "5": viewOverdueFines(); break;
                case "6": lookupUserTotalFine(); break;
                case "7": searchFines(true); break;
                case "8": viewAuditLog(); break;
                case "9": viewAllSearchRequests(); break;
                case "10": viewAllUsers(); break;
                case "11": logout(); return;
                default: System.out.println(" Invalid choice.");
            }
        }
    }

    private void viewAdminReport() {
        double[] report = fineController.getAdminReport();
        System.out.println("\n------------------------------------------");
        System.out.println(" SYSTEM REPORT");
        System.out.println("------------------------------------------");
        System.out.printf(" Total Fines Issued  : %d%n", (int) report[0]);
        System.out.printf(" Total Revenue       : Rs.%.2f%n", report[1]);
        System.out.printf(" Pending Amount      : Rs.%.2f%n", report[2]);
        System.out.println("------------------------------------------");
    }

    private void viewAllFines() {
        List<Fine> fines = fineController.getAllFines();
        if (fines.isEmpty()) { System.out.println("\n No fines in the system."); return; }
        System.out.println("\n------------------------------------------");
        System.out.println(" ALL SYSTEM FINES");
        System.out.println("------------------------------------------");
        printFineTable(fines, true);
    }

    private void viewAllPendingFines() {
        List<Fine> allFines = fineController.getAllFines();
        System.out.println("\n------------------------------------------");
        System.out.println(" PENDING FINES");
        System.out.println("------------------------------------------");
        boolean found = false;
        System.out.printf(" %-8s | %-18s | %-6s | %-7s | %-8s | %-8s%n", "FineID", "User", "Base", "Penalty", "Total", "Status");
        System.out.println(" ---------+--------------------+--------+---------+----------+---------");
        for (Fine f : allFines) {
            if ("Unpaid".equals(f.getStatus())) {
                System.out.printf(" %-8d | %-18s | %-6.0f | %-7.0f | Rs.%-5.0f | %s%n",
                        f.getFineId(), f.getUserName(), f.getFineAmount(), f.getPenaltyAmount(), f.getTotalAmount(), f.getStatus());
                found = true;
            }
        }
        if (!found) System.out.println(" No pending fines.");
    }

    private void viewTotalFinePerUser() {
        List<Object[]> data = fineController.getTotalFinePerUser();
        if (data.isEmpty()) { System.out.println("\n No data available."); return; }
        System.out.println("\n------------------------------------------");
        System.out.println(" TOTAL FINE PER USER");
        System.out.println("------------------------------------------");
        System.out.printf(" %-8s | %-12s | %-12s | %s%n", "UserID", "FirstName", "LastName", "TotalFine");
        System.out.println(" ---------+--------------+--------------+-----------");
        for (Object[] row : data) {
            System.out.printf(" %-8d | %-12s | %-12s | Rs.%.2f%n",
                    (int) row[0], (String) row[1], (String) row[2], (double) row[3]);
        }
    }

    private void viewOverdueFines() {
        List<Fine> allFines = fineController.getAllFines();
        System.out.println("\n------------------------------------------");
        System.out.println(" OVERDUE FINES");
        System.out.println("------------------------------------------");
        boolean found = false;
        System.out.printf(" %-8s | %-12s | %-6s | %-7s | %-7s | %s%n", "FineID", "Due Date", "Base", "Penalty", "Total", "Status");
        System.out.println(" ---------+--------------+--------+---------+---------+--------------------");
        for (Fine f : allFines) {
            if ("Unpaid".equals(f.getStatus()) && f.getDueDate() != null) {
                long diff = (System.currentTimeMillis() - f.getDueDate().getTime()) / (1000 * 60 * 60 * 24);
                if (diff > 0) {
                    System.out.printf(" %-8d | %-12s | %-6.0f | %-7.0f | %-7.0f | Overdue by %d days%n",
                            f.getFineId(), f.getDueDate().toString(), f.getFineAmount(), f.getPenaltyAmount(), f.getTotalAmount(), diff);
                    found = true;
                }
            }
        }
        if (!found) System.out.println(" No overdue fines.");
    }

    private void lookupUserTotalFine() {
        System.out.print("\n Enter Account ID to lookup: ");
        int accountId = readInt();
        if (accountId <= 0) return;
        double total = fineController.getTotalFine(accountId);
        System.out.printf("\n Account ID %d -> Total Fine: Rs.%.2f%n", accountId, total);
    }

    private void viewAuditLog() {
        List<Object[]> summary = authController.getAuditSummary();
        List<Object[]> logs = authController.getAuditLogs();

        System.out.println("\n------------------------------------------");
        System.out.println(" AUDIT LOG");
        System.out.println("------------------------------------------");

        if (!summary.isEmpty()) {
            System.out.println("\n  SUMMARY:");
            for (Object[] s : summary) {
                System.out.printf("   %-25s : %d times%n", s[0], (int) s[1]);
            }
        }

        if (logs.isEmpty()) {
            System.out.println("\n No audit log entries.");
            return;
        }
        System.out.println("\n  RECENT ACTIVITY:");
        System.out.printf("  %-5s | %-25s | %-10s | %s%n", "ID", "Action", "Table", "Time");
        System.out.println("  " + "-".repeat(65));
        for (Object[] l : logs) {
            System.out.printf("  %-5d | %-25s | %-10s | %s%n", (int) l[0], l[1], l[2], l[3]);
        }
    }

    private void viewAllSearchRequests() {
        List<Object[]> requests = authController.getAllSearchRequests();
        if (requests.isEmpty()) { System.out.println("\n No search requests recorded."); return; }

        System.out.println("\n------------------------------------------");
        System.out.println(" ALL SEARCH REQUESTS");
        System.out.println("------------------------------------------");
        System.out.printf(" %-5s | %-18s | %-25s | %s%n", "ID", "User", "Search Text", "Date");
        System.out.println(" ------+--------------------+---------------------------+-----------");
        for (Object[] r : requests) {
            System.out.printf(" %-5d | %-18s | %-25s | %s%n", (int) r[0], r[1], r[2], r[3]);
        }
    }

    private void authorityMenu() {
        while (loggedInAccount != null) {
            System.out.println("\n==========================================");
            System.out.println(" AUTHORITY DASHBOARD");
            System.out.println("==========================================");
            System.out.println(" 1. Issue a Fine / Penalty to a User");
            System.out.println(" 2. View Fines Issued by Authority");
            System.out.println(" 3. View All Users");
            System.out.println(" 4. View All Violation Types");
            System.out.println(" 5. Search Fines");
            System.out.println(" 6. View System Report");
            System.out.println(" 7. Logout");
            System.out.println("==========================================");
            System.out.print(" Enter choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": issueFine(); break;
                case "2": viewIssuedFines(); break;
                case "3": viewAllUsers(); break;
                case "4": viewViolationTypes(); break;
                case "5": searchFines(true); break;
                case "6": viewAdminReport(); break;
                case "7": logout(); return;
                default: System.out.println(" Invalid choice.");
            }
        }
    }

    private void issueFine() {
        System.out.println("\n------------------------------------------");
        System.out.println(" ISSUE A FINE / PENALTY");
        System.out.println("------------------------------------------");
        System.out.println("\n Identify the user:");
        System.out.println("   1. By Email");
        System.out.println("   2. By Account ID");
        System.out.print(" Choice: ");
        String userChoice = scanner.nextLine().trim();

        int targetAccountId = -1;
        String targetUserName = "";

        if (userChoice.equals("1")) {
            System.out.print(" Enter user's email: ");
            String email = scanner.nextLine().trim();
            User user = authController.findUserByEmail(email);
            if (user == null) { System.out.println(" [ERROR] No user found."); return; }
            targetAccountId = authController.getAccountId(user.getUserId());
            targetUserName = user.getFullName();
        } else if (userChoice.equals("2")) {
            System.out.print(" Enter Account ID: ");
            targetAccountId = readInt();
        } else {
            System.out.println(" Invalid choice."); return;
        }

        if (targetAccountId <= 0) { System.out.println(" [ERROR] Invalid account."); return; }
        System.out.println(" Target: " + (targetUserName.isEmpty() ? "Account #" + targetAccountId : targetUserName));
        List<Object[]> types = authController.getViolationTypes();
        if (types.isEmpty()) { System.out.println(" [ERROR] No violation types."); return; }
        System.out.println("\n Available Violation Types:");
        System.out.printf(" %-4s | %-25s | %s%n", "ID", "Name", "Base Fine");
        System.out.println(" -----+---------------------------+----------");
        for (Object[] t : types) {
            System.out.printf(" %-4d | %-25s | Rs.%.2f%n", (int) t[0], (String) t[1], (double) t[2]);
        }
        System.out.print("\n Enter Violation Type ID: ");
        int violationTypeId = readInt();
        if (violationTypeId <= 0) return;

        double baseFine = 0; String violationName = "";
        for (Object[] t : types) {
            if ((int) t[0] == violationTypeId) { baseFine = (double) t[2]; violationName = (String) t[1]; break; }
        }
        if (violationName.isEmpty()) { System.out.println(" [ERROR] Invalid violation type."); return; }
        System.out.print(" Location of violation: ");
        String location = scanner.nextLine().trim();
        if (location.isEmpty()) location = "Not specified";
        System.out.printf(" Fine Amount (base: Rs.%.2f, Enter for base): ", baseFine);
        String amountStr = scanner.nextLine().trim();
        double fineAmount = amountStr.isEmpty() ? baseFine : 0;
        if (!amountStr.isEmpty()) {
            try { fineAmount = Double.parseDouble(amountStr); }
            catch (NumberFormatException e) { fineAmount = baseFine; }
        }
        System.out.print(" Due Date (yyyy-MM-dd): ");
        String dueDateStr = scanner.nextLine().trim();
        Date dueDate;
        try { dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateStr); }
        catch (ParseException e) { dueDate = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); }
        System.out.print(" Penalty per day for late payment (0 for none): ");
        double penaltyPerDay = 0;
        try { penaltyPerDay = Double.parseDouble(scanner.nextLine().trim()); }
        catch (NumberFormatException e) {  }
        List<Object[]> authorities = authController.getAuthorities();
        int authorityId = 1;
        if (!authorities.isEmpty()) {
            System.out.println("\n Select Authority record:");
            System.out.printf(" %-4s | %-25s | %s%n", "ID", "Name", "Department");
            System.out.println(" -----+---------------------------+-----------");
            for (Object[] a : authorities) {
                System.out.printf(" %-4d | %-25s | %s%n", (int) a[0], (String) a[1], (String) a[2]);
            }
            System.out.print(" Enter Authority ID: ");
            authorityId = readInt();
            if (authorityId <= 0) authorityId = 1;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("\n------------------------------------------");
        System.out.println(" CONFIRM FINE DETAILS");
        System.out.println("------------------------------------------");
        System.out.printf(" User         : %s (Account #%d)%n",
                targetUserName.isEmpty() ? "Account #" + targetAccountId : targetUserName, targetAccountId);
        System.out.printf(" Violation    : %s%n", violationName);
        System.out.printf(" Location     : %s%n", location);
        System.out.printf(" Fine Amount  : Rs.%.2f%n", fineAmount);
        System.out.printf(" Due Date     : %s%n", sdf.format(dueDate));
        System.out.printf(" Penalty/Day  : Rs.%.2f%n", penaltyPerDay);
        System.out.println("------------------------------------------");
        System.out.print(" Issue this fine? (y/n): ");

        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            int fineId = fineController.issueFine(targetAccountId, violationTypeId, location,
                    authorityId, fineAmount, dueDate, penaltyPerDay);
            if (fineId > 0) {
                System.out.println("\n Fine issued successfully! Fine ID: " + fineId);
            } else {
                System.out.println("\n [ERROR] Failed to issue fine.");
            }
        } else {
            System.out.println(" Cancelled.");
        }
    }

    private void viewIssuedFines() {
        List<Object[]> authorities = authController.getAuthorities();
        if (authorities.isEmpty()) { System.out.println("\n No authorities."); return; }
        System.out.println("\n Select your Authority record:");
        System.out.printf(" %-4s | %-25s | %s%n", "ID", "Name", "Department");
        System.out.println(" -----+---------------------------+-----------");
        for (Object[] a : authorities) {
            System.out.printf(" %-4d | %-25s | %s%n", (int) a[0], (String) a[1], (String) a[2]);
        }
        System.out.print(" Enter Authority ID: ");
        int authorityId = readInt();
        if (authorityId <= 0) return;

        List<Fine> fines = fineController.getFinesByAuthority(authorityId);
        if (fines.isEmpty()) { System.out.println("\n No fines issued by this authority."); return; }
        System.out.println("\n FINES ISSUED BY AUTHORITY #" + authorityId);
        printFineTable(fines, true);
    }

    private void viewAllUsers() {
        List<User> users = authController.getAllUsers();
        if (users.isEmpty()) { System.out.println("\n No users found."); return; }
        System.out.println("\n------------------------------------------");
        System.out.println(" ALL USERS (Role = User)");
        System.out.println("------------------------------------------");
        System.out.printf(" %-6s | %-20s | %-25s | %s%n", "UserID", "Name", "Email", "Phone");
        System.out.println(" " + "-".repeat(80));
        for (User u : users) {
            System.out.printf(" %-6d | %-20s | %-25s | %s%n",
                    u.getUserId(), u.getFullName(), u.getEmail(),
                    u.getPhoneNo() != null ? u.getPhoneNo() : "N/A");
        }
    }

    private void viewViolationTypes() {
        List<Object[]> types = authController.getViolationTypes();
        if (types.isEmpty()) { System.out.println("\n No violation types."); return; }
        System.out.println("\n------------------------------------------");
        System.out.println(" VIOLATION TYPES");
        System.out.println("------------------------------------------");
        System.out.printf(" %-4s | %-25s | %s%n", "ID", "Name", "Base Fine");
        System.out.println(" -----+---------------------------+----------");
        for (Object[] t : types) {
            System.out.printf(" %-4d | %-25s | Rs.%.2f%n", (int) t[0], (String) t[1], (double) t[2]);
        }
    }

    private int readInt() {
        String input = scanner.nextLine().trim();
        try { return Integer.parseInt(input); }
        catch (NumberFormatException e) { System.out.println(" Invalid number."); return -1; }
    }

    private void printFineTable(List<Fine> fines, boolean showUser) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (showUser) {
            System.out.printf(" %-6s | %-15s | %-12s | %-10s | %-10s | %-10s | %-6s | %-6s | %-6s | %-8s | %s%n",
                    "ID", "User", "Violation", "Location", "IssueDate", "DueDate", "Base", "Pen", "Total", "Status", "Authority");
            System.out.println(" " + "-".repeat(120));
        } else {
            System.out.printf(" %-6s | %-15s | %-10s | %-10s | %-10s | %-6s | %-6s | %-6s | %s%n",
                    "ID", "Violation", "Location", "IssueDate", "DueDate", "Base", "Pen", "Total", "Status");
            System.out.println(" " + "-".repeat(95));
        }

        for (Fine f : fines) {
            String issueDate = f.getIssueDate() != null ? sdf.format(f.getIssueDate()) : "N/A";
            String dueDate = f.getDueDate() != null ? sdf.format(f.getDueDate()) : "N/A";
            if (showUser) {
                System.out.printf(" %-6d | %-15s | %-12s | %-10s | %-10s | %-10s | %-6.0f | %-6.0f | %-6.0f | %-8s | %s%n",
                        f.getFineId(), nn(f.getUserName()), nn(f.getViolationName()), nn(f.getLocation()),
                        issueDate, dueDate, f.getFineAmount(), f.getPenaltyAmount(), f.getTotalAmount(), f.getStatus(), nn(f.getAuthorityName()));
            } else {
                System.out.printf(" %-6d | %-15s | %-10s | %-10s | %-10s | %-6.0f | %-6.0f | %-6.0f | %s%n",
                        f.getFineId(), nn(f.getViolationName()), nn(f.getLocation()),
                        issueDate, dueDate, f.getFineAmount(), f.getPenaltyAmount(), f.getTotalAmount(), f.getStatus());
            }
        }
    }

    private String nn(String s) { return s != null ? s : "N/A"; }

    private void printBanner() {
        System.out.println();
        System.out.println("==========================================");
        System.out.println(" PFAMS - Penalty/Fine Management System");
        System.out.println("       Terminal Mode (CLI)");
        System.out.println("==========================================");
    }
}

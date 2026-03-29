package model;

public class Account {
    private int accountId;
    private int userId;
    private String password;
    private int roleId;
    private double totalFine;

    public Account() {}

    public Account(int accountId, int userId, String password, int roleId, double totalFine) {
        this.accountId = accountId;
        this.userId = userId;
        this.password = password;
        this.roleId = roleId;
        this.totalFine = totalFine;
    }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public double getTotalFine() { return totalFine; }
    public void setTotalFine(double totalFine) { this.totalFine = totalFine; }
}

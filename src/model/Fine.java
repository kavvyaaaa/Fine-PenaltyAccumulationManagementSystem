package model;

import java.util.Date;

public class Fine {
    private int fineId;
    private int violationId;
    private int authorityId;
    private Date issueDate;
    private Date dueDate;
    private double fineAmount;
    private double penaltyAmount;
    private double totalAmount;
    private String status;
    private String violationName;
    private String location;
    private String authorityName;
    private String userName;
    private int accountId;

    public Fine() {}

    public Fine(int fineId, int violationId, int authorityId, Date issueDate,
                Date dueDate, double fineAmount, String status) {
        this.fineId = fineId;
        this.violationId = violationId;
        this.authorityId = authorityId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.fineAmount = fineAmount;
        this.status = status;
    }
    public int getFineId() { return fineId; }
    public void setFineId(int fineId) { this.fineId = fineId; }

    public int getViolationId() { return violationId; }
    public void setViolationId(int violationId) { this.violationId = violationId; }

    public int getAuthorityId() { return authorityId; }
    public void setAuthorityId(int authorityId) { this.authorityId = authorityId; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public double getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(double penaltyAmount) { this.penaltyAmount = penaltyAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getViolationName() { return violationName; }
    public void setViolationName(String violationName) { this.violationName = violationName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAuthorityName() { return authorityName; }
    public void setAuthorityName(String authorityName) { this.authorityName = authorityName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
}

package model;

import java.util.Date;

public class Violation {
    private int violationId;
    private int accountId;
    private int violationTypeId;
    private Date vDate;
    private String location;
    private String violationName;
    private double baseFine;

    public Violation() {}

    public Violation(int violationId, int accountId, int violationTypeId, Date vDate, String location) {
        this.violationId = violationId;
        this.accountId = accountId;
        this.violationTypeId = violationTypeId;
        this.vDate = vDate;
        this.location = location;
    }
    public int getViolationId() { return violationId; }
    public void setViolationId(int violationId) { this.violationId = violationId; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public int getViolationTypeId() { return violationTypeId; }
    public void setViolationTypeId(int violationTypeId) { this.violationTypeId = violationTypeId; }

    public Date getVDate() { return vDate; }
    public void setVDate(Date vDate) { this.vDate = vDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getViolationName() { return violationName; }
    public void setViolationName(String violationName) { this.violationName = violationName; }

    public double getBaseFine() { return baseFine; }
    public void setBaseFine(double baseFine) { this.baseFine = baseFine; }
}

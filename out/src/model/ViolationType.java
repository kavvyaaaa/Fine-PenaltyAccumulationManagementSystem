package model;

public class ViolationType {
    private int violationTypeId;
    private String vName;
    private double baseFine;

    public ViolationType() {}

    public ViolationType(int violationTypeId, String vName, double baseFine) {
        this.violationTypeId = violationTypeId;
        this.vName = vName;
        this.baseFine = baseFine;
    }

    public int getViolationTypeId() { return violationTypeId; }
    public void setViolationTypeId(int violationTypeId) { this.violationTypeId = violationTypeId; }

    public String getVName() { return vName; }
    public void setVName(String vName) { this.vName = vName; }

    public double getBaseFine() { return baseFine; }
    public void setBaseFine(double baseFine) { this.baseFine = baseFine; }
}

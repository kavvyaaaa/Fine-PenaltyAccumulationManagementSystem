package model;

public class Authority {
    private int authorityId;
    private String aName;
    private String department;

    public Authority() {}

    public Authority(int authorityId, String aName, String department) {
        this.authorityId = authorityId;
        this.aName = aName;
        this.department = department;
    }

    public int getAuthorityId() { return authorityId; }
    public void setAuthorityId(int authorityId) { this.authorityId = authorityId; }

    public String getAName() { return aName; }
    public void setAName(String aName) { this.aName = aName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}

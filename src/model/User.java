package model;

import java.util.Date;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private Date joinDate;
    private String phoneNo;

    public User() {}

    public User(int userId, String firstName, String lastName, String email,
                String address, Date joinDate, String phoneNo) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.joinDate = joinDate;
        this.phoneNo = phoneNo;
    }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getJoinDate() { return joinDate; }
    public void setJoinDate(Date joinDate) { this.joinDate = joinDate; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}

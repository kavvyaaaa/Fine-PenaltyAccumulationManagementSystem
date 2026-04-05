package controller;

import model.Account;
import model.User;
import service.AuthService;

import java.sql.SQLException;
import java.util.List;

public class AuthController {

    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    public Account handleLogin(String email, String password) {
        try { return authService.login(email, password); }
        catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public Account handleSignup(String firstName, String lastName, String email,
                                String address, String phoneNo, String password, int roleId) {
        try { return authService.register(firstName, lastName, email, address, phoneNo, password, roleId); }
        catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public boolean isEmailTaken(String email) {
        try { return authService.isEmailTaken(email); }
        catch (SQLException e) { e.printStackTrace(); return true; }
    }

    public String getRole(Account account) {
        try { return authService.getUserRole(account); }
        catch (SQLException e) { e.printStackTrace(); return "Unknown"; }
    }

    public boolean isAdmin(Account account) { return authService.isAdmin(account); }
    public boolean isAuthority(Account account) { return authService.isAuthority(account); }

    public User getUserDetails(int userId) {
        try { return authService.getUserDetails(userId); }
        catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public User findUserByEmail(String email) {
        try { return authService.findUserByEmail(email); }
        catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public int getAccountId(int userId) {
        try { return authService.getAccountId(userId); }
        catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    public boolean accountExists(int accountId) {
        try { return authService.accountExists(accountId); }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<User> getAllUsers() {
        try { return authService.getAllUsers(); }
        catch (SQLException e) { e.printStackTrace(); return new java.util.ArrayList<>(); }
    }

    public List<Object[]> getViolationTypes() {
        try { return authService.getViolationTypes(); }
        catch (SQLException e) { e.printStackTrace(); return new java.util.ArrayList<>(); }
    }

    public List<Object[]> getAuthorities() {
        try { return authService.getAuthorities(); }
        catch (SQLException e) { e.printStackTrace(); return new java.util.ArrayList<>(); }
    }


    public List<Object[]> searchFines(int accountId, String keyword) {
        try { return authService.searchFines(accountId, keyword); }
        catch (SQLException e) { e.printStackTrace(); return new java.util.ArrayList<>(); }
    }


    public List<Object[]> getSearchHistory(int accountId) {
        try { return authService.getSearchHistory(accountId); }
        catch (SQLException e) { e.printStackTrace(); return new java.util.ArrayList<>(); }
    }


    public List<Object[]> getAllSearchRequests() {
        try { return authService.getAllSearchRequests(); }
        catch (SQLException e) { e.printStackTrace(); return new java.util.ArrayList<>(); }
    }


    public List<Object[]> getAuditLogs() {
        try { return authService.getAuditLogs(); }
        catch (SQLException e) { e.printStackTrace(); return new java.util.ArrayList<>(); }
    }


    public List<Object[]> getAuditSummary() {
        try { return authService.getAuditSummary(); }
        catch (SQLException e) { e.printStackTrace(); return new java.util.ArrayList<>(); }
    }
}

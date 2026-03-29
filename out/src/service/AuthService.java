package service;

import dao.UserDAO;
import dao.SearchRequestDAO;
import dao.AuditLogDAO;
import model.Account;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class AuthService {

    private final UserDAO userDAO;
    private final SearchRequestDAO searchRequestDAO;
    private final AuditLogDAO auditLogDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.searchRequestDAO = new SearchRequestDAO();
        this.auditLogDAO = new AuditLogDAO();
    }

    public Account login(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return null;
        }
        return userDAO.authenticate(email.trim(), password);
    }

    public String getUserRole(Account account) throws SQLException {
        return userDAO.getRoleName(account.getRoleId());
    }

    public boolean isAdmin(Account account) { return account.getRoleId() == 1; }
    public boolean isAuthority(Account account) { return account.getRoleId() == 3; }

    public User getUserDetails(int userId) throws SQLException {
        return userDAO.getUserById(userId);
    }

    public Account register(String firstName, String lastName, String email,
                            String address, String phoneNo, String password, int roleId) throws SQLException {
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty()) {
            return null;
        }
        if (userDAO.emailExists(email.trim())) return null;

        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName != null ? lastName.trim() : "");
        user.setEmail(email.trim());
        user.setAddress(address != null ? address.trim() : "");
        user.setPhoneNo(phoneNo != null ? phoneNo.trim() : "");

        return userDAO.createUserWithAccount(user, password, roleId);
    }

    public boolean isEmailTaken(String email) throws SQLException {
        return userDAO.emailExists(email);
    }

    public User findUserByEmail(String email) throws SQLException {
        return userDAO.getUserByEmail(email);
    }

    public int getAccountId(int userId) throws SQLException {
        return userDAO.getAccountIdByUserId(userId);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public List<Object[]> getViolationTypes() throws SQLException {
        return userDAO.getAllViolationTypes();
    }

    public List<Object[]> getAuthorities() throws SQLException {
        return userDAO.getAllAuthorities();
    }


    public List<Object[]> searchFines(int accountId, String keyword) throws SQLException {
        searchRequestDAO.insertSearchRequest(accountId, keyword);
        return searchRequestDAO.searchFines(keyword);
    }


    public List<Object[]> getSearchHistory(int accountId) throws SQLException {
        return searchRequestDAO.getSearchHistory(accountId);
    }


    public List<Object[]> getAllSearchRequests() throws SQLException {
        return searchRequestDAO.getAllSearchRequests();
    }


    public List<Object[]> getAuditLogs() throws SQLException {
        return auditLogDAO.getAllLogs();
    }


    public List<Object[]> getAuditSummary() throws SQLException {
        return auditLogDAO.getActionSummary();
    }
}

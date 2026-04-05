package dao;

import model.Account;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {


    public Account authenticate(String email, String password) throws SQLException {
        String sql = "SELECT a.AccountID, a.UserID, a.Pass, a.RoleID, a.TotalFine " +
                     "FROM Account a " +
                     "JOIN Users u ON a.UserID = u.UserID " +
                     "WHERE u.Email = ? AND a.Pass = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();
                    account.setAccountId(rs.getInt("AccountID"));
                    account.setUserId(rs.getInt("UserID"));
                    account.setPassword(rs.getString("Pass"));
                    account.setRoleId(rs.getInt("RoleID"));
                    account.setTotalFine(rs.getDouble("TotalFine"));
                    return account;
                }
            }
        }
        return null;
    }


    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE UserID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUserFromResultSet(rs);
                }
            }
        }
        return null;
    }


    public Account getAccountByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM Account WHERE UserID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAccountFromResultSet(rs);
                }
            }
        }
        return null;
    }


    public String getRoleName(int roleId) throws SQLException {
        String sql = "SELECT RoleName FROM Role WHERE RoleID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("RoleName");
                }
            }
        }
        return "Unknown";
    }


    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }


    public Account createUserWithAccount(User user, String password, int roleId) throws SQLException {
        String insertUser = "INSERT INTO Users (FirstName, LastName, Email, Address, JoinDate, PhoneNo) " +
                            "VALUES (?, ?, ?, ?, CURDATE(), ?)";
        String insertAccount = "INSERT INTO Account (UserID, Pass, RoleID) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getAddress());
                ps.setString(5, user.getPhoneNo());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        userId = keys.getInt(1);
                    } else {
                        conn.rollback();
                        return null;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(insertAccount, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setString(2, password);
                ps.setInt(3, roleId);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        conn.commit();
                        Account account = new Account();
                        account.setAccountId(keys.getInt(1));
                        account.setUserId(userId);
                        account.setPassword(password);
                        account.setRoleId(roleId);
                        account.setTotalFine(0);
                        return account;
                    }
                }
            }
            conn.rollback();
            return null;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }


    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUserFromResultSet(rs);
            }
        }
        return null;
    }


    public int getAccountIdByUserId(int userId) throws SQLException {
        String sql = "SELECT AccountID FROM Account WHERE UserID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("AccountID");
            }
        }
        return -1;
    }

    public boolean accountExists(int accountId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Account WHERE AccountID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }


    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT u.*, a.AccountID FROM Users u JOIN Account a ON u.UserID = a.UserID WHERE a.RoleID = 2";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = mapUserFromResultSet(rs);
                users.add(user);
            }
        }
        return users;
    }


    public List<Object[]> getAllViolationTypes() throws SQLException {
        String sql = "SELECT ViolationTypeID, VName, BaseFine FROM ViolationType";
        List<Object[]> types = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                types.add(new Object[]{
                    rs.getInt("ViolationTypeID"),
                    rs.getString("VName"),
                    rs.getDouble("BaseFine")
                });
            }
        }
        return types;
    }


    public List<Object[]> getAllAuthorities() throws SQLException {
        String sql = "SELECT AuthorityID, Aname, Department FROM Authority";
        List<Object[]> auths = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                auths.add(new Object[]{
                    rs.getInt("AuthorityID"),
                    rs.getString("Aname"),
                    rs.getString("Department")
                });
            }
        }
        return auths;
    }

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserID"));
        user.setFirstName(rs.getString("FirstName"));
        user.setLastName(rs.getString("LastName"));
        user.setEmail(rs.getString("Email"));
        user.setAddress(rs.getString("Address"));
        user.setJoinDate(rs.getDate("JoinDate"));
        user.setPhoneNo(rs.getString("PhoneNo"));
        return user;
    }

    private Account mapAccountFromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getInt("AccountID"));
        account.setUserId(rs.getInt("UserID"));
        account.setPassword(rs.getString("Pass"));
        account.setRoleId(rs.getInt("RoleID"));
        account.setTotalFine(rs.getDouble("TotalFine"));
        return account;
    }
}

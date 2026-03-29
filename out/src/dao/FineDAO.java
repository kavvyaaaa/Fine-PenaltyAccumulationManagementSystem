package dao;

import model.Fine;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {


    public List<Fine> getFinesByAccountId(int accountId) throws SQLException {
        String sql = "SELECT f.FineID, f.ViolationID, f.AuthorityID, f.IssueDate, f.DueDate, " +
                     "f.FineAmount AS BaseFineAmount, " +
                     "IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0) AS PenaltyAmount, " +
                     "(f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS TotalAmount, f.Status, vt.VName, v.Location, auth.Aname, " +
                     "CONCAT(u.FirstName, ' ', u.LastName) AS UserName " +
                     "FROM Fine f " +
                     "LEFT JOIN Penalty p ON f.FineID = p.FineID " +
                     "JOIN Violation v ON f.ViolationID = v.ViolationID " +
                     "JOIN ViolationType vt ON v.ViolationTypeID = vt.ViolationTypeID " +
                     "JOIN Authority auth ON f.AuthorityID = auth.AuthorityID " +
                     "JOIN Account a ON v.AccountID = a.AccountID " +
                     "JOIN Users u ON a.UserID = u.UserID " +
                     "WHERE a.AccountID = ?";

        List<Fine> fines = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Fine fine = mapFineFromResultSet(rs);
                    fines.add(fine);
                }
            }
        }
        return fines;
    }


    public List<Fine> getPendingFinesByAccountId(int accountId) throws SQLException {
        String sql = "SELECT u.FirstName, u.LastName, f.FineID, " +
                     "f.FineAmount AS BaseFineAmount, " +
                     "IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0) AS PenaltyAmount, " +
                     "(f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS TotalAmount, a.AccountID " +
                     "FROM Users u " +
                     "JOIN Account a ON u.UserID = a.UserID " +
                     "JOIN Violation v ON a.AccountID = v.AccountID " +
                     "JOIN Fine f ON v.ViolationID = f.ViolationID " +
                     "LEFT JOIN Penalty p ON f.FineID = p.FineID " +
                     "WHERE f.Status = 'Unpaid' AND a.AccountID = ?";
        List<Fine> fines = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Fine fine = new Fine();
                    fine.setFineId(rs.getInt("FineID"));
                    fine.setFineAmount(rs.getDouble("BaseFineAmount"));
                    fine.setPenaltyAmount(rs.getDouble("PenaltyAmount"));
                    fine.setTotalAmount(rs.getDouble("TotalAmount"));
                    fine.setUserName(rs.getString("FirstName") + " " + rs.getString("LastName"));
                    fine.setStatus("Unpaid");
                    fines.add(fine);
                }
            }
        }
        return fines;
    }


    public List<Fine> getAllFines() throws SQLException {
        String sql = "SELECT f.FineID, f.ViolationID, f.AuthorityID, f.IssueDate, f.DueDate, " +
                     "f.FineAmount AS BaseFineAmount, " +
                     "IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0) AS PenaltyAmount, " +
                     "(f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS TotalAmount, f.Status, vt.VName, v.Location, auth.Aname, " +
                     "CONCAT(u.FirstName, ' ', u.LastName) AS UserName, a.AccountID " +
                     "FROM Fine f " +
                     "LEFT JOIN Penalty p ON f.FineID = p.FineID " +
                     "JOIN Violation v ON f.ViolationID = v.ViolationID " +
                     "JOIN ViolationType vt ON v.ViolationTypeID = vt.ViolationTypeID " +
                     "JOIN Authority auth ON f.AuthorityID = auth.AuthorityID " +
                     "JOIN Account a ON v.AccountID = a.AccountID " +
                     "JOIN Users u ON a.UserID = u.UserID " +
                     "ORDER BY f.FineID";

        List<Fine> fines = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Fine fine = mapFineFromResultSet(rs);
                    fine.setAccountId(rs.getInt("AccountID"));
                    fines.add(fine);
                }
            }
        }
        return fines;
    }


    public double getUserTotalFine(int accountId) throws SQLException {
        String sql = "{CALL GetUserTotalFine(?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, accountId);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TotalFine");
                }
            }
        }
        return 0.0;
    }


    public List<Object[]> getTotalFinePerUser() throws SQLException {
        String sql = "SELECT * FROM TotalFinePerUser";
        List<Object[]> results = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("UserID"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getDouble("Total")
                };
                results.add(row);
            }
        }
        return results;
    }


    public double[] getAdminReport() throws SQLException {
        String sql = "SELECT " +
                     "COUNT(*) AS TotalFines, " +
                     "SUM(f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS TotalRevenue, " +
                     "SUM(CASE WHEN f.Status='Unpaid' THEN f.FineAmount + IFNULL(IF(CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0) ELSE 0 END) AS PendingAmount " +
                     "FROM Fine f " +
                     "LEFT JOIN Penalty p ON f.FineID = p.FineID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new double[]{
                    rs.getDouble("TotalFines"),
                    rs.getDouble("TotalRevenue"),
                    rs.getDouble("PendingAmount")
                };
            }
        }
        return new double[]{0, 0, 0};
    }


    public void updateFineStatus(int fineId, String status) throws SQLException {
        String sql = "UPDATE Fine SET Status = ? WHERE FineID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, fineId);
            ps.executeUpdate();
        }
    }


    public int createFine(int violationId, int authorityId, java.util.Date issueDate,
                          java.util.Date dueDate, double fineAmount) throws SQLException {
        String sql = "INSERT INTO Fine (ViolationID, AuthorityID, IssueDate, DueDate, FineAmount, Status) " +
                     "VALUES (?, ?, ?, ?, ?, 'Unpaid')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, violationId);
            ps.setInt(2, authorityId);
            ps.setDate(3, new java.sql.Date(issueDate.getTime()));
            ps.setDate(4, new java.sql.Date(dueDate.getTime()));
            ps.setDouble(5, fineAmount);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }


    public void createPenalty(int fineId, double penaltyPerDay) throws SQLException {
        String sql = "INSERT INTO Penalty (FineID, PenaltyPerDay) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fineId);
            ps.setDouble(2, penaltyPerDay);
            ps.executeUpdate();
        }
    }


    public List<Fine> getFinesByAuthorityId(int authorityId) throws SQLException {
        String sql = "SELECT f.FineID, f.ViolationID, f.AuthorityID, f.IssueDate, f.DueDate, " +
                     "f.FineAmount AS BaseFineAmount, " +
                     "IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0) AS PenaltyAmount, " +
                     "(f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS TotalAmount, f.Status, vt.VName, v.Location, auth.Aname, " +
                     "CONCAT(u.FirstName, ' ', u.LastName) AS UserName, a.AccountID " +
                     "FROM Fine f " +
                     "LEFT JOIN Penalty p ON f.FineID = p.FineID " +
                     "JOIN Violation v ON f.ViolationID = v.ViolationID " +
                     "JOIN ViolationType vt ON v.ViolationTypeID = vt.ViolationTypeID " +
                     "JOIN Authority auth ON f.AuthorityID = auth.AuthorityID " +
                     "JOIN Account a ON v.AccountID = a.AccountID " +
                     "JOIN Users u ON a.UserID = u.UserID " +
                     "WHERE f.AuthorityID = ? ORDER BY f.FineID DESC";

        List<Fine> fines = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Fine fine = mapFineFromResultSet(rs);
                    fine.setAccountId(rs.getInt("AccountID"));
                    fines.add(fine);
                }
            }
        }
        return fines;
    }


    private Fine mapFineFromResultSet(ResultSet rs) throws SQLException {
        Fine fine = new Fine();
        fine.setFineId(rs.getInt("FineID"));
        fine.setViolationId(rs.getInt("ViolationID"));
        fine.setAuthorityId(rs.getInt("AuthorityID"));
        fine.setIssueDate(rs.getDate("IssueDate"));
        fine.setDueDate(rs.getDate("DueDate"));
        fine.setFineAmount(rs.getDouble("BaseFineAmount"));
        fine.setPenaltyAmount(rs.getDouble("PenaltyAmount"));
        fine.setTotalAmount(rs.getDouble("TotalAmount"));
        fine.setStatus(rs.getString("Status"));
        fine.setViolationName(rs.getString("VName"));
        fine.setLocation(rs.getString("Location"));
        fine.setAuthorityName(rs.getString("Aname"));
        fine.setUserName(rs.getString("UserName"));
        return fine;
    }
}

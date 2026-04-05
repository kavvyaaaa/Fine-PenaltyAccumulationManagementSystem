package dao;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchRequestDAO {


    public void insertSearchRequest(int accountId, String searchText) throws SQLException {
        String sql = "INSERT INTO SearchRequest (AccountID, SearchText, SearchDate) VALUES (?, ?, CURDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, searchText);
            ps.executeUpdate();
        }
    }


    public List<Object[]> getSearchHistory(int accountId) throws SQLException {
        String sql = "SELECT RequestID, SearchText, SearchDate FROM SearchRequest WHERE AccountID = ? ORDER BY RequestID DESC";
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{
                        rs.getInt("RequestID"),
                        rs.getString("SearchText"),
                        rs.getDate("SearchDate")
                    });
                }
            }
        }
        return results;
    }


    public List<Object[]> getAllSearchRequests() throws SQLException {
        String sql = "SELECT sr.RequestID, CONCAT(u.FirstName,' ',u.LastName) AS UserName, " +
                     "sr.SearchText, sr.SearchDate " +
                     "FROM SearchRequest sr " +
                     "JOIN Account a ON sr.AccountID = a.AccountID " +
                     "JOIN Users u ON a.UserID = u.UserID " +
                     "ORDER BY sr.RequestID DESC";
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{
                    rs.getInt("RequestID"),
                    rs.getString("UserName"),
                    rs.getString("SearchText"),
                    rs.getDate("SearchDate")
                });
            }
        }
        return results;
    }


    public List<Object[]> searchFines(String keyword) throws SQLException {
        String sql = "SELECT f.FineID, CONCAT(u.FirstName,' ',u.LastName) AS UserName, " +
                     "vt.VName, v.Location, " +
                     "f.FineAmount AS BaseFineAmount, " +
                     "IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0) AS PenaltyAmount, " +
                     "(f.FineAmount + IFNULL(IF(f.Status = 'Unpaid' AND CURDATE() > f.DueDate, DATEDIFF(CURDATE(), f.DueDate) * p.PenaltyPerDay, 0), 0)) AS TotalAmount, f.Status " +
                     "FROM Fine f " +
                     "LEFT JOIN Penalty p ON f.FineID = p.FineID " +
                     "JOIN Violation v ON f.ViolationID = v.ViolationID " +
                     "JOIN ViolationType vt ON v.ViolationTypeID = vt.ViolationTypeID " +
                     "JOIN Account a ON v.AccountID = a.AccountID " +
                     "JOIN Users u ON a.UserID = u.UserID " +
                     "WHERE vt.VName LIKE ? OR v.Location LIKE ? OR " +
                     "CONCAT(u.FirstName,' ',u.LastName) LIKE ? " +
                     "ORDER BY f.FineID DESC";
        List<Object[]> results = new ArrayList<>();
        String like = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{
                        rs.getInt("FineID"),
                        rs.getString("UserName"),
                        rs.getString("VName"),
                        rs.getString("Location"),
                        rs.getDouble("BaseFineAmount"),
                        rs.getDouble("PenaltyAmount"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("Status")
                    });
                }
            }
        }
        return results;
    }
}

package dao;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {


    public List<Object[]> getAllLogs() throws SQLException {
        String sql = "SELECT LogID, Action, TableName, ActionTime FROM AuditLog ORDER BY LogID DESC";
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{
                    rs.getInt("LogID"),
                    rs.getString("Action"),
                    rs.getString("TableName"),
                    rs.getTimestamp("ActionTime")
                });
            }
        }
        return results;
    }


    public List<Object[]> getLogsByTable(String tableName) throws SQLException {
        String sql = "SELECT LogID, Action, TableName, ActionTime FROM AuditLog WHERE TableName = ? ORDER BY ActionTime DESC";
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{
                        rs.getInt("LogID"),
                        rs.getString("Action"),
                        rs.getString("TableName"),
                        rs.getTimestamp("ActionTime")
                    });
                }
            }
        }
        return results;
    }


    public List<Object[]> getActionSummary() throws SQLException {
        String sql = "SELECT Action, COUNT(*) AS ActionCount FROM AuditLog GROUP BY Action ORDER BY ActionCount DESC";
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{
                    rs.getString("Action"),
                    rs.getInt("ActionCount")
                });
            }
        }
        return results;
    }
}

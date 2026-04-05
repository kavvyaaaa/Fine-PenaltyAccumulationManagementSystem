package dao;

import model.Violation;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViolationDAO {


    public List<Violation> getViolationsByAccountId(int accountId) throws SQLException {
        String sql = "SELECT v.*, vt.VName, vt.BaseFine " +
                     "FROM Violation v " +
                     "JOIN ViolationType vt ON v.ViolationTypeID = vt.ViolationTypeID " +
                     "WHERE v.AccountID = ?";

        List<Violation> violations = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Violation v = new Violation();
                    v.setViolationId(rs.getInt("ViolationID"));
                    v.setAccountId(rs.getInt("AccountID"));
                    v.setViolationTypeId(rs.getInt("ViolationTypeID"));
                    v.setVDate(rs.getDate("VDate"));
                    v.setLocation(rs.getString("Location"));
                    v.setViolationName(rs.getString("VName"));
                    v.setBaseFine(rs.getDouble("BaseFine"));
                    violations.add(v);
                }
            }
        }
        return violations;
    }


    public int createViolation(int accountId, int violationTypeId, java.util.Date vDate, String location)
            throws SQLException {
        String sql = "INSERT INTO Violation (AccountID, ViolationTypeID, VDate, Location) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, accountId);
            ps.setInt(2, violationTypeId);
            ps.setDate(3, new java.sql.Date(vDate.getTime()));
            ps.setString(4, location);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }
}

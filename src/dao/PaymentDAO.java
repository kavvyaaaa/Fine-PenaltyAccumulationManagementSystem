package dao;

import model.Payment;
import util.DBConnection;

import java.sql.*;

public class PaymentDAO {


    public boolean insertPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO Payment (FineID, PaymentDate, PaymentMode, PaymentStatus, AmountPaid) " +
                     "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, payment.getFineId());
                ps.setDate(2, new java.sql.Date(payment.getPaymentDate().getTime()));
                ps.setString(3, payment.getPaymentMode());
                ps.setString(4, payment.getPaymentStatus());
                ps.setDouble(5, payment.getAmountPaid());

                int rowsAffected = ps.executeUpdate();

                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
}

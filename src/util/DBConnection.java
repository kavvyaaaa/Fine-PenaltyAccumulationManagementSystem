package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/<YOUR_DATABASE_NAME>"; // Usually PFAMS
    private static final String USER = "<YOUR_MYSQL_USERNAME>"; // Usually root
    private static final String PASSWORD = "<YOUR_MYSQL_PASSWORD>"; // Change this to your MySQL password

    private static Connection connection;

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Ensure mysql-connector-j is in classpath.", e);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

import java.sql.*;

public class ConexionDatabase {
    private static final String URL = "jdbc:postgresql://localhost:5432/imperium";
    private static final String USER = "postgres";
    private static final String PASSWORD = "imperio12";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        conn.setAutoCommit(false);  // Desactivar auto-commit
        return conn;
    }
}
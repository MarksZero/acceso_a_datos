import java.sql.*;
import java.time.LocalDate;

public class GestorCupo {
    public static boolean existeRazonCupo(Connection conexion, int razon) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cupos WHERE id_cupo = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, razon);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public static int asignarCupo(Connection conexion, int idUsuario, int cantidad, String razon) throws SQLException {
        Savepoint savepoint = null;
        try {
            savepoint = conexion.setSavepoint();

            String sql = "INSERT INTO cupos (id_usuario, cantidad, razon, fecha_obtencion) VALUES (?, ?, ?, ?) RETURNING id_cupo";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, idUsuario);
                pstmt.setInt(2, cantidad);
                pstmt.setString(3, razon);
                pstmt.setDate(4, Date.valueOf(LocalDate.now()));
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    conexion.commit();
                    return id;
                }
            }
            throw new SQLException("No se pudo asignar el cupo");
        } catch (SQLException e) {
            if (savepoint != null) {
                conexion.rollback(savepoint);
            }
            throw e;
        }
    }

    public static void mostrarCupos(Connection conexion, int idUsuario) throws SQLException {
        String sql = "SELECT * FROM cupos WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("|--------Cupos--------|");
            while (rs.next()) {
                System.out.println("Cupo ID: " + rs.getInt("id_cupo") + ", Cantidad: " + rs.getInt("cantidad") + ", Razón: " + rs.getString("razon") + ", Fecha: " + rs.getDate("fecha_obtencion"));
            }
            System.out.println("|---------------------|");
        }
    }

    public static void mostrarRazonesCupo(Connection conexion, int idUsuario) throws SQLException {
        String sql = "SELECT id_cupo, razon, cantidad FROM cupos WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("|--------Razones de cupo--------|");
            while (rs.next()) {
                System.out.println("Id: " + rs.getInt("id_cupo") + " Razón: " + rs.getString("razon") + ", Monto: " + rs.getInt("cantidad"));
            }
            System.out.println("|------------------------------|");
        }
    }
}
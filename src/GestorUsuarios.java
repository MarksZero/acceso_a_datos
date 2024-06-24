import java.sql.*;

public class GestorUsuarios {
    public static int crearUsuario(Connection conexion, String nombre, String apodo) throws SQLException {
        Savepoint savepoint = null;
        try {
            savepoint = conexion.setSavepoint();

            String sql = "INSERT INTO usuarios (nombre, apodo) VALUES (?, ?) RETURNING id_usuario";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, apodo);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    conexion.commit();
                    return id;
                }
            }
            throw new SQLException("No se pudo crear el usuario");
        } catch (SQLException e) {
            if (savepoint != null) {
                conexion.rollback(savepoint);
            }
            throw e;
        }
    }

    public static void mostrarTodosLosUsuarios(Connection conexionnn) throws SQLException {
        String sql = "SELECT * FROM usuarios";
        try (PreparedStatement pstmt = conexionnn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            System.out.println("|--------Usuarios--------|");
            while (rs.next()) {
                System.out.println("Usuario ID: " + rs.getInt("id_usuario") + ", Nombre: " + rs.getString("nombre") + ", Apodo: " + rs.getString("apodo"));
            }
            System.out.println("|------------------------|");
        }
    }

    public static void eliminarUsuario(Connection conexion, int idUsuario) throws SQLException {
        Savepoint savepoint = null;
        try {
            savepoint = conexion.setSavepoint();

            String sqlCupos = "DELETE FROM cupos WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlCupos)) {
                pstmt.setInt(1, idUsuario);
                pstmt.executeUpdate();
            }

            String sqlUsuario = "DELETE FROM usuarios WHERE id_usuario = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlUsuario)) {
                pstmt.setInt(1, idUsuario);
                pstmt.executeUpdate();
            }

            conexion.commit();
            System.out.println("Usuario y sus cupos eliminados correctamente.");
        } catch (SQLException e) {
            if (savepoint != null) {
                conexion.rollback(savepoint);
            }
            throw e;
        }
    }
}
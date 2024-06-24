import java.sql.*;

public class GestorInventario {
    public static int crearProducto(Connection conexion, String producto, int stock, double precio) throws SQLException {
        Savepoint savepoint = null;
        try {
            savepoint = conexion.setSavepoint();

            String sql = "INSERT INTO inventario (producto, stock, precio) VALUES (?, ?, ?) RETURNING id_inventario";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setString(1, producto);
                pstmt.setInt(2, stock);
                pstmt.setDouble(3, precio);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    conexion.commit();
                    return id;
                }
            }
            throw new SQLException("No se pudo crear el producto");
        } catch (SQLException e) {
            if (savepoint != null) {
                conexion.rollback(savepoint);
            }
            throw e;
        }
    }

    public static void mostrarProductos(Connection conexion) throws SQLException {
        String sql = "SELECT * FROM inventario";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            System.out.println("|--------Productos--------|");
            while (rs.next()) {
                System.out.println("Producto ID: " + rs.getInt("id_inventario") + ", Nombre: " + rs.getString("producto") + ", Stock: " + rs.getInt("stock") + ", Precio: " + rs.getDouble("precio"));
            }
            System.out.println("|-------------------------|");
        }
    }

    public static void eliminarProducto(Connection conexion, int idProducto) throws SQLException {
        Savepoint savepoint = null;
        try {
            savepoint = conexion.setSavepoint();

            String sql = "DELETE FROM inventario WHERE id_inventario = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, idProducto);
                pstmt.executeUpdate();
            }
            conexion.commit();
            System.out.println("Producto eliminado correctamente.");
        } catch (SQLException e) {
            if (savepoint != null) {
                conexion.rollback(savepoint);
            }
            throw e;
        }
    }
}
import java.sql.*;

public class GestorTransacciones {
    public static void gastarCupoEnProductos(Connection conexion, int idUsuario, int idProducto, int cantidad) throws SQLException {
        Savepoint savePoint = null;
        try {
            savePoint = conexion.setSavepoint(); // Crear un punto de guardado

            String sqlProducto = "SELECT precio, stock FROM inventario WHERE id_inventario = ? FOR UPDATE";
            double precioProducto;
            int stockActual;
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlProducto)) {
                pstmt.setInt(1, idProducto);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    precioProducto = rs.getDouble("precio");
                    stockActual = rs.getInt("stock");
                    if (stockActual < cantidad) {
                        throw new SQLException("No hay suficiente stock del producto.");
                    }
                } else {
                    throw new SQLException("El producto no existe.");
                }
            }

            double costoTotal = precioProducto * cantidad;

            String sqlCupos = "SELECT id_cupo, cantidad FROM cupos WHERE id_usuario = ? FOR UPDATE";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlCupos)) {
                pstmt.setInt(1, idUsuario);
                ResultSet rs = pstmt.executeQuery();
                boolean cupoSuficiente = false;
                while (rs.next()) {
                    int idCupo = rs.getInt("id_cupo");
                    int cupo = rs.getInt("cantidad");

                    if (cupo >= costoTotal) {
                        String sqlGastar = "UPDATE cupos SET cantidad = cantidad - ? WHERE id_cupo = ?";
                        try (PreparedStatement pstmtGastar = conexion.prepareStatement(sqlGastar)) {
                            pstmtGastar.setDouble(1, costoTotal);
                            pstmtGastar.setInt(2, idCupo);
                            pstmtGastar.executeUpdate();
                        }

                        String sqlStock = "UPDATE inventario SET stock = stock - ? WHERE id_inventario = ?";
                        try (PreparedStatement pstmtStock = conexion.prepareStatement(sqlStock)) {
                            pstmtStock.setInt(1, cantidad);
                            pstmtStock.setInt(2, idProducto);
                            pstmtStock.executeUpdate();
                        }

                        String sqlTransaccion = "INSERT INTO transacciones (id_cupo, cantidad) VALUES (?, ?)";
                        try (PreparedStatement pstmtTransaccion = conexion.prepareStatement(sqlTransaccion)) {
                            pstmtTransaccion.setInt(1, idCupo);
                            pstmtTransaccion.setDouble(2, costoTotal);
                            pstmtTransaccion.executeUpdate();
                        }

                        cupoSuficiente = true;
                        break;
                    }
                }
                if (!cupoSuficiente) {
                    throw new SQLException("No tienes suficiente cupo para realizar esta compra.");
                }
            }

            conexion.commit();
            System.out.println("Compra realizada con éxito. Cupo gastado: " + costoTotal);

        } catch (SQLException e) {
            if (savePoint != null) {
                conexion.rollback(savePoint);
            }
            System.out.println("Error en la transacción: " + e.getMessage());
            throw e;
        }
    }
}
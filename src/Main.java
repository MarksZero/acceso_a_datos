import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    // JDBC URL, username and password of PostgreSQL server
    private static final String URL = "jdbc:postgresql://localhost:5432/imperium";

    private static final String USER = "postgres";
    private static final String PASSWORD = "imperio12";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Conexión a la base de datos establecida.");

            Scanner scanner = new Scanner(System.in);
            boolean continuar = true;

            while (continuar) {
                System.out.println("|---------Seleccione una opción---------|");
                System.out.println("[1] Gastar cupo");
                System.out.println("[2] Gestion cupo");
                System.out.println("[3] Gestion inventario");
                System.out.println("[4] Salir");

                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:  //Gastar cupo

                        System.out.println("Ingrese el ID del usuario: ");
                        int idUsuario = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Razones de cupo y montos para el usuario:");
                        mostrarRazonesCupo(conn, idUsuario);
                        int motivo;
                        boolean razonValida;
                        do {
                            System.out.println("Ingrese la razón del cupo a gastar:");
                            motivo = scanner.nextInt();
                            razonValida = existeRazonCupo(conn, motivo);
                            if (razonValida) {
                                System.out.println("Mostrando productos:");
                                mostrarProductos(conn);
                                System.out.println("Ingrese el ID del producto a comprar:");
                                int idProducto = scanner.nextInt();
                                scanner.nextLine();
                                System.out.println("Ingrese la cantidad que desea comprar:");
                                int cantidad = scanner.nextInt();
                                scanner.nextLine();
                                gastarCupoEnProductos(conn, idUsuario, idProducto, cantidad);
                                //System.out.println("Cupo gastado. Mostrando cupos actualizados:");
                                mostrarCupos(conn, idUsuario);
                            } else {
                                System.out.println("La razón del cupo no existe en la base de datos. Por favor, intente de nuevo.");
                            }
                        } while (!razonValida);
                        break;

                    case 2:
                        //Gestion cupo
                        System.out.println("|---------Gestion cupo---------|");
                        System.out.println("[1] Crear usuario");
                        System.out.println("[2] Asignar cupo");
                        System.out.println("[3] Mostrar usuario");
                        System.out.println("[4] Eliminar usuario");

                        int opcionGestion = scanner.nextInt();
                        scanner.nextLine();

                        if (opcionGestion == 1) {//Crear usuario
                            System.out.println("Ingrese el nombre del usuario:");
                            String nombre = scanner.nextLine();

                            System.out.println("Ingrese el apodo del usuario:");
                            String apodo = scanner.nextLine();
                            idUsuario = crearUsuario(conn, nombre, apodo);
                            System.out.println("Usuario creado con ID: " + idUsuario);
                            continue;
                        } else if (opcionGestion == 2) {//Asignar cupo
                            mostrarTodosLosUsuarios(conn);
                            System.out.println("Ingrese el ID del usuario:");
                            idUsuario = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("Ingrese la cantidad del cupo:");
                            int cantidad = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("Ingrese la razón del cupo:");
                            String razon = scanner.nextLine();
                            int idCupo = asignarCupo(conn, idUsuario, cantidad, razon);
                            System.out.println("Cupo asignado con ID: " + idCupo);
                            continue;
                        } else if (opcionGestion == 3) {
                            //Mostrar usuario
                            mostrarTodosLosUsuarios(conn);
                            continue;


                        } else if (opcionGestion == 4) {
                            //Eliminar usuario
                            mostrarTodosLosUsuarios(conn);

                            System.out.println("Ingrese el ID del usuario:");
                            idUsuario = scanner.nextInt();
                            scanner.nextLine();
                            eliminarUsuario(conn, idUsuario);
                            continue;
                        } else {
                            System.out.println("Opción no válida. Intente de nuevo.");
                        }

                    case 3:
                        //gestion invetario
                        System.out.println("|---------Gestion inventario---------|");
                        System.out.println("[1] Crear producto");
                        System.out.println("[2] Mostrar productos");
                        System.out.println("[3] Eliminar producto");

                        int opcionInventario = scanner.nextInt();
                        scanner.nextLine();

                        if (opcionInventario == 1) {
                            //Crear producto
                            System.out.println("Ingrese el nombre del producto:");
                            String producto = scanner.nextLine();

                            System.out.println("Ingrese el stock del producto:");
                            int stock = scanner.nextInt();
                            scanner.nextLine();

                            System.out.println("Ingrese el precio del producto:");
                            int precio = scanner.nextInt();
                            scanner.nextLine();

                            int idProducto = crearProducto(conn, producto, stock, precio);
                            System.out.println("Producto creado con ID: " + idProducto);
                            continue;
                        } else if (opcionInventario == 2) {
                            //Mostrar productos
                            mostrarProductos(conn);
                            continue;
                        } else if (opcionInventario == 3) {
                            //Eliminar producto
                            mostrarProductos(conn);
                            System.out.println("Ingrese el ID del producto:");
                            int idProducto = scanner.nextInt();
                            scanner.nextLine();
                            eliminarProducto(conn, idProducto);
                            continue;
                        } else {
                            System.out.println("Opción no válida. Intente de nuevo.");
                        }


                        break;

                    case 4:
                        //Salir
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                        break;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en la conexión a la base de datos:");
            e.printStackTrace();
        }
    }

    //----------------------------------Gestion de cupo-----------------------------------------------------------------
    private static boolean existeRazonCupo(Connection conn, int razon) throws SQLException { // Método para verificar si una razón de cupo existe en la base de datos
        //String sql = "SELECT COUNT(*) FROM cupos WHERE razon = ?";
        String sql = "SELECT COUNT(*) FROM cupos WHERE id_cupo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, razon);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private static int crearUsuario(Connection conn, String nombre, String apodo) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, apodo) VALUES (?, ?) RETURNING id_usuario";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, apodo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private static int asignarCupo(Connection conn, int idUsuario, int cantidad, String razon) throws SQLException {
        String sql = "INSERT INTO cupos (id_usuario, cantidad, razon, fecha_obtencion) VALUES (?, ?, ?, ?) RETURNING id_cupo";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.setInt(2, cantidad);
            pstmt.setString(3, razon);
            pstmt.setDate(4, Date.valueOf(LocalDate.now()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private static void mostrarCupos(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT * FROM cupos WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("|--------Cupos--------|");
            while (rs.next()) {
                System.out.println("Cupo ID: " + rs.getInt("id_cupo") + ", Cantidad: " + rs.getInt("cantidad") + ", Razón: " + rs.getString("razon") + ", Fecha: " + rs.getDate("fecha_obtencion"));
            }
            System.out.println("|---------------------|");
        }
    }

    private static void mostrarRazonesCupo(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT id_cupo, razon, cantidad FROM cupos WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("|--------Razones de cupo--------|");
            while (rs.next()) {
                System.out.println("Id: " + rs.getInt("id_cupo") + " Razón: " + rs.getString("razon") + ", Monto: " + rs.getInt("cantidad"));
            }
            System.out.println("|------------------------------|");
        }
    }

    private static void mostrarTodosLosUsuarios(Connection conn) throws SQLException {
        String sql = "SELECT * FROM usuarios";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            System.out.println("|--------Usuarios--------|");

            while (rs.next()) {
                System.out.println("Usuario ID: " + rs.getInt("id_usuario") + ", Nombre: " + rs.getString("nombre") + ", Apodo: " + rs.getString("apodo"));
            }
            System.out.println("|------------------------|");
        }
    }

    private static void eliminarUsuario(Connection conn, int idUsuario) throws SQLException {
        // Primero, eliminar los cupos del usuario
        String sqlCupos = "DELETE FROM cupos WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlCupos)) {
            pstmt.setInt(1, idUsuario);
            pstmt.executeUpdate();
        }

        // Luego, eliminar al usuario
        String sqlUsuario = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlUsuario)) {
            pstmt.setInt(1, idUsuario);
            pstmt.executeUpdate();
        }

        System.out.println("Usuario y sus cupos eliminados correctamente.");
    }

    //---------------------------------Gestion para invetario-----------------------------------------------------------
    private static int crearProducto(Connection conn, String producto, int stock, int precio) throws SQLException {
        String sql = "INSERT INTO inventario (producto, stock, precio) VALUES (?, ?, ?) RETURNING id_inventario";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto);
            pstmt.setInt(2, stock);
            pstmt.setInt(3, precio);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private static void mostrarProductos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM inventario";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            System.out.println("|--------Productos--------|");
            while (rs.next()) {
                System.out.println("Producto ID: " + rs.getInt("id_inventario") + ", Nombre: " + rs.getString("producto") + ", Stock: " + rs.getInt("stock") + ", Precio: " + rs.getInt("precio"));
            }
            System.out.println("|-------------------------|");
        }
    }

    private static void eliminarProducto(Connection conn, int idProducto) throws SQLException {
        String sql = "DELETE FROM inventario WHERE id_inventario = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            pstmt.executeUpdate();
        }
        System.out.println("Producto eliminado correctamente.");
    }

    //---------------------------------Gastar cupo en productos---------------------------------------------------------

    private static void gastarCupoEnProductos(Connection conn, int idUsuario, int idProducto, int cantidad) throws SQLException {
        // Obtener el precio del producto
        String sqlProducto = "SELECT precio FROM inventario WHERE id_inventario = ?";
        int precioProducto;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlProducto)) {
            pstmt.setInt(1, idProducto);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                precioProducto = rs.getInt("precio");
            } else {
                System.out.println("El producto no existe.");
                return;
            }
        }

        // Calcular el costo total
        int costoTotal = precioProducto * cantidad;

        // Obtener los cupos del usuario
        String sqlCupos = "SELECT id_cupo, cantidad FROM cupos WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlCupos)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int idCupo = rs.getInt("id_cupo");
                int cupo = rs.getInt("cantidad");

                // Verificar si el cupo es suficiente
                if (cupo > costoTotal) {
                    // Restar el costo total del cupo
                    String sqlGastar = "UPDATE cupos SET cantidad = cantidad - ? WHERE id_cupo = ?";
                    try (PreparedStatement pstmtGastar = conn.prepareStatement(sqlGastar)) {
                        pstmtGastar.setInt(1, costoTotal);
                        pstmtGastar.setInt(2, idCupo);
                        pstmtGastar.executeUpdate();
                    }

                    // Actualizar el stock del producto
                    String sqlStock = "UPDATE inventario SET stock = stock - ? WHERE id_inventario = ?";
                    try (PreparedStatement pstmtStock = conn.prepareStatement(sqlStock)) {
                        pstmtStock.setInt(1, cantidad);
                        pstmtStock.setInt(2, idProducto);
                        pstmtStock.executeUpdate();
                    }

                    System.out.println("Compra realizada con éxito. Cupo gastado: " + costoTotal);
                    return;
                }
            }
        }

        System.out.println("No tienes suficiente cupo para realizar esta compra.");
    }
}
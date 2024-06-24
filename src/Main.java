import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Connection conexion = ConexionDatabase.getConnection()) {
            System.out.println("Conexión a la base de datos establecida.");

            Scanner scanner = new Scanner(System.in);
            boolean continuar = true;

            while (continuar) {
                try {
                    System.out.println("|---------Seleccione una opción---------|");
                    System.out.println("[1] Gastar cupo");
                    System.out.println("[2] Gestion cupo");
                    System.out.println("[3] Gestion inventario");
                    System.out.println("[4] Salir");

                    int opcion = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcion) {
                        case 1:
                            gestionGastarCupo(conexion, scanner);
                            break;
                        case 2:
                            gestionCupo(conexion, scanner);
                            break;
                        case 3:
                            gestionInventario(conexion, scanner);
                            break;
                        case 4:
                            continuar = false;
                            break;
                        default:
                            System.out.println("Opción no válida. Intente de nuevo.");
                            break;
                    }

                    conexion.commit();
                } catch (SQLException e) {
                    System.out.println("Error en la operación: " + e.getMessage());
                    conexion.rollback();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en la conexión a la base de datos:");
            e.printStackTrace();
        }
    }

    private static void gestionGastarCupo(Connection conexion, Scanner scanner) throws SQLException {
        System.out.println("Ingrese el ID del usuario: ");
        int idUsuario = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Razones de cupo y montos para el usuario:");
        GestorCupo.mostrarRazonesCupo(conexion, idUsuario);

        GestorInventario.mostrarProductos(conexion);

        System.out.println("Ingrese el ID del producto a comprar:");
        int idProducto = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Ingrese la cantidad que desea comprar:");
        int cantidad = scanner.nextInt();
        scanner.nextLine();

        GestorTransacciones.gastarCupoEnProductos(conexion, idUsuario, idProducto, cantidad);
        GestorCupo.mostrarCupos(conexion, idUsuario);
    }

    private static void gestionCupo(Connection conexion, Scanner scanner) throws SQLException {
        System.out.println("|---------Gestion cupo---------|");
        System.out.println("[1] Crear usuario");
        System.out.println("[2] Asignar cupo");
        System.out.println("[3] Mostrar usuario");
        System.out.println("[4] Eliminar usuario");

        int opcionGestion = scanner.nextInt();
        scanner.nextLine();

        switch (opcionGestion) {
            case 1:
                System.out.println("Ingrese el nombre del usuario:");
                String nombre = scanner.nextLine();
                System.out.println("Ingrese el apodo del usuario:");
                String apodo = scanner.nextLine();
                int idUsuario =GestorUsuarios.crearUsuario(conexion, nombre, apodo);
                System.out.println("Usuario creado con ID: " + idUsuario);
                break;
            case 2:
                GestorUsuarios.mostrarTodosLosUsuarios(conexion);
                System.out.println("Ingrese el ID del usuario:");
                idUsuario = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Ingrese la cantidad del cupo:");
                int cantidad = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Ingrese la razón del cupo:");
                String razon = scanner.nextLine();
                int idCupo = GestorCupo.asignarCupo(conexion, idUsuario, cantidad, razon);
                System.out.println("Cupo asignado con ID: " + idCupo);
                break;
            case 3:
                GestorUsuarios.mostrarTodosLosUsuarios(conexion);
                break;
            case 4:
                GestorUsuarios.mostrarTodosLosUsuarios(conexion);
                System.out.println("Ingrese el ID del usuario a eliminar:");
                idUsuario = scanner.nextInt();
                scanner.nextLine();
                GestorUsuarios.eliminarUsuario(conexion, idUsuario);
                break;
            default:
                System.out.println("Opción no válida. Intente de nuevo.");
                break;
        }
    }

    private static void gestionInventario(Connection conexion, Scanner scanner) throws SQLException {
        System.out.println("|---------Gestion inventario---------|");
        System.out.println("[1] Crear producto");
        System.out.println("[2] Mostrar productos");
        System.out.println("[3] Eliminar producto");

        int opcionInventario = scanner.nextInt();
        scanner.nextLine();

        switch (opcionInventario) {
            case 1:
                System.out.println("Ingrese el nombre del producto:");
                String producto = scanner.nextLine();
                System.out.println("Ingrese el stock del producto:");
                int stock = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Ingrese el precio del producto:");
                double precio = scanner.nextDouble();
                scanner.nextLine();
                int idProducto = GestorInventario.crearProducto(conexion, producto, stock, precio);
                System.out.println("Producto creado con ID: " + idProducto);
                break;
            case 2:
                GestorInventario.mostrarProductos(conexion);
                break;
            case 3:
                GestorInventario.mostrarProductos(conexion);
                System.out.println("Ingrese el ID del producto a eliminar:");
                idProducto = scanner.nextInt();
                scanner.nextLine();
                GestorInventario.eliminarProducto(conexion, idProducto);
                break;
            default:
                System.out.println("Opción no válida. Intente de nuevo.");
                break;
        }
    }
}
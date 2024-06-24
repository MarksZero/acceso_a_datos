# Manual de Uso

Este proyecto es una aplicación de consola simple que interactúa con una base de datos PostgreSQL. A continuación, se detallan los pasos para utilizar la aplicación.

## Ejecución de la aplicación

1. Ejecute la clase `Main.java`. Esto iniciará la aplicación.
2. Verá un menú con varias opciones. Puede seleccionar una opción ingresando el número correspondiente.

Las opciones del menú son las siguientes:

- **Gastar cupo**: Esta opción le permite gastar el cupo de un usuario en un producto. Se le pedirá que ingrese el ID del usuario, el ID del producto y la cantidad que desea comprar.
- **Gestión de cupo**: Esta opción le permite gestionar los cupos de los usuarios. Puede crear un nuevo usuario, asignar un cupo a un usuario, mostrar todos los usuarios o eliminar un usuario.
- **Gestión de inventario**: Esta opción le permite gestionar el inventario de productos. Puede crear un nuevo producto, mostrar todos los productos o eliminar un producto.
- **Salir**: Esta opción cierra la aplicación.

## Notas adicionales

- Todas las operaciones de la base de datos se realizan dentro de transacciones para garantizar la atomicidad y la consistencia. Si ocurre un error durante una operación, todas las modificaciones de la base de datos se revertirán a su estado anterior a la transacción.
- La aplicación maneja el control de concurrencia utilizando bloqueos. Cuando se inicia una transacción, la base de datos coloca bloqueos en los registros que están siendo modificados para evitar que otros usuarios los modifiquen hasta que la transacción se haya confirmado o revertido.

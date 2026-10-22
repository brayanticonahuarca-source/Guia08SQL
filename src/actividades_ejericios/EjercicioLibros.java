package actividades_ejericios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class EjercicioLibros {
	private static final String URL = "jdbc:sqlite:biblioteca_ejercicio.db";
	private static final String CLAVE_CORRECTA = "1234";

	public static void main(String[] args) {
		Connection con = null;
		Scanner sc = new Scanner(System.in);

		try {
			Class.forName("org.sqlite.JDBC");

			con = DriverManager.getConnection(URL);
			con.setAutoCommit(false);

			try (Statement stmtTabla = con.createStatement()) {
				String sqlTabla = "CREATE TABLE IF NOT EXISTS libro ("
                        + "id INTEGER PRIMARY KEY, "
                        + "titulo TEXT NOT NULL, "
                        + "autor TEXT NOT NULL, "
                        + "precio REAL NOT NULL);";
                stmtTabla.execute(sqlTabla); 
                con.commit();
			}

			int opcion = 0;
			do {
				System.out.println("\n--- MENÚ GESTIÓN DE LIBROS ---");
				System.out.println("1. Ingresar libro");
				System.out.println("2. Actualizar libro");
				System.out.println("3. Borrar libro");
				System.out.println("4. Mostrar libros");
				System.out.println("5. Salir");
				System.out.print("Seleccione una opción: ");
				
				try {
					opcion = Integer.parseInt(sc.nextLine());
				} catch (NumberFormatException e) {
					opcion = 0;
				}

				switch (opcion) {
				case 1:
					ingresarLibro(con, sc);
					break;
				case 2:
					actualizarLibro(con, sc);
					break;
				case 3:
					borrarLibro(con, sc);
					break;
				case 4:
					mostrarLibros(con);
					break;
				case 5:
					System.out.println("Saliendo...");
					break;
				default:
					System.out.println("Opción no válida.");
				}
			} while (opcion != 5);

		} catch (ClassNotFoundException e) {
            System.err.println( e.getMessage());
        } catch (SQLException e) {
            System.err.println("" + e.getMessage());
        } finally {
        	if (con != null) {
                try {
                    con.close();
                    System.out.println("\nPASO 5: Conexión cerrada correctamente.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        	sc.close();
        }
	}
	public static void ingresarLibro(Connection con, Scanner sc) throws SQLException {
		System.out.print("ID: ");
		int id = Integer.parseInt(sc.nextLine());
		System.out.print("Título: ");
		String titulo = sc.nextLine();
		System.out.print("Autor: ");
		String autor = sc.nextLine();
		System.out.print("Precio: ");
		double precio = Double.parseDouble(sc.nextLine());

		String sql = "INSERT INTO libro(id, titulo, autor, precio) VALUES(?, ?, ?, ?)";
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			pstmt.setString(2, titulo);
			pstmt.setString(3, autor);
			pstmt.setDouble(4, precio);
			pstmt.executeUpdate();

			validarClaveYConfirmar(con, sc);
		} catch (SQLException e) {
			if (con != null) con.rollback();
			System.out.println("Error al insertar. Transacción revertida.");
		}
	}
	public static void actualizarLibro(Connection con, Scanner sc) throws SQLException {
		System.out.print("Ingrese ID del libro a actualizar: ");
		int id = Integer.parseInt(sc.nextLine());
		System.out.print("Nuevo Precio: ");
		double precio = Double.parseDouble(sc.nextLine());

		String sql = "UPDATE libro SET precio = ? WHERE id = ?";
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setDouble(1, precio);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();

			validarClaveYConfirmar(con, sc);
		} catch (SQLException e) {
			if (con != null) con.rollback();
			System.out.println("Transacción revertida.");
		}
	}
	public static void borrarLibro(Connection con, Scanner sc) throws SQLException {
		System.out.print("Ingrese ID del libro a eliminar: ");
		int id = Integer.parseInt(sc.nextLine());

		String sql = "DELETE FROM libro WHERE id = ?";
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();

			validarClaveYConfirmar(con, sc);
		} catch (SQLException e) {
			if (con != null) con.rollback();
			System.out.println("Error al borrar. Transacción revertida.");
		}
	}
	public static void mostrarLibros(Connection con) throws SQLException {
		String sqlSelect = "SELECT id, titulo, autor, precio FROM libro";
		try (Statement stmtSelect = con.createStatement();
             ResultSet rs = stmtSelect	.executeQuery(sqlSelect)) {

            System.out.println("\n--- REGISTROS ACTUALES EN LA TABLA LIBRO ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                double precio = rs.getDouble("precio");
                System.out.println("ID: " + id + " | Título: " 
                + titulo + " | Autor: " + autor + " | Precio: S/. " + precio);
            }
        }
	}
	public static void validarClaveYConfirmar(Connection con, Scanner sc) throws SQLException {
		System.out.print("Ingrese la clave para confirmar los cambios en la BD: ");
		String clave = sc.nextLine();

		if (CLAVE_CORRECTA.equals(clave)) {
			con.commit();
			System.out.println("Clave correcta. Cambios guardados.");
		} else {
			con.rollback();
			System.out.println("Clave incorrecta. Operación revertida.");
		}
	}
}
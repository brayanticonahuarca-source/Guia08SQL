package actividades_ejericios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestActividadLibros {
	private static final String URL = "jdbc:sqlite:biblioteca_actividad.db";

	public static void main(String[] args) {
		Connection con = null;

		try {
			Class.forName("org.sqlite.JDBC");

			con = DriverManager.getConnection(URL);
			con.setAutoCommit(false);

			try (Statement stmtTabla = con.createStatement()) {
				stmtTabla.execute("DROP TABLE IF EXISTS libro;");
				String sqlTabla = "CREATE TABLE IF NOT EXISTS libro ("
                        + "id INTEGER PRIMARY KEY, "
                        + "titulo TEXT NOT NULL, "
                        + "autor TEXT NOT NULL);";
                stmtTabla.execute(sqlTabla); 
                con.commit();
			}

			String sqlInsert = "INSERT INTO libro(id, titulo, autor) VALUES(?, ?, ?)";
			try (PreparedStatement pstmtInsert = con.prepareStatement(sqlInsert)) {
				pstmtInsert.setInt(1, 101);
                pstmtInsert.setString(2, "Cien Años de Soledad");
                pstmtInsert.setString(3, "Gabriel García Márquez");
                pstmtInsert.executeUpdate();
                
                pstmtInsert.setInt(1, 102);
                pstmtInsert.setString(2, "Don Quijote de la Mancha");
                pstmtInsert.setString(3, "Miguel de Cervantes");
                pstmtInsert.executeUpdate();

                con.commit(); 
                System.out.println("Registros insertados con éxito.");
			} catch (SQLException e) {
                if (con != null) con.rollback();
                System.out.println("Error detectado.rollback().");
            }

			String sqlSelect = "SELECT id, titulo, autor FROM libro";
			try (Statement stmtSelect = con.createStatement();
	             ResultSet rs = stmtSelect.executeQuery(sqlSelect)) {

	                System.out.println("\n--- REGISTROS ACTUALES EN LA TABLA LIBRO ---");
	                while (rs.next()) {
	                    int id = rs.getInt("id");
	                    String titulo = rs.getString("titulo");
	                    String autor = rs.getString("autor");
	                    System.out.println("ID: " + id + " | Título: " + titulo 
	                    		+ " | Autor: " + autor);
	                }
	            }

			String sqlUpdate = "UPDATE libro SET titulo = ? WHERE id = ?";
			try (PreparedStatement pstmtUpdate = con.prepareStatement(sqlUpdate)) {
				pstmtUpdate.setString(1, "Don Quijote de la Mancha (Edición Especial)");
				pstmtUpdate.setInt(2, 102);
				pstmtUpdate.executeUpdate();

				con.commit();
				System.out.println("\n Registro ID 102 actualizado");
			} catch (SQLException e) {
				if (con != null) con.rollback();
				System.out.println("Error al actualizar");
			}

			String sqlDelete = "DELETE FROM libro WHERE id = ?";
            try (PreparedStatement pstmtDelete = con.prepareStatement(sqlDelete)) {
                pstmtDelete.setInt(1, 101);
                pstmtDelete.executeUpdate();

                con.commit();
                System.out.println(" Registro ID 101 eliminado ");
            } catch (SQLException e) {
                if (con != null) con.rollback();
                System.out.println("Error al borrar. Transacción revertida");
            }

            try (Statement stmtFinal = con.createStatement();
                 ResultSet rsFinal = stmtFinal.executeQuery(sqlSelect)) {

                System.out.println("\n--- ESTADO FINAL DE LA TABLA LIBRO ---");
                while (rsFinal.next()) {
                    System.out.println("ID: " + rsFinal.getInt("id") 
                            + " | Título: " + rsFinal.getString("titulo") 
                            + " | Autor: " + rsFinal.getString("autor"));
                }
            }

		} catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el controlador (Paso 1): " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error SQL en las operaciones: " + e.getMessage());
        } finally {
        	if (con != null) {
                try {
                    con.close();
                    System.out.println("\nPASO 5: Conexión cerrada correctamente.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}
}

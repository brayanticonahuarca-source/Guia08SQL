package ejercicio_2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
public class Main {
    private static final String URL = 
    		"jdbc:sqlite:biblioteca_ejercicio.db";
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection con = DriverManager.getConnection(URL)) {
                
                GestorLibros gestor = new GestorLibros();
                gestor.cargarDesdeBD(con);
                System.out.println("\n   CONFIGURAR CONSULTA EN JAVA    ");

                System.out.println("Ingrese los campos a mostrar separados por coma: ");
                String[] camposInput = sc.nextLine().split(",");
                List<String> campos = new ArrayList<>();
                for (String c : camposInput) {
                    if (!c.trim().isEmpty()) campos.add(c.trim());
                }
                System.out.print("¿Desea filtrar por precio mínimo? "
                		+ "Ingrese el monto (o 0 para omitir): ");
                double precioMin = Double.parseDouble(sc.nextLine());
                Predicate<Libro> filtro = (precioMin > 0) 
                		? libro -> libro.getPrecio() >= precioMin : null;
                System.out.print("Campo por el cual ordenar (id, titulo, autor, precio): ");
                String campoOrden = sc.nextLine().trim();

                System.out.print("¿Orden ascendente? (s/n): ");
                boolean asc = sc.nextLine().trim().equalsIgnoreCase("s");
                System.out.print("Límite de registros a mostrar (0 para sin límite): ");
                int limite = Integer.parseInt(sc.nextLine());

                gestor.consultarMemoria(campos, filtro, campoOrden, asc, limite);
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Entrada numéricas no válida.");
        } finally {
            sc.close();
        }
    }
}
package ejercicio_2;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
public class GestorLibros {
    private List<Libro> listaLibros;
    public GestorLibros() {
        this.listaLibros = new ArrayList<>();
    }
    public void cargarDesdeBD(Connection con) throws SQLException {
        listaLibros.clear();
        String sql = "SELECT id, titulo, autor, precio FROM libro";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Libro libro = new Libro(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getDouble("precio")
                );
                listaLibros.add(libro);
            }
        }
        System.out.println("Se cargaron " + listaLibros.size() 
        + " registros desde la BD al arreglo en memoria.");
    }
    public void consultarMemoria(List<String> camposAMostrar, 
    		Predicate<Libro> condicion, String campoOrden, 
    		boolean ascendente, int limite) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro l : listaLibros) {
            if (condicion == null || condicion.test(l)) {
                resultado.add(l);
            }
        }
        if (campoOrden != null && !campoOrden.isEmpty()) {
            Comparator<Libro> comp = null;
            switch (campoOrden.toLowerCase()) {
                case "id":
                    comp = Comparator.comparingInt(Libro::getId);
                    break;
                case "titulo":
                    comp = Comparator.comparing(Libro::getTitulo, 
                    		String.CASE_INSENSITIVE_ORDER);
                    break;
                case "autor":
                    comp = Comparator.comparing(Libro::getAutor, 
                    		String.CASE_INSENSITIVE_ORDER);
                    break;
                case "precio":
                    comp = Comparator.comparingDouble(Libro::getPrecio);
                    break;
            }
            if (comp != null) {
                if (!ascendente) {
                    comp = comp.reversed();
                }
                resultado.sort(comp);
            }
        }
        if (limite > 0 && limite < resultado.size()) {
            resultado = resultado.subList(0, limite);
        }

        System.out.println("\n    RESULTADO DE LA CONSULTA");
        if (resultado.isEmpty()) {
            System.out.println("No hay registro ");
            return;
        }

        for (Libro l : resultado) {
            StringBuilder sb = new StringBuilder();
            if (camposAMostrar == null || camposAMostrar.isEmpty()) {
                sb.append(l.toString());
            } else {
                for (String campo : camposAMostrar) {
                    switch (campo.toLowerCase()) {
                        case "id":
                            sb.append("ID: ").append(l.getId()).append(" | ");
                            break;
                        case "titulo":
                            sb.append("Título: ").append(l.getTitulo()).append(" | ");
                            break;
                        case "autor":
                            sb.append("Autor: ").append(l.getAutor()).append(" | ");
                            break;
                        case "precio":
                            sb.append("Precio: S/. ").append(l.getPrecio()).append
                            (" | ");
                            break;
                    }
                }
            }
            System.out.println(sb.toString());
        }
    }
}
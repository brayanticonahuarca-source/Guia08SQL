package ejercicio_2;
public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private double precio;
    public Libro(int id, String titulo, String autor, double precio) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
    }
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public double getPrecio() { return precio; }
    @Override
    public String toString() {
        return "ID: " + id + " | Título: " 
    + titulo + " | Autor: " + autor 
    + " | Precio: S/. " + precio;
    }
}
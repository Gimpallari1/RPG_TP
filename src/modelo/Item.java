package modelo;

import java.io.Serializable;

public abstract class Item implements Serializable {
    protected String nombre;
    protected String descripcion;
    protected int precio;

    public Item(String nombre, String descripcion, int precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getPrecio() { return precio; }

    @Override
    public String toString() {
        return nombre + " - " + descripcion;
    }
}
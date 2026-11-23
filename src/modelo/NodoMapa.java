package modelo;

import java.io.Serializable;

public class NodoMapa implements Serializable {
    public enum Tipo { COMBATE, DESCANSO, JEFE }

    private int numero;
    private String nombre;
    private Tipo tipo;
    private boolean completado;

    public NodoMapa(int numero, String nombre, Tipo tipo) {
        this.numero = numero;
        this.nombre = nombre;
        this.tipo = tipo;
        this.completado = false;
    }

    public int getNumero() { return numero; }
    public String getNombre() { return nombre; }
    public Tipo getTipo() { return tipo; }
    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
}
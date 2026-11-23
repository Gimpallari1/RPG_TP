package modelo;

import java.io.Serializable;

public class Habilidad implements Serializable {
    public enum Tipo { DANIO, CURACION }

    private String nombre;
    private int costoMana;
    private double multiplicador;
    private Tipo tipo;

    public Habilidad(String nombre, int costoMana, double multiplicador, Tipo tipo) {
        this.nombre = nombre;
        this.costoMana = costoMana;
        this.multiplicador = multiplicador;
        this.tipo = tipo;
    }

    public String getNombre() { return nombre; }
    public int getCostoMana() { return costoMana; }
    public double getMultiplicador() { return multiplicador; }
    public Tipo getTipo() { return tipo; }

    @Override
    public String toString() {
        return nombre + " (MP: " + costoMana + ")";
    }
}
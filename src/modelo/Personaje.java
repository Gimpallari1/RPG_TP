package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Personaje implements Serializable, Comparable<Personaje> {
    protected String nombre;
    protected int nivel;
    protected int hpMax;
    protected int hpActual;
    protected int mpMax;
    protected int mpActual;
    protected int ataqueBase;
    protected int defensaBase;
    protected int velocidad;
    protected double barraTurno;
    protected boolean defendiendo;
    protected String spritePath;
    protected List<Habilidad> habilidades;

    public Personaje(String nombre, int nivel, int hp, int mp, int ataque, int defensa, int velocidad, String spritePath) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.hpMax = hp;
        this.hpActual = hp;
        this.mpMax = mp;
        this.mpActual = mp;
        this.ataqueBase = ataque;
        this.defensaBase = defensa;
        this.velocidad = velocidad;
        this.barraTurno = 0.0;
        this.defendiendo = false;
        this.spritePath = spritePath;
        this.habilidades = new ArrayList<>();
    }

    public int getAtaqueTotal() {
        return ataqueBase;
    }

    public int getDefensaTotal() {
        return defendiendo ? defensaBase * 2 : defensaBase;
    }

    public int getVelocidadEfectiva() {
        return velocidad;
    }

    public void avanzarBarraTurno(double multiplicadorTick) {
        if (estaVivo()) {
            this.barraTurno = Math.min(100.0, this.barraTurno + (getVelocidadEfectiva() * multiplicadorTick));
        } else {
            this.barraTurno = 0.0;
        }
    }

    public boolean barraTurnoLlena() {
        return estaVivo() && this.barraTurno >= 100.0;
    }

    public void reiniciarBarraTurno() {
        this.barraTurno = 0.0;
    }

    public int getPorcentajeBarraTurno() {
        return (int) Math.min(100, Math.round(barraTurno));
    }

    public int recibirDanio(int danioBruto) {
        int defensaEfectiva = getDefensaTotal();
        int danioReal = Math.max(1, danioBruto - defensaEfectiva);
        this.hpActual = Math.max(0, this.hpActual - danioReal);
        if (!estaVivo()) {
            this.barraTurno = 0.0;
        }
        return danioReal;
    }

    public int curar(int cantidad) {
        if (!estaVivo()) return 0;
        int hpAntes = this.hpActual;
        this.hpActual = Math.min(this.hpMax, this.hpActual + cantidad);
        return this.hpActual - hpAntes;
    }

    public void restaurarMana(int cantidad) {
        this.mpActual = Math.min(this.mpMax, this.mpActual + cantidad);
    }

    public void consumirMana(int costo) {
        this.mpActual = Math.max(0, this.mpActual - costo);
    }

    public void activarDefensa() {
        this.defendiendo = true;
    }

    public void reiniciarEstadoTurno() {
        this.defendiendo = false;
    }

    public boolean estaVivo() {
        return this.hpActual > 0;
    }

    @Override
    public int compareTo(Personaje otro) {
        int cmpBarra = Double.compare(otro.barraTurno, this.barraTurno);
        if (cmpBarra != 0) return cmpBarra;
        return Integer.compare(otro.getVelocidadEfectiva(), this.getVelocidadEfectiva());
    }

    public String getNombre() { return nombre; }
    public int getNivel() { return nivel; }
    public int getHpMax() { return hpMax; }
    public int getHpActual() { return hpActual; }
    public int getMpMax() { return mpMax; }
    public int getMpActual() { return mpActual; }
    public int getVelocidad() { return velocidad; }
    public double getBarraTurno() { return barraTurno; }
    public boolean isDefendiendo() { return defendiendo; }
    public String getSpritePath() { return spritePath; }
    public List<Habilidad> getHabilidades() { return habilidades; }
}
import java.io.Serializable;

public class Enemigo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private int vidaMax, vidaActual, ataque, defensa, velocidad, expRecompensa, oroRecompensa;
    private String spriteUrl;
    private String tipoIA;

    public Enemigo(String nombre, int vida, int ataque, int defensa, int velocidad, int expRecompensa, int oroRecompensa, String spriteUrl, String tipoIA) {
        this.nombre = nombre;
        this.vidaMax = vida;
        this.vidaActual = vida;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.expRecompensa = expRecompensa;
        this.oroRecompensa = oroRecompensa;
        this.spriteUrl = spriteUrl;
        this.tipoIA = tipoIA;
    }

    public void recibirDanio(int cantidad) {
        int danioEfectivo = Math.max(1, cantidad - (defensa / 2));
        this.vidaActual = Math.max(0, this.vidaActual - danioEfectivo);
    }

    public boolean estaVivo() { return vidaActual > 0; }

    public String getNombre() { return nombre; }
    public int getVidaActual() { return vidaActual; }
    public int getVidaMax() { return vidaMax; }
    public int getAtaque() { return ataque; }
    public int getVelocidad() { return velocidad; }
    public int getExpRecompensa() { return expRecompensa; }
    public int getOroRecompensa() { return oroRecompensa; }
    public String getSpriteUrl() { return spriteUrl; }
    public String getTipoIA() { return tipoIA; }
}
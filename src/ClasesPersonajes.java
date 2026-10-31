import java.io.Serializable;

public abstract class Personaje implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nombre;
    protected int nivel;
    protected int vidaActual;
    protected int vidaMax;
    protected int manaActual;
    protected int manaMax;
    protected int ataque;
    protected int defensa;
    protected int experiencia;
    protected String spriteUrl;

    public Personaje(String nombre, int vidaMax, int manaMax, int ataque, int defensa, String spriteUrl) {
        this.nombre = nombre;
        this.nivel = 1;
        this.vidaMax = vidaMax;
        this.vidaActual = vidaMax;
        this.manaMax = manaMax;
        this.manaActual = manaMax;
        this.ataque = ataque;
        this.defensa = defensa;
        this.experiencia = 0;
        this.spriteUrl = spriteUrl;
    }

    public boolean estaVivo() {
        return this.vidaActual > 0;
    }

    public void curar(int cantidad) {
        this.vidaActual += cantidad;
        if (this.vidaActual > this.vidaMax) {
            this.vidaActual = this.vidaMax;
        }
    }

    public void recuperarMana(int cantidad) {
        this.manaActual += cantidad;
        if (this.manaActual > this.manaMax) {
            this.manaActual = this.manaMax;
        }
    }

    public void recibirDanio(int danio) {
        int danioReal = Math.max(1, danio - (this.defensa / 2));
        this.vidaActual -= danioReal;
        if (this.vidaActual < 0) {
            this.vidaActual = 0;
        }
    }

    public void ganarExperiencia(int exp) {
        this.experiencia += exp;
        while (this.experiencia >= 100 * nivel) {
            this.experiencia -= 100 * nivel;
            this.nivel++;
            this.vidaMax += 20;
            this.manaMax += 10;
            this.ataque += 5;
            this.defensa += 2;
        }
    }

    public abstract String usaraHabilidad(Enemigo enemigo);

    // Getters y Setters
    public String getNombre() { return nombre; }
    public int getNivel() { return nivel; }
    public int getVidaActual() { return vidaActual; }
    public int getVidaMax() { return vidaMax; }
    public int getManaActual() { return manaActual; }
    public int getManaMax() { return manaMax; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getExperiencia() { return experiencia; }
    public String getSpriteUrl() { return spriteUrl; }
}
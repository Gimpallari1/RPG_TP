package modelo;

public class Equipable extends Item {
    public enum Ranura { ARMA, ARMADURA, ACCESORIO }

    private Ranura ranura;
    private int bonusAtaque;
    private int bonusDefensa;
    private int bonusVelocidad;

    public Equipable(String nombre, String descripcion, int precio, Ranura ranura, int atk, int def, int vel) {
        super(nombre, descripcion, precio);
        this.ranura = ranura;
        this.bonusAtaque = atk;
        this.bonusDefensa = def;
        this.bonusVelocidad = vel;
    }

    public Ranura getRanura() { return ranura; }
    public int getBonusAtaque() { return bonusAtaque; }
    public int getBonusDefensa() { return bonusDefensa; }
    public int getBonusVelocidad() { return bonusVelocidad; }
}
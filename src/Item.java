import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String tipo; // "PocionVida", "PocionMana", "Revivir"
    private int valorEfecto;
    private int precio;

    public Item(String nombre, String tipo, int valorEfecto, int precio) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valorEfecto = valorEfecto;
        this.precio = precio;
    }

    public boolean aplicarA(Personaje p) {
        if (tipo.equals("Revivir")) {
            if (!p.estaVivo()) {
                p.curar(valorEfecto); // Le devuelve HP para revivirlo
                return true;
            }
            return false; // No se puede usar revivir en un aliado vivo
        } else if (tipo.equals("PocionVida")) {
            if (p.estaVivo()) {
                p.curar(valorEfecto);
                return true;
            }
            return false; // No se puede curar a alguien muerto
        } else if (tipo.equals("PocionMana")) {
            if (p.estaVivo()) {
                p.recuperarMana(valorEfecto);
                return true;
            }
            return false;
        }
        return false;
    }

    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public int getValorEfecto() { return valorEfecto; }
    public int getPrecio() { return precio; }
}
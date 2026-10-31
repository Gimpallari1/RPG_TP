import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Partida implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Personaje> party = new ArrayList<>();
    private List<Item> inventario = new ArrayList<>();
    private int oro = 50;
    
    private List<NodoMapa> mapa = new ArrayList<>();
    private NodoMapa nodoActual;
    private int nivelActual = 1;

    // Estado del combate serializable
    private Enemigo enemigoActual;
    private boolean batallaFinalizada;
    private String resultadoUltimaBatalla;

    public Partida() {
        party.add(new Guerrero("Thorin"));
        party.add(new Mago("Gandalf"));
        party.add(new Arquero("Legolas"));
        party.add(new Curador("Elrond"));
        
        inventario.add(new Item("Poción Vida Ch", "PocionVida", 30, 20));

        generarMapa();
    }

    private void generarMapa() {
        NodoMapa n1 = new NodoMapa(1, 1, "COMBATE");

        NodoMapa n2 = new NodoMapa(2, 2, "COMBATE");
        NodoMapa n3 = new NodoMapa(3, 2, "DESCANSO");

        NodoMapa n4 = new NodoMapa(4, 3, "TIENDA");
        NodoMapa n5 = new NodoMapa(5, 3, "COMBATE");

        NodoMapa n6 = new NodoMapa(6, 4, "COMBATE_JEFE");

        n1.agregarConexion(n2);
        n1.agregarConexion(n3);

        n2.agregarConexion(n4);
        n2.agregarConexion(n5);
        n3.agregarConexion(n4);
        n3.agregarConexion(n5);

        n4.agregarConexion(n6);
        n5.agregarConexion(n6);

        mapa.add(n1);
        mapa.add(n2);
        mapa.add(n3);
        mapa.add(n4);
        mapa.add(n5);
        mapa.add(n6);

        this.nodoActual = n1;
        this.nivelActual = 1;
    }

    public List<Personaje> getParty() { return party; }
    public List<Item> getInventario() { return inventario; }
    public int getOro() { return oro; }
    public void sumarOro(int cantidad) { this.oro += cantidad; }
    public boolean restarOro(int cantidad) {
        if (this.oro >= cantidad) {
            this.oro -= cantidad;
            return true;
        }
        return false;
    }
    public List<NodoMapa> getMapa() { return mapa; }
    public NodoMapa getNodoActual() { return nodoActual; }
    public void setNodoActual(NodoMapa nodo) { this.nodoActual = nodo; }

    public int getNivelActual() { return nivelActual; }
    public void setNivelActual(int nivelActual) { this.nivelActual = nivelActual; }

    public Enemigo getEnemigoActual() { return enemigoActual; }
    public void setEnemigoActual(Enemigo enemigoActual) { this.enemigoActual = enemigoActual; }
    public boolean isBatallaFinalizada() { return batallaFinalizada; }
    public void setBatallaFinalizada(boolean batallaFinalizada) { this.batallaFinalizada = batallaFinalizada; }
    public String getResultadoUltimaBatalla() { return resultadoUltimaBatalla; }
    public void setResultadoUltimaBatalla(String resultadoUltimaBatalla) { this.resultadoUltimaBatalla = resultadoUltimaBatalla; }
}
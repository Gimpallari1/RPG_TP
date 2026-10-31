import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NodoMapa implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int nivel;
    private String tipo;
    private boolean completado;
    private boolean usado;
    private List<NodoMapa> conexiones;

    public NodoMapa(int id, int nivel, String tipo) {
        this.id = id;
        this.nivel = nivel;
        this.tipo = tipo;
        this.completado = false;
        this.usado = false;
        this.conexiones = new ArrayList<>();
    }

    public void agregarConexion(NodoMapa siguiente) {
        this.conexiones.add(siguiente);
    }

    public int getId() { return id; }
    public int getNivel() { return nivel; }
    public String getTipo() { return tipo; }
    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
    public boolean isUsado() { return usado; }
    public void setUsado(boolean usado) { this.usado = usado; }
    public List<NodoMapa> getConexiones() { return conexiones; }
}
package servicio;

import excepciones.JuegoException;
import java.io.*;

public class PersistenciaService {
    private static final String ARCHIVO = "Partida.dat";

    public void guardar(EstadoJuego estado) throws JuegoException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(estado);
        } catch (IOException e) {
            throw new JuegoException("Error al guardar la partida: " + e.getMessage());
        }
    }

    public EstadoJuego cargar() throws JuegoException {
        File f = new File(ARCHIVO);
        if (!f.exists()) {
            throw new JuegoException("No existe ninguna partida guardada.");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (EstadoJuego) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new JuegoException("Error al cargar la partida: " + e.getMessage());
        }
    }

    public static class EstadoJuego implements Serializable {
        public java.util.List<modelo.Heroe> party;
        public java.util.List<modelo.Item> inventario;
        public java.util.List<modelo.NodoMapa> mapa;
        public int nodoActualIndex;
        public int oro;
    }
}
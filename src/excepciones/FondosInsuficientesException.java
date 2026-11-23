package excepciones;

public class FondosInsuficientesException extends JuegoException {
    public FondosInsuficientesException(int costo, int oroActual) {
        super("Oro insuficiente. Costo: " + costo + "G, Disponible: " + oroActual + "G.");
    }
}
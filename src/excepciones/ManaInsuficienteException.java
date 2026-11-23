package excepciones;

public class ManaInsuficienteException extends JuegoException {
    public ManaInsuficienteException(String personaje, int costo, int actual) {
        super(personaje + " no tiene suficiente maná (Requiere: " + costo + ", Actual: " + actual + ").");
    }
}
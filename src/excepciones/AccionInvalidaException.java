package excepciones;

public class AccionInvalidaException extends JuegoException {
    public AccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
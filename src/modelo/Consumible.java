package modelo;

import excepciones.AccionInvalidaException;

public class Consumible extends Item {
    public enum Efecto { CURAR_HP, RESTAURAR_MP, REVIVIR }

    private Efecto efecto;
    private int valor;

    public Consumible(String nombre, String descripcion, int precio, Efecto efecto, int valor) {
        super(nombre, descripcion, precio);
        this.efecto = efecto;
        this.valor = valor;
    }

    public String aplicar(Heroe objetivo) throws AccionInvalidaException {
        switch (efecto) {
            case CURAR_HP:
                if (!objetivo.estaVivo()) {
                    throw new AccionInvalidaException("No puedes curar a un aliado caído. Usa una Pluma de Fénix.");
                }
                int curado = objetivo.curar(valor);
                return objetivo.getNombre() + " recuperó " + curado + " HP.";
            case RESTAURAR_MP:
                if (!objetivo.estaVivo()) {
                    throw new AccionInvalidaException("El objetivo está caído.");
                }
                objetivo.restaurarMana(valor);
                return objetivo.getNombre() + " recuperó " + valor + " MP.";
            case REVIVIR:
                if (objetivo.estaVivo()) {
                    throw new AccionInvalidaException(objetivo.getNombre() + " ya está con vida.");
                }
                objetivo.revivir(valor);
                return "¡" + objetivo.getNombre() + " ha vuelto al combate!";
            default:
                throw new AccionInvalidaException("Efecto desconocido.");
        }
    }

    public Efecto getEfecto() { return efecto; }
    public int getValor() { return valor; }
}
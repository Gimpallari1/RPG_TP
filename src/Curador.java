public class Curador extends Personaje {
    private static final long serialVersionUID = 1L;

    public Curador(String nombre) {
        super(nombre, 90, 60, 15, 8, "src/sprites/curador.png");
    }

    @Override
    public String usaraHabilidad(Enemigo enemigo) {
        int costoMana = 20;
        if (this.manaActual >= costoMana) {
            this.manaActual -= costoMana;
            int curacion = 40;
            this.curar(curacion);
            return this.nombre + " usó 'Plegaria Sagrada' y se curó " + curacion + " de HP.";
        } else {
            return this.nombre + " no tiene suficiente Maná para realizar una curación.";
        }
    }
}
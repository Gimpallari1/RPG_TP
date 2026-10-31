public class Arquero extends Personaje {
    private static final long serialVersionUID = 1L;

    public Arquero(String nombre) {
        super(nombre, 95, 40, 28, 6, "src/sprites/arquero.png");
    }

    @Override
    public String usaraHabilidad(Enemigo enemigo) {
        int costoMana = 15;
        if (this.manaActual >= costoMana) {
            this.manaActual -= costoMana;
            int danioHabilidad = this.ataque + 20;
            enemigo.recibirDanio(danioHabilidad);
            return this.nombre + " usó 'Disparo Certero' causando " + danioHabilidad + " de daño a " + enemigo.getNombre() + ".";
        } else {
            return this.nombre + " no tiene suficiente Maná para disparar.";
        }
    }
}
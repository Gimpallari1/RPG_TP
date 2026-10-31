public class Guerrero extends Personaje {
    private static final long serialVersionUID = 1L;

    public Guerrero(String nombre) {
        super(nombre, 120, 20, 25, 10, "src/sprites/guerrero.png");
    }

    @Override
    public String usaraHabilidad(Enemigo enemigo) {
        int costoMana = 10;
        if (this.manaActual >= costoMana) {
            this.manaActual -= costoMana;
            int danioHabilidad = this.ataque + 15;
            enemigo.recibirDanio(danioHabilidad);
            return this.nombre + " usó 'Golpe Devastador' causando " + danioHabilidad + " de daño a " + enemigo.getNombre() + ".";
        } else {
            return this.nombre + " intentó usar su habilidad pero no tiene suficiente Maná.";
        }
    }
}
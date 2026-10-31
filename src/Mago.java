public class Mago extends Personaje {
    private static final long serialVersionUID = 1L;

    public Mago(String nombre) {
        super(nombre, 80, 80, 35, 4, "src/sprites/mago.png");
    }

    @Override
    public String usaraHabilidad(Enemigo enemigo) {
        int costoMana = 25;
        if (this.manaActual >= costoMana) {
            this.manaActual -= costoMana;
            int danioHabilidad = this.ataque * 2;
            enemigo.recibirDanio(danioHabilidad);
            return this.nombre + " lanzó 'Bola de Fuego' causando " + danioHabilidad + " de daño mágico a " + enemigo.getNombre() + ".";
        } else {
            return this.nombre + " intentó lanzar un hechizo pero no tiene suficiente Maná.";
        }
    }
}
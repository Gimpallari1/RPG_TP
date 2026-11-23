package modelo;

import java.util.List;

public class Goblin extends Enemigo {
    public Goblin(int nivel) {
        super("Goblin Saqueador", nivel, 55 + nivel * 12, 20, 14 + nivel * 3, 4 + nivel, 16 + nivel,
              35 * nivel, 25 * nivel, "src/sprites/goblin.png");
    }

    @Override
    public String ejecutarTurno(List<Heroe> heroes) {
        Heroe objetivo = elegirObjetivoMasDebil(heroes);
        if (objetivo == null) return nombre + " observa el campo.";
        int danioReal = objetivo.recibirDanio(getAtaqueTotal());
        return nombre + " apuñala por la espalda a " + objetivo.getNombre() + " por " + danioReal + " de daño real.";
    }
}
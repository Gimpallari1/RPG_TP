package modelo;

import java.util.List;

public class Orco extends Enemigo {
    public Orco(int nivel) {
        super("Orco Berserker", nivel, 100 + nivel * 20, 30, 21 + nivel * 4, 9 + nivel * 2, 8 + nivel,
              65 * nivel, 45 * nivel, "src/sprites/orco.png");
    }

    @Override
    public String ejecutarTurno(List<Heroe> heroes) {
        Heroe objetivo = elegirObjetivoAleatorio(heroes);
        if (objetivo == null) return nombre + " ruge.";
        if (random.nextDouble() < 0.4) {
            int danioReal = objetivo.recibirDanio((int) (getAtaqueTotal() * 1.4));
            return "¡" + nombre + " descarga un GOLPE APLASTANTE sobre " + objetivo.getNombre() + " por " + danioReal + " de daño real!";
        }
        int danioReal = objetivo.recibirDanio(getAtaqueTotal());
        return nombre + " ataca brutalmente a " + objetivo.getNombre() + " por " + danioReal + " de daño real.";
    }
}
package modelo;

import java.util.List;

public class DragonFuego extends Enemigo {
    public DragonFuego(int nivel) {
        super("Dragón de Fuego Ancestral", nivel, 240 + nivel * 35, 100, 28 + nivel * 5, 14 + nivel * 2, 15 + nivel,
              250 * nivel, 200 * nivel, "src/sprites/dragon.png");
    }

    @Override
    public String ejecutarTurno(List<Heroe> heroes) {
        if (mpActual >= 25 && random.nextDouble() < 0.45) {
            consumirMana(25);
            StringBuilder sb = new StringBuilder("¡" + nombre + " desata ALIENTO DE FUEGO sobre toda la party! ");
            for (Heroe h : heroes) {
                if (h.estaVivo()) {
                    int danioReal = h.recibirDanio((int) (getAtaqueTotal() * 0.85));
                    sb.append(h.getNombre()).append(" (-").append(danioReal).append(" HP) ");
                }
            }
            return sb.toString();
        }
        Heroe objetivo = elegirObjetivoMasDebil(heroes);
        if (objetivo == null) return nombre + " sobrevuela el campo.";
        int danioReal = objetivo.recibirDanio(getAtaqueTotal());
        return nombre + " desgarra con sus garras a " + objetivo.getNombre() + " por " + danioReal + " de daño real.";
    }
}
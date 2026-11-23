package modelo;

import java.util.List;

public class Esqueleto extends Enemigo {
    public Esqueleto(int nivel) {
        super("Esqueleto Guerrero", nivel, 70 + nivel * 15, 20, 16 + nivel * 3, 7 + nivel * 2, 11 + nivel,
              45 * nivel, 30 * nivel, "src/sprites/esqueleto.png");
    }

    @Override
    public String ejecutarTurno(List<Heroe> heroes) {
        if (hpActual < hpMax * 0.35 && !defendiendo && random.nextBoolean()) {
            activarDefensa();
            return nombre + " alza su escudo óseo y entra en postura defensiva.";
        }
        Heroe objetivo = elegirObjetivoAleatorio(heroes);
        if (objetivo == null) return nombre + " espera.";
        int danioReal = objetivo.recibirDanio(getAtaqueTotal());
        return nombre + " golpea con su espada oxidada a " + objetivo.getNombre() + " por " + danioReal + " de daño real.";
    }
}
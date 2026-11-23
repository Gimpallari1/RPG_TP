package modelo;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class Enemigo extends Personaje {
    protected int recompensaExp;
    protected int recompensaOro;
    protected final Random random = new Random();

    public Enemigo(String nombre, int nivel, int hpBase, int mpBase, int atqBase, int defBase, int velBase,
                   int expBase, int oroBase, String spritePath) {
        super(nombre, nivel,
                (int) (hpBase * (1 + (nivel - 1) * 0.30)),
                mpBase,
                (int) (atqBase * (1 + (nivel - 1) * 0.25)),
                (int) (defBase * (1 + (nivel - 1) * 0.25)),
                velBase + (nivel - 1),
                spritePath);
        this.recompensaExp = expBase * nivel;
        this.recompensaOro = oroBase * nivel;
    }

    public abstract String ejecutarTurno(List<Heroe> heroes);

    protected List<Heroe> obtenerHeroesVivos(List<Heroe> heroes) {
        return heroes.stream().filter(Personaje::estaVivo).collect(Collectors.toList());
    }

    protected Heroe elegirObjetivoAleatorio(List<Heroe> heroes) {
        List<Heroe> vivos = obtenerHeroesVivos(heroes);
        if (vivos.isEmpty()) return null;
        return vivos.get(random.nextInt(vivos.size()));
    }

    protected Heroe elegirObjetivoMasDebil(List<Heroe> heroes) {
        return obtenerHeroesVivos(heroes).stream()
                .min(Comparator.comparingInt(Personaje::getHpActual))
                .orElse(null);
    }

    public int getRecompensaExp() { return recompensaExp; }
    public int getRecompensaOro() { return recompensaOro; }
}
package modelo;

public class Arquero extends Heroe {
    public Arquero(String nombre) {
        super(nombre, 105, 50, 24, 8, 18, "src/sprites/arquero.png");
        habilidades.add(new Habilidad("Disparo Preciso", 12, 1.7, Habilidad.Tipo.DANIO));
        habilidades.add(new Habilidad("Lluvia de Flechas", 22, 2.0, Habilidad.Tipo.DANIO));
    }
}
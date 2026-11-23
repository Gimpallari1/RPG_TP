package modelo;

public class Mago extends Heroe {
    public Mago(String nombre) {
        super(nombre, 85, 100, 28, 5, 14, "src/sprites/mago.png");
        habilidades.add(new Habilidad("Bola de Fuego", 15, 1.8, Habilidad.Tipo.DANIO));
        habilidades.add(new Habilidad("Tormenta Arcana", 30, 2.5, Habilidad.Tipo.DANIO));
    }
}
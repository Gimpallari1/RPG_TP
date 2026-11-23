package modelo;

public class Guerrero extends Heroe {
    public Guerrero(String nombre) {
        super(nombre, 140, 30, 22, 12, 10, "src/sprites/guerrero.png");
        habilidades.add(new Habilidad("Corte Brutal", 10, 1.6, Habilidad.Tipo.DANIO));
        habilidades.add(new Habilidad("Embate Sísmico", 18, 2.1, Habilidad.Tipo.DANIO));
    }
}
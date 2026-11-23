package modelo;

public class Curador extends Heroe {
    public Curador(String nombre) {
        super(nombre, 95, 90, 15, 9, 13, "src/sprites/curador.png");
        habilidades.add(new Habilidad("Luz Sanadora", 15, 2.2, Habilidad.Tipo.CURACION));
        habilidades.add(new Habilidad("Castigo Divino", 14, 1.5, Habilidad.Tipo.DANIO));
    }
}
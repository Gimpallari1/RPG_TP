package servicio;

import modelo.*;

import java.util.ArrayList;
import java.util.List;

public class FabricaEnemigos {

    public List<Enemigo> generarEncuentro(int nivelPromedioParty, int numeroNodo, boolean esJefe) {
        List<Enemigo> enemigos = new ArrayList<>();
        int nivelBase = Math.max(1, nivelPromedioParty);

        if (esJefe) {
            enemigos.add(new DragonFuego(nivelBase + 3));
            enemigos.add(new Orco(nivelBase + 2));
            enemigos.add(new Esqueleto(nivelBase + 2));
            return enemigos;
        }

        switch (numeroNodo) {
            case 1:
                enemigos.add(new Goblin(nivelBase));
                enemigos.add(new Goblin(nivelBase));
                break;
            case 2:
                enemigos.add(new Goblin(nivelBase + 1));
                enemigos.add(new Esqueleto(nivelBase + 1));
                enemigos.add(new Goblin(nivelBase + 1));
                break;
            case 3:
                enemigos.add(new Esqueleto(nivelBase + 1));
                enemigos.add(new Orco(nivelBase + 1));
                enemigos.add(new Esqueleto(nivelBase + 1));
                break;
            case 5:
                enemigos.add(new Orco(nivelBase + 2));
                enemigos.add(new Esqueleto(nivelBase + 2));
                enemigos.add(new Orco(nivelBase + 2));
                enemigos.add(new Esqueleto(nivelBase + 2));
                break;
            default:
                enemigos.add(new Goblin(nivelBase + 1));
                enemigos.add(new Orco(nivelBase + 1));
                enemigos.add(new Esqueleto(nivelBase + 1));
                break;
        }
        return enemigos;
    }
}
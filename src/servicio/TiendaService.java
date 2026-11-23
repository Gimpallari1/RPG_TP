package servicio;

import excepciones.FondosInsuficientesException;
import modelo.Consumible;
import modelo.Equipable;
import modelo.Item;
import java.util.ArrayList;
import java.util.List;

public class TiendaService {
    private List<Item> catalogo;

    public TiendaService() {
        catalogo = new ArrayList<>();
        catalogo.add(new Consumible("Poción de Vida", "Restaura 50 HP", 30, Consumible.Efecto.CURAR_HP, 50));
        catalogo.add(new Consumible("Éter Mágico", "Restaura 40 MP", 35, Consumible.Efecto.RESTAURAR_MP, 40));
        catalogo.add(new Consumible("Pluma de Fénix", "Revive con 50% HP", 80, Consumible.Efecto.REVIVIR, 50));
        catalogo.add(new Equipable("Espada de Acero", "+8 Ataque", 75, Equipable.Ranura.ARMA, 8, 0, 0));
        catalogo.add(new Equipable("Báculo Rúnico", "+10 Ataque, +2 Vel", 95, Equipable.Ranura.ARMA, 10, 0, 2));
        catalogo.add(new Equipable("Cota de Malla", "+7 Defensa, -1 Vel", 70, Equipable.Ranura.ARMADURA, 0, 7, -1));
        catalogo.add(new Equipable("Botas Aladas", "+6 Velocidad", 65, Equipable.Ranura.ACCESORIO, 0, 0, 6));
    }

    public List<Item> getCatalogo() {
        return catalogo;
    }

    public Item comprar(int indice, int oroDisponible) throws FondosInsuficientesException {
        Item seleccionado = catalogo.get(indice);
        if (oroDisponible < seleccionado.getPrecio()) {
            throw new FondosInsuficientesException(seleccionado.getPrecio(), oroDisponible);
        }
        return seleccionado;
    }
}
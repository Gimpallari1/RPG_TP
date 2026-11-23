package controlador;

import excepciones.AccionInvalidaException;
import excepciones.FondosInsuficientesException;
import excepciones.JuegoException;
import excepciones.ManaInsuficienteException;
import modelo.*;
import servicio.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JuegoController {
    private List<Heroe> party;
    private List<Item> inventario;
    private List<NodoMapa> mapa;
    private int nodoActualIndex;
    private int oro;

    private final CombateManager combateManager;
    private final TiendaService tiendaService;
    private final FabricaEnemigos fabricaEnemigos;
    private final PersistenciaService persistenciaService;

    public JuegoController() {
        this.combateManager = new CombateManager();
        this.tiendaService = new TiendaService();
        this.fabricaEnemigos = new FabricaEnemigos();
        this.persistenciaService = new PersistenciaService();
        iniciarNuevaPartida();
    }

    public void iniciarNuevaPartida() {
        party = new ArrayList<>();
        party.add(new Guerrero("Aldric"));
        party.add(new Mago("Lyra"));
        party.add(new Arquero("Kael"));
        party.add(new Curador("Elena"));

        inventario = new ArrayList<>();
        inventario.add(new Consumible("Poción de Vida", "Restaura 50 HP", 30, Consumible.Efecto.CURAR_HP, 50));
        inventario.add(new Consumible("Poción de Vida", "Restaura 50 HP", 30, Consumible.Efecto.CURAR_HP, 50));
        inventario.add(new Consumible("Éter Mágico", "Restaura 40 MP", 35, Consumible.Efecto.RESTAURAR_MP, 40));
        inventario.add(new Consumible("Pluma de Fénix", "Revive con 50% HP", 80, Consumible.Efecto.REVIVIR, 50));
        inventario.add(new Equipable("Daga Veloz", "+4 Ataque, +3 Vel", 50, Equipable.Ranura.ARMA, 4, 0, 3));

        ordenarInventario();

        mapa = new ArrayList<>();
        mapa.add(new NodoMapa(1, "Bosque Sombrío", NodoMapa.Tipo.COMBATE));
        mapa.add(new NodoMapa(2, "Pantano Maldito", NodoMapa.Tipo.COMBATE));
        mapa.add(new NodoMapa(3, "Ruinas Antiguas", NodoMapa.Tipo.COMBATE));
        mapa.add(new NodoMapa(4, "Fogata del Viajero", NodoMapa.Tipo.DESCANSO));
        mapa.add(new NodoMapa(5, "Paso de la Montaña", NodoMapa.Tipo.COMBATE));
        mapa.add(new NodoMapa(6, "Cráter del Dragón", NodoMapa.Tipo.JEFE));

        nodoActualIndex = 0;
        oro = 100;
    }

    public void ordenarInventario() {
        inventario.sort(Comparator.comparing((Item i) -> i.getClass().getSimpleName())
                .thenComparing(Item::getNombre));
    }

    public String entrarNodoActual() throws AccionInvalidaException {
        if (nodoActualIndex >= mapa.size()) {
            throw new AccionInvalidaException("¡Ya completaste todos los nodos del mapa!");
        }
        NodoMapa nodo = mapa.get(nodoActualIndex);
        switch (nodo.getTipo()) {
            case COMBATE:
            case JEFE:
                int nivelProm = (int) party.stream().mapToInt(Personaje::getNivel).average().orElse(1);
                List<Enemigo> enemigos = fabricaEnemigos.generarEncuentro(nivelProm, nodo.getNumero(), nodo.getTipo() == NodoMapa.Tipo.JEFE);
                combateManager.iniciarCombate(party, enemigos);
                return "COMBATE";
            case DESCANSO:
                for (Heroe h : party) {
                    if (h.estaVivo()) h.descansoCompleto();
                }
                completarNodoActual();
                return "DESCANSO";
            default:
                throw new AccionInvalidaException("Tipo de nodo inválido.");
        }
    }

    public void completarNodoActual() {
        if (nodoActualIndex < mapa.size()) {
            mapa.get(nodoActualIndex).setCompletado(true);
            nodoActualIndex++;
        }
    }

    public boolean avanzarBarrasCombate() {
        return combateManager.tickBarrasTurno();
    }

    public void huirDeCombate() {
        for (Heroe h : party) {
            h.reiniciarBarraTurno();
            h.reiniciarEstadoTurno();
        }
    }

    public String atacarEnCombate(int indiceEnemigo) throws AccionInvalidaException {
        Heroe actual = getHeroeTurnoActual();
        List<Enemigo> enemigos = combateManager.getEnemigos();
        if (indiceEnemigo < 0 || indiceEnemigo >= enemigos.size()) {
            throw new AccionInvalidaException("Selecciona un enemigo válido.");
        }
        return combateManager.ejecutarAtaqueBasico(actual, enemigos.get(indiceEnemigo));
    }

    public String defenderEnCombate() throws AccionInvalidaException {
        Heroe actual = getHeroeTurnoActual();
        return combateManager.ejecutarDefender(actual);
    }

    public String usarHabilidadEnCombate(int indiceHabilidad, Personaje objetivo)
            throws AccionInvalidaException, ManaInsuficienteException {
        Heroe actual = getHeroeTurnoActual();
        if (indiceHabilidad < 0 || indiceHabilidad >= actual.getHabilidades().size()) {
            throw new AccionInvalidaException("Habilidad inválida.");
        }
        Habilidad hab = actual.getHabilidades().get(indiceHabilidad);
        return combateManager.ejecutarHabilidad(actual, hab, objetivo);
    }

    public String usarOEquiparItem(int indiceItem, Heroe objetivo) throws AccionInvalidaException {
        if (indiceItem < 0 || indiceItem >= inventario.size()) {
            throw new AccionInvalidaException("Selecciona un ítem válido.");
        }
        Item item = inventario.get(indiceItem);

        if (item instanceof Consumible) {
            String msg = ((Consumible) item).aplicar(objetivo);
            inventario.remove(indiceItem);
            if (!combateManager.combateTerminado() && combateManager.getCombatienteActual() instanceof Heroe) {
                combateManager.finalizarTurnoActual();
            }
            return msg;
        } else if (item instanceof Equipable) {
            if (!objetivo.estaVivo()) {
                throw new AccionInvalidaException("No puedes equipar a un personaje caído.");
            }
            inventario.remove(indiceItem);
            Equipable anterior = objetivo.equipar((Equipable) item);
            if (anterior != null) {
                inventario.add(anterior);
            }
            ordenarInventario();
            return objetivo.getNombre() + " equipó " + item.getNombre() + ".";
        }
        throw new AccionInvalidaException("Ítem no soportado.");
    }

    public String procesarTurnoEnemigo() throws AccionInvalidaException {
        return combateManager.ejecutarTurnoEnemigo();
    }

    public String finalizarVictoriaCombate() {
        int exp = combateManager.getExpAcumulada();
        int oroGanado = combateManager.getOroAcumulado();
        this.oro += oroGanado;
        StringBuilder sb = new StringBuilder("¡Victoria! Ganaste " + oroGanado + "G y " + exp + " EXP. ");
        for (Heroe h : party) {
            if (h.ganarExperiencia(exp)) {
                sb.append("¡").append(h.getNombre()).append(" subió a nivel ").append(h.getNivel()).append("! ");
            }
        }
        completarNodoActual();
        return sb.toString();
    }

    public void comprarEnTienda(int indiceCatalogo) throws FondosInsuficientesException {
        Item comprado = tiendaService.comprar(indiceCatalogo, oro);
        oro -= comprado.getPrecio();
        inventario.add(comprado);
        ordenarInventario();
    }

    public void guardarPartida() throws JuegoException {
        PersistenciaService.EstadoJuego estado = new PersistenciaService.EstadoJuego();
        estado.party = party;
        estado.inventario = inventario;
        estado.mapa = mapa;
        estado.nodoActualIndex = nodoActualIndex;
        estado.oro = oro;
        persistenciaService.guardar(estado);
    }

    public void cargarPartida() throws JuegoException {
        PersistenciaService.EstadoJuego estado = persistenciaService.cargar();
        this.party = estado.party;
        this.inventario = estado.inventario;
        this.mapa = estado.mapa;
        this.nodoActualIndex = estado.nodoActualIndex;
        this.oro = estado.oro;
    }

    public Heroe getHeroeTurnoActual() throws AccionInvalidaException {
        Personaje p = combateManager.getCombatienteActual();
        if (!(p instanceof Heroe)) {
            throw new AccionInvalidaException("Es el turno del enemigo.");
        }
        return (Heroe) p;
    }

    public Personaje getCombatienteActual() {
        return combateManager.getCombatienteActual();
    }

    public List<Enemigo> getEnemigosCombate() {
        return combateManager.getEnemigos();
    }

    public boolean isCombateTerminado() {
        return combateManager.combateTerminado();
    }

    public boolean isHeroesDerrotados() {
        return combateManager.heroesDerrotados();
    }

    public boolean isEnemigosDerrotados() {
        return combateManager.enemigosDerrotados();
    }

    public List<Heroe> getParty() { return party; }
    public List<Item> getInventario() { return inventario; }
    public List<NodoMapa> getMapa() { return mapa; }
    public int getNodoActualIndex() { return nodoActualIndex; }
    public int getOro() { return oro; }
    public List<Item> getCatalogoTienda() { return tiendaService.getCatalogo(); }
}
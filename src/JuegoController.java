import java.io.*;
import java.util.*;

public class JuegoController {
    private Partida partida;
    private int ultimaExpGanada;
    private int ultimoOroGanado;

    public JuegoController() {
        this.partida = new Partida();
        generarEnemigo();
    }

    public void reiniciarPartida() {
        this.partida = new Partida();
        generarEnemigo();
    }

    private int obtenerNivelPromedioParty() {
        if (partida.getParty().isEmpty()) return 1;
        int sumaNiveles = 0;
        for (Personaje p : partida.getParty()) {
            sumaNiveles += p.getNivel();
        }
        return Math.max(1, sumaNiveles / partida.getParty().size());
    }

    public void generarEnemigo() {
        NodoMapa actual = partida.getNodoActual();
        int nivelParty = obtenerNivelPromedioParty();
        int nivelMapa = (actual != null) ? actual.getNivel() : 1;
        
        Enemigo nuevoEnemigo;
        
        if (actual != null && actual.getTipo().equals("COMBATE_JEFE")) {
            int vidaJefe = 300 + (nivelParty * 80);
            int ataqueJefe = 30 + (nivelParty * 8);
            int defensaJefe = 15 + (nivelParty * 4);
            
            nuevoEnemigo = new Enemigo(
                "Dragón de Fuego (JEFE)", 
                vidaJefe, 
                ataqueJefe, 
                defensaJefe, 
                15 + nivelParty, 
                300 * nivelParty, 
                200 * nivelParty, 
                "src/sprites/dragon.png", 
                "AREA"
            );
        } else {
            String[] nombres = {"Goblin Astuto", "Orc Cazador", "Troll de las Cavernas"};
            String[] sprites = {
                "src/sprites/goblin.png",
                "src/sprites/orc.png",
                "src/sprites/troll.png"
            };
            String[] ias = {"ALEATORIO", "MAS_DEBIL", "ALEATORIO"};
            
            Random r = new Random();
            int idx = r.nextInt(nombres.length);

            int multiplicadorDificultad = nivelParty + nivelMapa - 1;
            int vidaEnemigo = 90 + (idx * 30) + (multiplicadorDificultad * 40);
            int ataqueEnemigo = 12 + (idx * 5) + (multiplicadorDificultad * 6);
            int defensaEnemigo = 4 + (idx * 2) + (multiplicadorDificultad * 3);

            nuevoEnemigo = new Enemigo(
                nombres[idx], 
                vidaEnemigo, 
                ataqueEnemigo, 
                defensaEnemigo, 
                10 + idx * 2, 
                50 * multiplicadorDificultad,
                30 * multiplicadorDificultad,
                sprites[idx], 
                ias[idx]
            );
        }
        partida.setEnemigoActual(nuevoEnemigo);
        partida.setBatallaFinalizada(false);
    }

    public String ejecutarAtaque(Personaje p) {
        if (partida.isBatallaFinalizada()) return "La batalla ya ha terminado.";
        if (!p.estaVivo()) return p.getNombre() + " está debilitado y no puede actuar.";
        
        partida.getEnemigoActual().recibirDanio(p.getAtaque());
        String res = p.getNombre() + " atacó a " + partida.getEnemigoActual().getNombre() + " causando " + p.getAtaque() + " de daño.\n";
        res += procesarTurnoEnemigo();
        return res;
    }

    public String ejecutarHabilidad(Personaje p) {
        if (partida.isBatallaFinalizada()) return "La batalla ya ha terminado.";
        if (!p.estaVivo()) return p.getNombre() + " está debilitado y no puede actuar.";
        
        String res = p.usaraHabilidad(partida.getEnemigoActual()) + "\n";
        res += procesarTurnoEnemigo();
        return res;
    }

    public String usarItem(Personaje usuario, Personaje objetivo, Item item) {
        if (partida.isBatallaFinalizada()) return "La batalla ya ha terminado.";
        if (!usuario.estaVivo()) return usuario.getNombre() + " está debilitado y no puede actuar.";
        if (item == null) return "No hay ítems disponibles.";

        boolean exito = item.aplicarA(objetivo);
        if (!exito) {
            if (item.getTipo().equals("Revivir")) {
                return "No se puede usar en " + objetivo.getNombre() + " porque ya está vivo.";
            } else {
                return "No se puede usar en " + objetivo.getNombre() + " porque está debilitado.";
            }
        }

        partida.getInventario().remove(item);
        String res = usuario.getNombre() + " usó " + item.getNombre() + " en " + objetivo.getNombre() + ".\n";
        res += procesarTurnoEnemigo();
        return res;
    }

    public boolean comprarItem(Item item) {
        if (partida.restarOro(item.getPrecio())) {
            partida.getInventario().add(item);
            return true;
        }
        return false;
    }

    public void avanzarNivelMapa() {
        partida.setNivelActual(partida.getNivelActual() + 1);
    }

    private String procesarTurnoEnemigo() {
        Enemigo enemigo = partida.getEnemigoActual();

        if (!enemigo.estaVivo()) {
            partida.setBatallaFinalizada(true);
            
            if (partida.getNodoActual() != null) {
                partida.getNodoActual().setCompletado(true);
            }

            avanzarNivelMapa();
            
            ultimaExpGanada = enemigo.getExpRecompensa();
            ultimoOroGanado = enemigo.getOroRecompensa();
            
            partida.sumarOro(ultimoOroGanado);
            for (Personaje p : partida.getParty()) {
                p.ganarExperiencia(ultimaExpGanada);
            }
            
            if (esJefeFinal()) {
                partida.setResultadoUltimaBatalla(generarResumenFinalPartida());
            } else {
                partida.setResultadoUltimaBatalla("¡VICTORIA!\nEl enemigo " + enemigo.getNombre() + " ha sido derrotado.\nGanaste: " + ultimaExpGanada + " EXP y " + ultimoOroGanado + " de Oro.");
            }
            return "¡Enemigo derrotado!";
        }

        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : partida.getParty()) {
            if (p.estaVivo()) {
                vivos.add(p);
            }
        }

        if (vivos.isEmpty()) {
            partida.setBatallaFinalizada(true);
            partida.setResultadoUltimaBatalla("¡DERROTA!\nToda tu party ha sido eliminada en combate.\n¿Deseas intentar de nuevo la aventura?");
            return "¡Toda la party ha sido derrotada!";
        }

        Random rand = new Random();
        String tipoIA = enemigo.getTipoIA();

        if (tipoIA.equals("AREA")) {
            int danioArea = Math.max(1, enemigo.getAtaque() / 2);
            for (Personaje p : vivos) {
                p.recibirDanio(danioArea);
            }
            
            if (esPartyDerrotada()) {
                partida.setBatallaFinalizada(true);
                partida.setResultadoUltimaBatalla("¡DERROTA!\nToda tu party ha sido eliminada en combate.\n¿Deseas intentar de nuevo la aventura?");
            }
            return enemigo.getNombre() + " usó ATAQUE EN ÁREA afectando a toda la party (" + danioArea + " de daño a cada uno).";

        } else if (tipoIA.equals("MAS_DEBIL")) {
            Personaje masDebil = vivos.get(0);
            for (Personaje p : vivos) {
                if (p.getVidaActual() < masDebil.getVidaActual()) {
                    masDebil = p;
                }
            }
            masDebil.recibirDanio(enemigo.getAtaque());
            
            if (esPartyDerrotada()) {
                partida.setBatallaFinalizada(true);
                partida.setResultadoUltimaBatalla("¡DERROTA!\nToda tu party ha sido eliminada en combate.\n¿Deseas intentar de nuevo la aventura?");
            }
            return enemigo.getNombre() + " emboscó al más débil: " + masDebil.getNombre() + " infligiendo " + enemigo.getAtaque() + " de daño.";

        } else {
            Personaje objetivo = vivos.get(rand.nextInt(vivos.size()));
            objetivo.recibirDanio(enemigo.getAtaque());
            
            if (esPartyDerrotada()) {
                partida.setBatallaFinalizada(true);
                partida.setResultadoUltimaBatalla("¡DERROTA!\nToda tu party ha sido eliminada en combate.\n¿Deseas intentar de nuevo la aventura?");
            }
            return enemigo.getNombre() + " atacó al azar a " + objetivo.getNombre() + " causando " + enemigo.getAtaque() + " de daño.";
        }
    }

    public boolean esPartyDerrotada() {
        for (Personaje p : partida.getParty()) {
            if (p.estaVivo()) return false;
        }
        return true;
    }

    public boolean esJefeFinal() {
        return partida.getNodoActual() != null && partida.getNodoActual().getTipo().equals("COMBATE_JEFE");
    }

    private String generarResumenFinalPartida() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("     ¡VICTORIA FINAL! RESUMEN DE PARTIDA \n");
        sb.append("=========================================\n\n");
        sb.append("¡Has derrotado al Dragón de Fuego y salvado el reino!\n\n");
        
        sb.append("--- ORO TOTAL Y RECURSOS ---\n");
        sb.append("Oro acumulado en la partida: ").append(partida.getOro()).append(" de oro\n");
        sb.append("Ítems restantes en el inventario: ").append(partida.getInventario().size()).append("\n");
        for (Item item : partida.getInventario()) {
            sb.append("  - ").append(item.getNombre()).append("\n");
        }
        sb.append("\n");

        sb.append("--- ESTADO FINAL DE LOS HÉROES ---\n");
        for (Personaje p : partida.getParty()) {
            sb.append("• ").append(p.getNombre()).append(" - Nivel ").append(p.getNivel())
              .append(" | HP: ").append(p.getVidaActual()).append("/").append(p.getVidaMax())
              .append(" | MP: ").append(p.getManaActual()).append("/").append(p.getManaMax())
              .append(" | EXP: ").append(p.getExperiencia()).append("\n");
        }
        return sb.toString();
    }

    public List<NodoMapa> getNodosDisponibles() {
        List<NodoMapa> delNivelActual = new ArrayList<>();
        int nivActual = partida.getNivelActual();

        for (NodoMapa n : partida.getMapa()) {
            if (n.getNivel() == nivActual) {
                delNivelActual.add(n);
            }
        }
        return delNivelActual;
    }

    public void seleccionarNodo(NodoMapa destino) {
        partida.setNodoActual(destino);
        if (destino.getTipo().contains("COMBATE")) {
            generarEnemigo();
        }
    }

    public void guardarPartida(String archivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(partida);
        }
    }

    public void cargarPartida(String archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            this.partida = (Partida) ois.readObject();
        }
    }

    public Partida getPartida() { return partida; }
    public Enemigo getEnemigoActual() { return partida.getEnemigoActual(); }
    public boolean isBatallaFinalizada() { return partida.isBatallaFinalizada(); }
    public String getResultadoUltimaBatalla() { return partida.getResultadoUltimaBatalla(); }
}
package servicio;

import excepciones.AccionInvalidaException;
import excepciones.ManaInsuficienteException;
import modelo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombateManager {
    private List<Heroe> heroes;
    private List<Enemigo> enemigos;
    private Personaje combatienteActual;
    private int expAcumulada;
    private int oroAcumulado;

    public void iniciarCombate(List<Heroe> heroes, List<Enemigo> enemigos) {
        this.heroes = heroes;
        this.enemigos = enemigos;
        this.expAcumulada = enemigos.stream().mapToInt(Enemigo::getRecompensaExp).sum();
        this.oroAcumulado = enemigos.stream().mapToInt(Enemigo::getRecompensaOro).sum();
        this.combatienteActual = null;

        for (Heroe h : heroes) {
            h.reiniciarBarraTurno();
            h.reiniciarEstadoTurno();
        }
        for (Enemigo e : enemigos) {
            e.reiniciarBarraTurno();
            e.reiniciarEstadoTurno();
        }
    }

    /**
     * Avanza un paso las barras de turno según la velocidad de cada personaje.
     * Devuelve true si algún personaje llenó su barra al 100% y obtuvo el turno.
     */
    public boolean tickBarrasTurno() {
        if (combateTerminado() || combatienteActual != null) {
            return combatienteActual != null;
        }

        List<Personaje> vivos = new ArrayList<>();
        for (Heroe h : heroes) if (h.estaVivo()) vivos.add(h);
        for (Enemigo e : enemigos) if (e.estaVivo()) vivos.add(e);

        if (vivos.isEmpty()) return false;

        // Cada tick suma velocidad * 0.45 para que la barra se vea avanzar fluidamente
        for (Personaje p : vivos) {
            p.avanzarBarraTurno(0.45);
        }

        List<Personaje> listos = new ArrayList<>();
        for (Personaje p : vivos) {
            if (p.barraTurnoLlena()) {
                listos.add(p);
            }
        }

        if (!listos.isEmpty()) {
            Collections.sort(listos); // Desempata por mayor velocidad usando Comparable
            combatienteActual = listos.get(0);
            combatienteActual.reiniciarEstadoTurno();
            return true;
        }
        return false;
    }

    public void finalizarTurnoActual() {
        if (combatienteActual != null) {
            combatienteActual.reiniciarBarraTurno();
            combatienteActual = null;
        }
    }

    private void validarTurnoHeroe(Heroe actor) throws AccionInvalidaException {
        if (combatienteActual == null || combatienteActual != actor) {
            throw new AccionInvalidaException("No es el turno de " + actor.getNombre() + ".");
        }
        if (!actor.estaVivo()) {
            throw new AccionInvalidaException(actor.getNombre() + " está incapacitado.");
        }
    }

    public String ejecutarAtaqueBasico(Heroe atacante, Enemigo objetivo) throws AccionInvalidaException {
        validarTurnoHeroe(atacante);
        if (objetivo == null || !objetivo.estaVivo()) {
            throw new AccionInvalidaException("Debes seleccionar un enemigo vivo.");
        }
        int danioReal = objetivo.recibirDanio(atacante.getAtaqueTotal());
        finalizarTurnoActual();
        return atacante.getNombre() + " ataca a " + objetivo.getNombre() + " causando " + danioReal + " de daño real.";
    }

    public String ejecutarDefender(Heroe actor) throws AccionInvalidaException {
        validarTurnoHeroe(actor);
        actor.activarDefensa();
        finalizarTurnoActual();
        return actor.getNombre() + " adopta postura defensiva (Defensa duplicada hasta su próximo turno).";
    }

    public String ejecutarHabilidad(Heroe actor, Habilidad hab, Personaje objetivo)
            throws AccionInvalidaException, ManaInsuficienteException {
        validarTurnoHeroe(actor);
        if (actor.getMpActual() < hab.getCostoMana()) {
            throw new ManaInsuficienteException(actor.getNombre(), hab.getCostoMana(), actor.getMpActual());
        }
        if (objetivo == null || !objetivo.estaVivo()) {
            throw new AccionInvalidaException("Objetivo inválido para la habilidad.");
        }

        actor.consumirMana(hab.getCostoMana());
        String resultado;

        if (hab.getTipo() == Habilidad.Tipo.DANIO) {
            if (!(objetivo instanceof Enemigo)) {
                throw new AccionInvalidaException("Esta habilidad solo puede usarse contra enemigos.");
            }
            int danioBruto = (int) (actor.getAtaqueTotal() * hab.getMultiplicador());
            int danioReal = objetivo.recibirDanio(danioBruto);
            resultado = actor.getNombre() + " usa " + hab.getNombre() + " contra " + objetivo.getNombre()
                    + " y causa " + danioReal + " de daño real.";
        } else {
            if (!(objetivo instanceof Heroe)) {
                throw new AccionInvalidaException("Esta habilidad solo puede usarse sobre aliados.");
            }
            int curacion = (int) (actor.getAtaqueTotal() * hab.getMultiplicador());
            int curadoReal = objetivo.curar(curacion);
            resultado = actor.getNombre() + " lanza " + hab.getNombre() + " sobre " + objetivo.getNombre()
                    + " restaurando " + curadoReal + " HP.";
        }

        finalizarTurnoActual();
        return resultado;
    }

    public String ejecutarTurnoEnemigo() throws AccionInvalidaException {
        if (!(combatienteActual instanceof Enemigo)) {
            throw new AccionInvalidaException("El combatiente actual no es un enemigo.");
        }
        Enemigo enemigo = (Enemigo) combatienteActual;
        String log = enemigo.ejecutarTurno(heroes);
        finalizarTurnoActual();
        return log;
    }

    public boolean combateTerminado() {
        return heroesDerrotados() || enemigosDerrotados();
    }

    public boolean heroesDerrotados() {
        return heroes.stream().noneMatch(Personaje::estaVivo);
    }

    public boolean enemigosDerrotados() {
        return enemigos.stream().noneMatch(Personaje::estaVivo);
    }

    public Personaje getCombatienteActual() { return combatienteActual; }
    public List<Enemigo> getEnemigos() { return enemigos; }
    public int getExpAcumulada() { return expAcumulada; }
    public int getOroAcumulado() { return oroAcumulado; }
}
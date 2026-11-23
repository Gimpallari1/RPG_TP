package vista;

import controlador.JuegoController;
import excepciones.JuegoException;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class VentanaJuego extends JFrame {
    private final JuegoController controller;
    private final CardLayout cardLayout;
    private final JPanel panelContenedor;

    private JPanel panelMapaNodos;
    private JLabel lblInfoPartyMapa;
    private JTextArea areaLogCombate;
    private JPanel panelHeroesCombate;
    private JPanel panelEnemigosCombate;
    private JLabel lblTurnoActual;
    private JButton btnAtacar, btnHabilidad, btnDefender, btnItem, btnGuardarCombate, btnSalirBatalla;
    private Timer timerBarrasTurno;

    public VentanaJuego() {
        this.controller = new JuegoController();
        setTitle("Eclipse Arcana - RPG Táctico");
        setSize(1020, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarMenuSuperior();

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        panelContenedor.add(crearPanelTitulo(), "TITULO");
        panelContenedor.add(crearPanelMapa(), "MAPA");
        panelContenedor.add(crearPanelCombate(), "COMBATE");
        panelContenedor.add(crearPanelDerrota(), "DERROTA");

        add(panelContenedor);
        cardLayout.show(panelContenedor, "TITULO");
    }

    private void configurarMenuSuperior() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Juego");

        JMenuItem itemNuevo = new JMenuItem("Nueva Partida");
        itemNuevo.addActionListener(e -> {
            detenerTimerBarras();
            controller.iniciarNuevaPartida();
            actualizarMapa();
            cardLayout.show(panelContenedor, "MAPA");
        });

        JMenuItem itemGuardar = new JMenuItem("Guardar Partida");
        itemGuardar.addActionListener(e -> guardarPartidaUI());

        JMenuItem itemCargar = new JMenuItem("Cargar Partida");
        itemCargar.addActionListener(e -> {
            try {
                detenerTimerBarras();
                controller.cargarPartida();
                actualizarMapa();
                cardLayout.show(panelContenedor, "MAPA");
                JOptionPane.showMessageDialog(this, "Partida cargada.");
            } catch (JuegoException ex) {
                mostrarError(ex.getMessage());
            }
        });

        JMenuItem itemTienda = new JMenuItem("Abrir Tienda");
        itemTienda.addActionListener(e -> {
            abrirDialogoTienda();
            actualizarMapa();
        });

        JMenuItem itemMenuPrincipal = new JMenuItem("Volver al Menú Principal");
        itemMenuPrincipal.addActionListener(e -> volverAlMenuPrincipal());

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemCargar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemTienda);
        menuArchivo.addSeparator();
        menuArchivo.add(itemMenuPrincipal);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);
    }

    private void volverAlMenuPrincipal() {
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Volver al menú principal? Asegurate de haber guardado tu partida si no querés perder el progreso.",
                "Menú Principal", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            detenerTimerBarras();
            controller.huirDeCombate();
            cardLayout.show(panelContenedor, "TITULO");
        }
    }

    private void guardarPartidaUI() {
        try {
            controller.guardarPartida();
            JOptionPane.showMessageDialog(this, "Partida guardada correctamente.");
        } catch (JuegoException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 15, 30));

        JLabel titulo = new JLabel("ECLIPSE ARCANA", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 52));
        titulo.setForeground(new Color(220, 180, 90));

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        JButton btnJugar = new JButton("Iniciar Aventura");
        btnJugar.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnJugar.addActionListener(e -> {
            controller.iniciarNuevaPartida();
            actualizarMapa();
            cardLayout.show(panelContenedor, "MAPA");
        });

        JButton btnCargar = new JButton("Continuar Partida");
        btnCargar.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnCargar.addActionListener(e -> {
            try {
                controller.cargarPartida();
                actualizarMapa();
                cardLayout.show(panelContenedor, "MAPA");
            } catch (JuegoException ex) {
                mostrarError(ex.getMessage());
            }
        });

        botones.add(btnJugar);
        botones.add(btnCargar);

        panel.add(titulo, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelMapa() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblInfoPartyMapa = new JLabel("", SwingConstants.CENTER);
        lblInfoPartyMapa.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(lblInfoPartyMapa, BorderLayout.NORTH);

        panelMapaNodos = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.add(panelMapaNodos, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton btnTienda = new JButton("Tienda de Equipo");
        btnTienda.addActionListener(e -> {
            abrirDialogoTienda();
            actualizarMapa();
        });

        JButton btnInventario = new JButton("Inventario y Equipo");
        btnInventario.addActionListener(e -> abrirDialogoInventario(false));

        JButton btnGuardarMapa = new JButton("Guardar Partida");
        btnGuardarMapa.addActionListener(e -> guardarPartidaUI());

        JButton btnMenuPrincipal = new JButton("Menú Principal");
        btnMenuPrincipal.addActionListener(e -> volverAlMenuPrincipal());

        panelInferior.add(btnTienda);
        panelInferior.add(btnInventario);
        panelInferior.add(btnGuardarMapa);
        panelInferior.add(btnMenuPrincipal);
        panel.add(panelInferior, BorderLayout.SOUTH);

        return panel;
    }

    private void actualizarMapa() {
        StringBuilder info = new StringBuilder("Oro: " + controller.getOro() + "G  |  ");
        for (Heroe h : controller.getParty()) {
            info.append(h.getNombre()).append(" Nv.").append(h.getNivel())
                .append(" (HP:").append(h.getHpActual()).append("/").append(h.getHpMax())
                .append(" MP:").append(h.getMpActual()).append("/").append(h.getMpMax()).append(")  ");
        }
        lblInfoPartyMapa.setText(info.toString());

        panelMapaNodos.removeAll();
        List<NodoMapa> nodos = controller.getMapa();
        int actual = controller.getNodoActualIndex();

        for (int i = 0; i < nodos.size(); i++) {
            NodoMapa n = nodos.get(i);
            JButton btnNodo = new JButton("<html><center><b>" + n.getNumero() + ". " + n.getNombre() + "</b><br>[" + n.getTipo() + "]</center></html>");
            btnNodo.setEnabled(i == actual);
            if (n.isCompletado()) {
                btnNodo.setBackground(new Color(180, 230, 180));
            }
            btnNodo.addActionListener(e -> ejecutarNodoActual());
            panelMapaNodos.add(btnNodo);
        }
        panelMapaNodos.revalidate();
        panelMapaNodos.repaint();
    }

    private void ejecutarNodoActual() {
        try {
            String resultado = controller.entrarNodoActual();
            switch (resultado) {
                case "COMBATE":
                    areaLogCombate.setText("¡Comienza el combate! Las barras de turno se llenan según la velocidad.\n");
                    actualizarVistaCombate();
                    cardLayout.show(panelContenedor, "COMBATE");
                    iniciarCicloBarrasTurno();
                    break;
                case "DESCANSO":
                    JOptionPane.showMessageDialog(this, "La party descansó en la fogata y recuperó todo su HP y MP.");
                    actualizarMapa();
                    break;
            }
        } catch (JuegoException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private JPanel crearPanelCombate() {
        PanelFondoBatalla panel = new PanelFondoBatalla(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTurnoActual = new JLabel("Cargando barras de iniciativa...", SwingConstants.CENTER);
        lblTurnoActual.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTurnoActual.setForeground(Color.WHITE);
        panel.add(lblTurnoActual, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 10));
        centro.setOpaque(false);
        panelHeroesCombate = new JPanel(new GridLayout(4, 1, 8, 8));
        panelHeroesCombate.setOpaque(false);
        panelEnemigosCombate = new JPanel(new GridLayout(4, 1, 8, 8));
        panelEnemigosCombate.setOpaque(false);

        centro.add(panelHeroesCombate);
        centro.add(panelEnemigosCombate);
        panel.add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(5, 5));
        areaLogCombate = new JTextArea(6, 40);
        areaLogCombate.setEditable(false);
        sur.add(new JScrollPane(areaLogCombate), BorderLayout.CENTER);

        JPanel botonesAccion = new JPanel(new GridLayout(1, 6, 8, 5));
        btnAtacar = new JButton("Atacar");
        btnHabilidad = new JButton("Habilidad");
        btnDefender = new JButton("Defender");
        btnItem = new JButton("Ítem / Equipo");
        btnGuardarCombate = new JButton("Guardar");
        btnSalirBatalla = new JButton("Salir Batalla");

        btnAtacar.addActionListener(e -> accionAtacar());
        btnHabilidad.addActionListener(e -> accionHabilidad());
        btnDefender.addActionListener(e -> accionDefender());
        btnItem.addActionListener(e -> abrirDialogoInventario(true));
        btnGuardarCombate.addActionListener(e -> guardarPartidaUI());
        btnSalirBatalla.addActionListener(e -> accionSalirBatalla());

        botonesAccion.add(btnAtacar);
        botonesAccion.add(btnHabilidad);
        botonesAccion.add(btnDefender);
        botonesAccion.add(btnItem);
        botonesAccion.add(btnGuardarCombate);
        botonesAccion.add(btnSalirBatalla);
        sur.add(botonesAccion, BorderLayout.SOUTH);

        panel.add(sur, BorderLayout.SOUTH);
        return panel;
    }

    private void iniciarCicloBarrasTurno() {
        detenerTimerBarras();
        timerBarrasTurno = new Timer(60, e -> {
            if (controller.isCombateTerminado()) {
                detenerTimerBarras();
                verificarFinCombate();
                return;
            }

            boolean alguienListo = controller.avanzarBarrasCombate();
            actualizarVistaCombate();

            if (alguienListo) {
                detenerTimerBarras();
                Personaje actual = controller.getCombatienteActual();
                if (actual instanceof Enemigo) {
                    ejecutarTurnoEnemigoConPausa();
                }
            }
        });
        timerBarrasTurno.start();
    }

    private void detenerTimerBarras() {
        if (timerBarrasTurno != null && timerBarrasTurno.isRunning()) {
            timerBarrasTurno.stop();
        }
    }

    private void ejecutarTurnoEnemigoConPausa() {
        Timer pausaEnemigo = new Timer(650, e -> {
            if (!controller.isCombateTerminado() && controller.getCombatienteActual() instanceof Enemigo) {
                try {
                    String log = controller.procesarTurnoEnemigo();
                    areaLogCombate.append(log + "\n");
                    actualizarVistaCombate();
                    if (!verificarFinCombate()) {
                        iniciarCicloBarrasTurno();
                    }
                } catch (JuegoException ex) {
                    mostrarError(ex.getMessage());
                }
            }
        });
        pausaEnemigo.setRepeats(false);
        pausaEnemigo.start();
    }

    private ImageIcon cargarSprite(String ruta, int ancho, int alto) {
        if (ruta == null) return null;
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            java.net.URL url = getClass().getClassLoader().getResource(ruta.replace("src/", ""));
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
            return null;
        }
        Image img = new ImageIcon(ruta).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void actualizarVistaCombate() {
        Personaje actual = controller.getCombatienteActual();
        if (actual != null) {
            int velActual = (actual instanceof Heroe) ? ((Heroe) actual).getVelocidadTotal() : actual.getVelocidad();
            lblTurnoActual.setText("¡TURNO DE: " + actual.getNombre().toUpperCase() + "! (Vel: " + velActual + ")");
        } else {
            lblTurnoActual.setText("Llenando barras de turno según velocidad...");
        }

        panelHeroesCombate.removeAll();
        for (Heroe h : controller.getParty()) {
            panelHeroesCombate.add(crearTarjetaCombatiente(
                    h.getNombre(), h.getNivel(), h.isDefendiendo(),
                    h.getHpActual(), h.getHpMax(), h.getMpActual(), h.getMpMax(),
                    h.getVelocidadTotal(), h.getPorcentajeBarraTurno(),
                    h.getSpritePath(), h == actual, true
            ));
        }

        panelEnemigosCombate.removeAll();
        for (Enemigo e : controller.getEnemigosCombate()) {
            panelEnemigosCombate.add(crearTarjetaCombatiente(
                    e.getNombre(), e.getNivel(), e.isDefendiendo(),
                    e.getHpActual(), e.getHpMax(), -1, -1,
                    e.getVelocidad(), e.getPorcentajeBarraTurno(),
                    e.getSpritePath(), e == actual, false
            ));
        }

        boolean turnoHeroe = actual instanceof Heroe;
        btnAtacar.setEnabled(turnoHeroe);
        btnHabilidad.setEnabled(turnoHeroe);
        btnDefender.setEnabled(turnoHeroe);
        btnItem.setEnabled(turnoHeroe);

        panelHeroesCombate.revalidate();
        panelHeroesCombate.repaint();
        panelEnemigosCombate.revalidate();
        panelEnemigosCombate.repaint();
    }

    private JPanel crearTarjetaCombatiente(String nombre, int nivel, boolean defendiendo,
                                           int hp, int hpMax, int mp, int mpMax,
                                           int vel, int porcentajeBarra, String spritePath,
                                           boolean esTurnoActual, boolean esHeroe) {
        JPanel tarjeta = new JPanel(new BorderLayout(8, 2));
        tarjeta.setOpaque(true);

        if (esTurnoActual) {
            tarjeta.setBackground(esHeroe ? new Color(255, 248, 175) : new Color(255, 190, 190));
        } else {
            tarjeta.setBackground(esHeroe ? new Color(235, 240, 250, 235) : new Color(245, 225, 225, 235));
        }

        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(esTurnoActual ? (esHeroe ? new Color(220, 150, 0) : Color.RED) : Color.DARK_GRAY, esTurnoActual ? 2 : 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        String estadoDef = defendiendo ? " [DEF]" : "";
        String lineaMp = (mp >= 0) ? String.format(" | <b>MP: %d/%d</b>", mp, mpMax) : "";
        String texto = String.format("<html><b>%s</b> (Nv.%d)%s<br>HP: %d/%d%s | Vel: %d</html>",
                nombre, nivel, estadoDef, hp, hpMax, lineaMp, vel);

        JLabel lblInfo = new JLabel(texto);
        ImageIcon icono = cargarSprite(spritePath, esHeroe ? 56 : 64, esHeroe ? 56 : 64);
        if (icono != null) {
            lblInfo.setIcon(icono);
            lblInfo.setIconTextGap(10);
        }

        JProgressBar barraTurno = new JProgressBar(0, 100);
        barraTurno.setValue(porcentajeBarra);
        barraTurno.setStringPainted(true);
        barraTurno.setString(porcentajeBarra >= 100 ? "¡LISTO!" : "Turno: " + porcentajeBarra + "%");
        barraTurno.setPreferredSize(new Dimension(100, 16));
        barraTurno.setForeground(porcentajeBarra >= 100 ? new Color(40, 170, 60) : new Color(50, 120, 210));

        tarjeta.add(lblInfo, BorderLayout.CENTER);
        tarjeta.add(barraTurno, BorderLayout.SOUTH);
        return tarjeta;
    }

    private void accionSalirBatalla() {
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Seguro que querés salir de la batalla y volver al mapa?",
                "Salir de la batalla", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            detenerTimerBarras();
            controller.huirDeCombate();
            actualizarMapa();
            cardLayout.show(panelContenedor, "MAPA");
        }
    }

    private void accionAtacar() {
        List<Enemigo> enemigos = controller.getEnemigosCombate();
        String[] nombres = enemigos.stream()
                .map(e -> e.getNombre() + " (HP: " + e.getHpActual() + "/" + e.getHpMax() + ")")
                .toArray(String[]::new);

        int sel = JOptionPane.showOptionDialog(this, "Selecciona el objetivo:", "Atacar",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
        if (sel >= 0) {
            try {
                String log = controller.atacarEnCombate(sel);
                areaLogCombate.append(log + "\n");
                procesarPostAccionCombate();
            } catch (JuegoException ex) {
                mostrarError(ex.getMessage());
            }
        }
    }

    private void accionDefender() {
        try {
            String log = controller.defenderEnCombate();
            areaLogCombate.append(log + "\n");
            procesarPostAccionCombate();
        } catch (JuegoException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void accionHabilidad() {
        try {
            Heroe actual = controller.getHeroeTurnoActual();
            List<Habilidad> habs = actual.getHabilidades();
            String[] opciones = habs.stream()
                    .map(h -> h.getNombre() + " [Costo: " + h.getCostoMana() + " MP]")
                    .toArray(String[]::new);

            int selHab = JOptionPane.showOptionDialog(this,
                    actual.getNombre() + " (MP disponible: " + actual.getMpActual() + "/" + actual.getMpMax() + ")\nElige habilidad:",
                    "Habilidades", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);

            if (selHab < 0) return;
            Habilidad elegida = habs.get(selHab);

            Personaje objetivo;
            if (elegida.getTipo() == Habilidad.Tipo.DANIO) {
                List<Enemigo> enemigos = controller.getEnemigosCombate();
                String[] nomEnem = enemigos.stream().map(e -> e.getNombre() + " (" + e.getHpActual() + " HP)").toArray(String[]::new);
                int selObj = JOptionPane.showOptionDialog(this, "Objetivo enemigo:", "Habilidad",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomEnem, nomEnem[0]);
                if (selObj < 0) return;
                objetivo = enemigos.get(selObj);
            } else {
                List<Heroe> heroes = controller.getParty();
                String[] nomHer = heroes.stream().map(h -> h.getNombre() + " (" + h.getHpActual() + " HP)").toArray(String[]::new);
                int selObj = JOptionPane.showOptionDialog(this, "Objetivo aliado:", "Habilidad",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomHer, nomHer[0]);
                if (selObj < 0) return;
                objetivo = heroes.get(selObj);
            }

            String log = controller.usarHabilidadEnCombate(selHab, objetivo);
            areaLogCombate.append(log + "\n");
            procesarPostAccionCombate();
        } catch (JuegoException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void procesarPostAccionCombate() {
        actualizarVistaCombate();
        if (verificarFinCombate()) return;
        iniciarCicloBarrasTurno();
    }

    private boolean verificarFinCombate() {
        if (controller.isHeroesDerrotados()) {
            detenerTimerBarras();
            cardLayout.show(panelContenedor, "DERROTA");
            return true;
        }
        if (controller.isEnemigosDerrotados()) {
            detenerTimerBarras();
            String msg = controller.finalizarVictoriaCombate();
            JOptionPane.showMessageDialog(this, msg);
            if (controller.getNodoActualIndex() >= controller.getMapa().size()) {
                JOptionPane.showMessageDialog(this, "¡FELICITACIONES! Completaste Eclipse Arcana.");
                cardLayout.show(panelContenedor, "TITULO");
            } else {
                actualizarMapa();
                cardLayout.show(panelContenedor, "MAPA");
            }
            return true;
        }
        return false;
    }

    private void abrirDialogoInventario(boolean enCombate) {
        List<Item> inv = controller.getInventario();
        if (inv.isEmpty()) {
            mostrarError("El inventario está vacío.");
            return;
        }
        String[] itemsStr = inv.stream().map(Item::toString).toArray(String[]::new);
        int selItem = JOptionPane.showOptionDialog(this, "Selecciona un ítem o equipo:", "Inventario",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, itemsStr, itemsStr[0]);
        if (selItem < 0) return;

        List<Heroe> heroes = controller.getParty();
        String[] heroesStr = heroes.stream().map(h -> h.getNombre() + " (HP:" + h.getHpActual() + ")").toArray(String[]::new);
        int selHeroe = JOptionPane.showOptionDialog(this, "¿Sobre qué héroe aplicarlo?", "Objetivo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, heroesStr, heroesStr[0]);
        if (selHeroe < 0) return;

        try {
            String res = controller.usarOEquiparItem(selItem, heroes.get(selHeroe));
            if (enCombate) {
                areaLogCombate.append(res + "\n");
                procesarPostAccionCombate();
            } else {
                JOptionPane.showMessageDialog(this, res);
                actualizarMapa();
            }
        } catch (JuegoException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void abrirDialogoTienda() {
        JDialog dialogoTienda = new JDialog(this, "Tienda del Mercader", true);
        dialogoTienda.setSize(520, 480);
        dialogoTienda.setLocationRelativeTo(this);
        dialogoTienda.setLayout(new BorderLayout(10, 10));

        JPanel panelCabecera = new JPanel(new BorderLayout());
        panelCabecera.setBackground(new Color(35, 25, 50));
        panelCabecera.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel lblTitulo = new JLabel("MERCADER DE ECLIPSE ARCANA");
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(235, 195, 100));

        JLabel lblOro = new JLabel("Oro: " + controller.getOro() + "G");
        lblOro.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblOro.setForeground(new Color(255, 215, 0));

        panelCabecera.add(lblTitulo, BorderLayout.WEST);
        panelCabecera.add(lblOro, BorderLayout.EAST);
        dialogoTienda.add(panelCabecera, BorderLayout.NORTH);

        List<Item> catalogo = controller.getCatalogoTienda();
        JPanel panelLista = new JPanel(new GridLayout(catalogo.size(), 1, 8, 8));
        panelLista.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        for (int i = 0; i < catalogo.size(); i++) {
            final int indice = i;
            Item item = catalogo.get(i);
            String tipoEtiqueta = (item instanceof Equipable) ? "[Equipo]" : "[Consumible]";

            JPanel filaItem = new JPanel(new BorderLayout(10, 5));
            filaItem.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 195)),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            filaItem.setBackground(new Color(248, 248, 252));

            JLabel lblDetalle = new JLabel("<html><b>" + item.getNombre() + "</b> <font color='#666666'>" + tipoEtiqueta + "</font><br>"
                    + item.getDescripcion() + "</html>");
            lblDetalle.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JButton btnComprar = new JButton("Comprar (" + item.getPrecio() + "G)");
            btnComprar.setFocusPainted(false);
            btnComprar.setBackground(new Color(230, 240, 230));
            btnComprar.addActionListener(e -> {
                try {
                    controller.comprarEnTienda(indice);
                    lblOro.setText("Oro: " + controller.getOro() + "G");
                    JOptionPane.showMessageDialog(dialogoTienda, "¡Compraste " + item.getNombre() + "!");
                } catch (JuegoException ex) {
                    mostrarError(ex.getMessage());
                }
            });

            filaItem.add(lblDetalle, BorderLayout.CENTER);
            filaItem.add(btnComprar, BorderLayout.EAST);
            panelLista.add(filaItem);
        }

        JScrollPane scrollLista = new JScrollPane(panelLista);
        scrollLista.getVerticalScrollBar().setUnitIncrement(12);
        dialogoTienda.add(scrollLista, BorderLayout.CENTER);

        JPanel panelPie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelPie.setBorder(BorderFactory.createEmptyBorder(4, 12, 8, 12));
        JButton btnCerrar = new JButton("Cerrar Tienda");
        btnCerrar.addActionListener(e -> dialogoTienda.dispose());
        panelPie.add(btnCerrar);
        dialogoTienda.add(panelPie, BorderLayout.SOUTH);

        dialogoTienda.setVisible(true);
    }

    private JPanel crearPanelDerrota() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 10, 10));
        JLabel lbl = new JLabel("HAS SIDO DERROTADO", SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", Font.BOLD, 42));
        lbl.setForeground(Color.RED);

        JButton btnReintentar = new JButton("Volver al Menú Principal");
        btnReintentar.addActionListener(e -> cardLayout.show(panelContenedor, "TITULO"));

        panel.add(lbl, BorderLayout.CENTER);
        panel.add(btnReintentar, BorderLayout.SOUTH);
        return panel;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private static class PanelFondoBatalla extends JPanel {
        private Image imagenFondo;

        public PanelFondoBatalla(LayoutManager layout) {
            super(layout);
            imagenFondo = new ImageIcon("src/sprites/fondo_batalla.png").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagenFondo != null && imagenFondo.getWidth(null) > 0) {
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            } else {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 30, 50), 0, getHeight(), new Color(15, 10, 20));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}
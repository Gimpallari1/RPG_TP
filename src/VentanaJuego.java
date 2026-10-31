import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaJuego extends JFrame {
    private JuegoController controller;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private JTextArea txtLog;
    private JLabel lblEnemigoSprite, lblEnemigoNombre;
    private JPanel pnlPartySprites;
    private JComboBox<String> cbPersonajes;

    private JTextArea txtResultados;
    private JLabel lblTituloResultados;
    private JButton btnBotonResultado;

    public VentanaJuego() {
        controller = new JuegoController();
        setTitle("Eclipse Arcana - RPG por Turnos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Barra de menú superior
        setJMenuBar(crearBarraMenu());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(crearPantallaInicio(), "Inicio");
        mainPanel.add(crearPantallaEstado(), "Estado");
        mainPanel.add(crearPantallaMapa(), "Mapa");
        mainPanel.add(crearPantallaBatalla(), "Batalla");
        mainPanel.add(crearPantallaTienda(), "Tienda");
        mainPanel.add(crearPantallaResultados(), "Resultados");

        add(mainPanel);
        cardLayout.show(mainPanel, "Inicio");
    }

    private JMenuBar crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemInicio = new JMenuItem("Ir al Menú Principal");
        JMenuItem itemGuardar = new JMenuItem("Guardar Partida");
        JMenuItem itemCargar = new JMenuItem("Cargar Partida");
        JMenuItem itemSalir = new JMenuItem("Salir del Juego");

        itemInicio.addActionListener(e -> cardLayout.show(mainPanel, "Inicio"));

        itemGuardar.addActionListener(e -> {
            try {
                controller.guardarPartida("partida.dat");
                JOptionPane.showMessageDialog(this, "Partida guardada exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar la partida.");
            }
        });

        itemCargar.addActionListener(e -> {
            try {
                controller.cargarPartida("partida.dat");
                JOptionPane.showMessageDialog(this, "Partida cargada exitosamente.");
                if (controller.getPartida().getNodoActual().getTipo().contains("COMBATE") && !controller.isBatallaFinalizada()) {
                    cardLayout.show(mainPanel, "Batalla");
                    actualizarBatallaUI();
                } else {
                    cardLayout.show(mainPanel, "Mapa");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar archivo de partida.");
            }
        });

        itemSalir.addActionListener(e -> System.exit(0));

        menuArchivo.add(itemInicio);
        menuArchivo.addSeparator();
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemCargar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        JMenu menuVer = new JMenu("Ver");
        JMenuItem itemEstado = new JMenuItem("Estado de la Party");
        itemEstado.addActionListener(e -> cardLayout.show(mainPanel, "Estado"));
        menuVer.add(itemEstado);

        menuBar.add(menuArchivo);
        menuBar.add(menuVer);

        return menuBar;
    }

    private JPanel crearPantallaInicio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(20, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("ECLIPSE ARCANA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 42));
        lblTitulo.setForeground(new Color(220, 170, 60));

        JLabel lblSubtitulo = new JLabel("RPG TÁCTICO POR TURNOS", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("SansSerif", Font.ITALIC, 14));
        lblSubtitulo.setForeground(Color.LIGHT_GRAY);

        JButton btnNueva = new JButton("NUEVA AVENTURA");
        JButton btnCargar = new JButton("CONTINUAR PARTIDA");
        JButton btnSalir = new JButton("SALIR");

        Dimension btnSize = new Dimension(220, 45);
        btnNueva.setPreferredSize(btnSize);
        btnCargar.setPreferredSize(btnSize);
        btnSalir.setPreferredSize(btnSize);

        Font fontBotones = new Font("SansSerif", Font.BOLD, 14);
        btnNueva.setFont(fontBotones);
        btnCargar.setFont(fontBotones);
        btnSalir.setFont(fontBotones);

        btnNueva.addActionListener(e -> {
            controller.reiniciarPartida();
            txtLog.setText("");
            cardLayout.show(mainPanel, "Batalla");
            actualizarBatallaUI();
        });

        btnCargar.addActionListener(e -> {
            try {
                controller.cargarPartida("partida.dat");
                JOptionPane.showMessageDialog(this, "Partida cargada exitosamente.");
                if (controller.getPartida().getNodoActual().getTipo().contains("COMBATE") && !controller.isBatallaFinalizada()) {
                    cardLayout.show(mainPanel, "Batalla");
                    actualizarBatallaUI();
                } else {
                    cardLayout.show(mainPanel, "Mapa");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No se encontró una partida guardada previa.");
            }
        });

        btnSalir.addActionListener(e -> System.exit(0));

        gbc.gridy = 0;
        panel.add(lblTitulo, gbc);

        gbc.gridy = 1;
        panel.add(lblSubtitulo, gbc);

        gbc.gridy = 2;
        panel.add(Box.createVerticalStrut(20), gbc);

        gbc.gridy = 3;
        panel.add(btnNueva, gbc);

        gbc.gridy = 4;
        panel.add(btnCargar, gbc);

        gbc.gridy = 5;
        panel.add(btnSalir, gbc);

        return panel;
    }

    private JPanel crearPantallaMapa() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("SELECCIONA TU SIGUIENTE DESTINO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlNodos = new JPanel(new FlowLayout());

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                pnlNodos.removeAll();

                List<NodoMapa> opciones = controller.getNodosDisponibles();

                if (opciones.isEmpty()) {
                    pnlNodos.add(new JLabel("¡Has llegado al final de la aventura!"));
                } else {
                    for (NodoMapa nodo : opciones) {
                        String textoEstado = "";
                        if (nodo.isCompletado()) textoEstado = " [Completado]";
                        else if (nodo.isUsado()) textoEstado = " [Usado]";

                        JButton btnNodo = new JButton(nodo.getTipo() + " (Nivel " + nodo.getNivel() + ")" + textoEstado);
                        btnNodo.addActionListener(evt -> {
                            if (nodo.isCompletado()) {
                                JOptionPane.showMessageDialog(null, "Este nodo ya fue completado.");
                                return;
                            }

                            controller.seleccionarNodo(nodo);

                            if (nodo.getTipo().contains("COMBATE")) {
                                cardLayout.show(mainPanel, "Batalla");
                                actualizarBatallaUI();
                            } else if (nodo.getTipo().equals("TIENDA")) {
                                cardLayout.show(mainPanel, "Tienda");
                            } else if (nodo.getTipo().equals("DESCANSO")) {
                                if (!nodo.isUsado()) {
                                    nodo.setUsado(true);
                                    nodo.setCompletado(true);
                                    for (Personaje p : controller.getPartida().getParty()) {
                                        p.curar(50);
                                    }
                                    JOptionPane.showMessageDialog(null, "Descansaste y recuperaste 50 HP para cada integrante.");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Ya utilizaste esta zona de descanso.");
                                }
                                cardLayout.show(mainPanel, "Mapa");
                            }
                        });
                        pnlNodos.add(btnNodo);
                    }
                }
                pnlNodos.revalidate();
                pnlNodos.repaint();
            }
        });

        panel.add(pnlNodos, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPantallaTienda() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("TIENDA DE ÍTEMS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel lblOro = new JLabel("Oro disponible: 0", SwingConstants.CENTER);
        lblOro.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel pnlNorte = new JPanel(new GridLayout(2, 1));
        pnlNorte.add(lblTitulo);
        pnlNorte.add(lblOro);
        panel.add(pnlNorte, BorderLayout.NORTH);

        JPanel pnlItems = new JPanel(new GridLayout(3, 1, 5, 5));
        
        JButton btnPocionVida = new JButton("Poción de Vida (+50 HP) - Precio: 20 Oro");
        JButton btnPocionMana = new JButton("Poción de Maná (+30 MP) - Precio: 25 Oro");
        JButton btnPlumaFenix = new JButton("Pluma de Fénix (Revivir con 60 HP) - Precio: 50 Oro");

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                lblOro.setText("Oro disponible: " + controller.getPartida().getOro());
            }
        });

        btnPocionVida.addActionListener(e -> {
            if (controller.comprarItem(new Item("Poción Vida Gran", "PocionVida", 50, 20))) {
                JOptionPane.showMessageDialog(this, "Compraste Poción de Vida.");
                lblOro.setText("Oro disponible: " + controller.getPartida().getOro());
            } else {
                JOptionPane.showMessageDialog(this, "Oro insuficiente.");
            }
        });

        btnPocionMana.addActionListener(e -> {
            if (controller.comprarItem(new Item("Poción Maná Gran", "PocionMana", 30, 25))) {
                JOptionPane.showMessageDialog(this, "Compraste Poción de Maná.");
                lblOro.setText("Oro disponible: " + controller.getPartida().getOro());
            } else {
                JOptionPane.showMessageDialog(this, "Oro insuficiente.");
            }
        });

        btnPlumaFenix.addActionListener(e -> {
            if (controller.comprarItem(new Item("Pluma de Fénix", "Revivir", 60, 50))) {
                JOptionPane.showMessageDialog(this, "Compraste Pluma de Fénix.");
                lblOro.setText("Oro disponible: " + controller.getPartida().getOro());
            } else {
                JOptionPane.showMessageDialog(this, "Oro insuficiente.");
            }
        });

        pnlItems.add(btnPocionVida);
        pnlItems.add(btnPocionMana);
        pnlItems.add(btnPlumaFenix);
        panel.add(pnlItems, BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver al Mapa");
        btnVolver.addActionListener(e -> {
            if (controller.getPartida().getNodoActual() != null) {
                controller.getPartida().getNodoActual().setUsado(true);
            }
            cardLayout.show(mainPanel, "Mapa");
        });
        panel.add(btnVolver, BorderLayout.SOUTH);

        return panel;
    }

    // Panel personalizado para dibujar la imagen de fondo en la batalla
    private class PanelFondoBatalla extends JPanel {
        private Image imagenFondo;

        public PanelFondoBatalla(LayoutManager layout) {
            super(layout);
            try {
                imagenFondo = new ImageIcon("src/sprites/fondo_batalla.png").getImage();
            } catch (Exception e) {
                imagenFondo = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagenFondo != null && imagenFondo.getWidth(null) > 0) {
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fondo por defecto con degradado si no existe la imagen
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 30, 50), 0, getHeight(), new Color(15, 10, 20));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private JPanel crearPantallaBatalla() {
        PanelFondoBatalla panel = new PanelFondoBatalla(new BorderLayout());
        
        JPanel pnlEnemigo = new JPanel(new BorderLayout());
        pnlEnemigo.setOpaque(false);
        lblEnemigoNombre = new JLabel("", SwingConstants.CENTER);
        lblEnemigoNombre.setFont(new Font("Arial", Font.BOLD, 15));
        lblEnemigoNombre.setForeground(Color.WHITE);
        lblEnemigoSprite = new JLabel("", SwingConstants.CENTER);
        pnlEnemigo.add(lblEnemigoNombre, BorderLayout.NORTH);
        pnlEnemigo.add(lblEnemigoSprite, BorderLayout.CENTER);
        panel.add(pnlEnemigo, BorderLayout.NORTH);

        JPanel pnlCentro = new JPanel(new GridLayout(2, 1));
        pnlCentro.setOpaque(false);
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pnlCentro.add(new JScrollPane(txtLog));

        pnlPartySprites = new JPanel(new FlowLayout());
        pnlPartySprites.setOpaque(false);
        pnlCentro.add(pnlPartySprites);

        panel.add(pnlCentro, BorderLayout.CENTER);

        JPanel pnlAcciones = new JPanel();
        cbPersonajes = new JComboBox<>();
        JButton btnAtacar = new JButton("Atacar");
        JButton btnHabilidad = new JButton("Habilidad");
        JButton btnItem = new JButton("Usar Ítem");
        JButton btnEstado = new JButton("Ver Party");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnMenuPrincipal = new JButton("Menú Principal");

        btnAtacar.addActionListener(e -> {
            Personaje p = getPersonajeSeleccionado();
            if (p != null) {
                txtLog.append(controller.ejecutarAtaque(p) + "\n");
                verificarFinBatalla();
            }
        });

        btnHabilidad.addActionListener(e -> {
            Personaje p = getPersonajeSeleccionado();
            if (p != null) {
                txtLog.append(controller.ejecutarHabilidad(p) + "\n");
                verificarFinBatalla();
            }
        });

        btnItem.addActionListener(e -> {
            Personaje usuario = getPersonajeSeleccionado();
            List<Item> inventario = controller.getPartida().getInventario();

            if (usuario == null || !usuario.estaVivo()) {
                txtLog.append("El personaje seleccionado no puede actuar.\n");
                return;
            }

            if (inventario.isEmpty()) {
                txtLog.append("Sin ítems en inventario.\n");
                return;
            }

            String[] comboItems = new String[inventario.size()];
            for (int i = 0; i < inventario.size(); i++) {
                comboItems[i] = inventario.get(i).getNombre() + " (" + inventario.get(i).getTipo() + ")";
            }

            List<Personaje> party = controller.getPartida().getParty();
            String[] comboAliados = new String[party.size()];
            for (int i = 0; i < party.size(); i++) {
                Personaje p = party.get(i);
                comboAliados[i] = p.getNombre() + " (HP: " + Math.max(0, p.getVidaActual()) + "/" + p.getVidaMax() + ")";
            }

            JComboBox<String> cbItemsDlg = new JComboBox<>(comboItems);
            JComboBox<String> cbAliadosDlg = new JComboBox<>(comboAliados);

            JPanel panelDialogo = new JPanel(new GridLayout(2, 2, 5, 5));
            panelDialogo.add(new JLabel("Selecciona Ítem:"));
            panelDialogo.add(cbItemsDlg);
            panelDialogo.add(new JLabel("Selecciona Aliado Objetivo:"));
            panelDialogo.add(cbAliadosDlg);

            int opt = JOptionPane.showConfirmDialog(this, panelDialogo, "Usar Ítem en Aliado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if (opt == JOptionPane.OK_OPTION) {
                int idxItem = cbItemsDlg.getSelectedIndex();
                int idxAliado = cbAliadosDlg.getSelectedIndex();

                if (idxItem >= 0 && idxAliado >= 0) {
                    Item itemSeleccionado = inventario.get(idxItem);
                    Personaje objetivoSeleccionado = party.get(idxAliado);

                    String resItem = controller.usarItem(usuario, objetivoSeleccionado, itemSeleccionado);
                    txtLog.append(resItem + "\n");
                    verificarFinBatalla();
                }
            }
        });

        btnEstado.addActionListener(e -> cardLayout.show(mainPanel, "Estado"));

        btnGuardar.addActionListener(e -> {
            try {
                controller.guardarPartida("partida.dat");
                JOptionPane.showMessageDialog(this, "Partida guardada exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar.");
            }
        });

        btnMenuPrincipal.addActionListener(e -> {
            int resp = JOptionPane.showConfirmDialog(this, "¿Deseas volver al Menú Principal? Asegúrate de haber guardado tu progreso.", "Volver al Menú", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                cardLayout.show(mainPanel, "Inicio");
            }
        });

        pnlAcciones.add(cbPersonajes);
        pnlAcciones.add(btnAtacar);
        pnlAcciones.add(btnHabilidad);
        pnlAcciones.add(btnItem);
        pnlAcciones.add(btnEstado);
        pnlAcciones.add(btnGuardar);
        pnlAcciones.add(btnMenuPrincipal);

        panel.add(pnlAcciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPantallaResultados() {
        JPanel panel = new JPanel(new BorderLayout());
        lblTituloResultados = new JLabel("RESUMEN DE BATALLA", SwingConstants.CENTER);
        lblTituloResultados.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTituloResultados, BorderLayout.NORTH);

        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Monospaced", Font.PLAIN, 13));
        panel.add(new JScrollPane(txtResultados), BorderLayout.CENTER);

        btnBotonResultado = new JButton("Ir al Mapa");
        btnBotonResultado.addActionListener(e -> {
            if (controller.esPartyDerrotada()) {
                controller.reiniciarPartida();
                txtLog.setText("");
                cardLayout.show(mainPanel, "Batalla");
                actualizarBatallaUI();
            } else {
                cardLayout.show(mainPanel, "Mapa");
            }
        });

        panel.add(btnBotonResultado, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPantallaEstado() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea txtEstado = new JTextArea();
        txtEstado.setEditable(false);
        
        JButton btnVolver = new JButton("Volver a Batalla");
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "Batalla"));

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                StringBuilder sb = new StringBuilder("=== ESTADO DE LA PARTY ===\n");
                sb.append("Oro total: ").append(controller.getPartida().getOro()).append("\n\n");
                for (Personaje p : controller.getPartida().getParty()) {
                    sb.append(p.getNombre()).append(" - Niv: ").append(p.getNivel())
                      .append(" | HP: ").append(p.getVidaActual()).append("/").append(p.getVidaMax())
                      .append(" | MP: ").append(p.getManaActual()).append("/").append(p.getManaMax())
                      .append(" | EXP: ").append(p.getExperiencia()).append("\n");
                }
                sb.append("\n=== INVENTARIO ===\n");
                for (Item i : controller.getPartida().getInventario()) {
                    sb.append("- ").append(i.getNombre()).append("\n");
                }
                txtEstado.setText(sb.toString());
            }
        });

        panel.add(new JScrollPane(txtEstado), BorderLayout.CENTER);
        panel.add(btnVolver, BorderLayout.SOUTH);
        return panel;
    }

    private void verificarFinBatalla() {
        actualizarBatallaUI();

        if (controller.esPartyDerrotada() || controller.isBatallaFinalizada()) {
            if (controller.esPartyDerrotada()) {
                JOptionPane.showMessageDialog(this, "¡Toda tu party ha sido eliminada! Game Over.", "DERROTA", JOptionPane.ERROR_MESSAGE);
                lblTituloResultados.setText("¡HAS SIDO DERROTADO!");
                lblTituloResultados.setForeground(Color.RED);
                txtResultados.setText("GAME OVER\n\nTodos los miembros de tu equipo han caído en combate.\n\nHacé clic en 'Intentar de nuevo' para reiniciar tu aventura desde el primer mapa.");
                btnBotonResultado.setText("Intentar de nuevo");
            } else {
                lblTituloResultados.setText("RESUMEN DE BATALLA");
                lblTituloResultados.setForeground(Color.BLACK);
                txtResultados.setText(controller.getResultadoUltimaBatalla());
                btnBotonResultado.setText("Ir al Mapa");
            }

            cardLayout.show(mainPanel, "Resultados");
        }
    }

    private Personaje getPersonajeSeleccionado() {
        int idx = cbPersonajes.getSelectedIndex();
        if (idx >= 0 && idx < controller.getPartida().getParty().size()) {
            return controller.getPartida().getParty().get(idx);
        }
        return null;
    }

    private void actualizarBatallaUI() {
        Enemigo e = controller.getEnemigoActual();
        if (e != null) {
            lblEnemigoNombre.setText(e.getNombre() + " (HP: " + Math.max(0, e.getVidaActual()) + "/" + e.getVidaMax() + " | ATK: " + e.getAtaque() + ")");
            lblEnemigoSprite.setIcon(cargarIcono(e.getSpriteUrl()));
        }
        
        cbPersonajes.removeAllItems();
        pnlPartySprites.removeAll();

        for (Personaje p : controller.getPartida().getParty()) {
            cbPersonajes.addItem(p.getNombre() + " (HP: " + Math.max(0, p.getVidaActual()) + ")");
            
            JPanel pnlHeroe = new JPanel(new BorderLayout());
            pnlHeroe.setOpaque(false);
            JLabel lblSprite = new JLabel(cargarIcono(p.getSpriteUrl()));
            JLabel lblNombre = new JLabel(p.getNombre() + " (" + Math.max(0, p.getVidaActual()) + " HP)", SwingConstants.CENTER);
            lblNombre.setForeground(Color.WHITE);
            pnlHeroe.add(lblSprite, BorderLayout.CENTER);
            pnlHeroe.add(lblNombre, BorderLayout.SOUTH);
            pnlPartySprites.add(pnlHeroe);
        }
        
        pnlPartySprites.revalidate();
        pnlPartySprites.repaint();
    }

    private ImageIcon cargarIcono(String rutaArchivo) {
        try {
            ImageIcon icon = new ImageIcon(rutaArchivo);
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaJuego().setVisible(true));
    }
}
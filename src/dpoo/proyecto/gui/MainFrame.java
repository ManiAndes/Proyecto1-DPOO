package dpoo.proyecto.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.gui.admin.AdminDashboardPanel;
import dpoo.proyecto.gui.auth.AuthPanel;
import dpoo.proyecto.gui.organizador.OrganizadorDashboardPanel;
import dpoo.proyecto.gui.usuario.UsuarioDashboardPanel;
import dpoo.proyecto.usuarios.Administrador;
import dpoo.proyecto.usuarios.Organizador;
import dpoo.proyecto.usuarios.Usuario;
import dpoo.proyecto.usuarios.UsuarioGenerico;
import persistencia.CentralPersistencia;

/**
 * Ventana principal de la aplicación Swing.
 * Contendrá los paneles de Login/Registro y los tableros por rol.
 * Por ahora solo muestra un placeholder mientras se arma la navegación.
 */
public class MainFrame extends JFrame {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;
    private final CardLayout cards;
    private final JPanel root;

    private AuthPanel authPanel;
    private AdminDashboardPanel adminPanel;
    private OrganizadorDashboardPanel orgPanel;
    private UsuarioDashboardPanel usuarioPanel;

    public MainFrame(MasterTicket sistema, CentralPersistencia persistencia) {
        super("BoletaMaster - GUI");
        this.sistema = sistema;
        this.persistencia = persistencia;
        this.cards = new CardLayout();
        this.root = new JPanel(cards);
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 720));
        setLayout(new BorderLayout());

        authPanel = new AuthPanel(sistema, persistencia, this::handleLogin);
        root.add(authPanel, "auth");

        root.add(buildPlaceholder(), "placeholder");
        cards.show(root, "auth");

        add(root, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(18, 18, 38));
        JLabel title = new JLabel("BoletaMaster GUI en construcción", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        panel.add(title, BorderLayout.CENTER);

        JLabel subtitle = new JLabel(
                "Próximamente: login, tableros por rol, compra y emisión de tiquetes con QR",
                SwingConstants.CENTER);
        subtitle.setForeground(new Color(180, 180, 220));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(subtitle, BorderLayout.SOUTH);
        return panel;
    }

    private void handleLogin(UsuarioGenerico usuario) {
        if (usuario instanceof Administrador) {
            adminPanel = new AdminDashboardPanel(sistema, persistencia, (Administrador) usuario, this::mostrarLogin);
            root.add(adminPanel, "admin");
            cards.show(root, "admin");
        } else if (usuario instanceof Organizador) {
            orgPanel = new OrganizadorDashboardPanel(sistema, persistencia, (Organizador) usuario, this::mostrarLogin);
            root.add(orgPanel, "org");
            cards.show(root, "org");
        } else if (usuario instanceof Usuario) {
            usuarioPanel = new UsuarioDashboardPanel(sistema, persistencia, (Usuario) usuario, this::mostrarLogin);
            root.add(usuarioPanel, "usr");
            cards.show(root, "usr");
        } else {
            cards.show(root, "auth");
        }
    }

    private void mostrarLogin() {
        cards.show(root, "auth");
    }

    public MasterTicket getSistema() {
        return sistema;
    }

    public CentralPersistencia getPersistencia() {
        return persistencia;
    }
}

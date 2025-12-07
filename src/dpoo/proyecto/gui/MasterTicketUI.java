package dpoo.proyecto.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.usuarios.Administrador;
import dpoo.proyecto.usuarios.Natural;
import dpoo.proyecto.usuarios.Organizador;
import dpoo.proyecto.usuarios.UsuarioGenerico;
import persistencia.CentralPersistencia;

public class MasterTicketUI extends JFrame {

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;
    private final CardLayout layout = new CardLayout();
    private final JPanel container = new JPanel(layout);

    private final JLabel status = new JLabel(" ");

    public MasterTicketUI() {
        super("MasterTicket - GUI");
        this.sistema = new MasterTicket();
        this.persistencia = new CentralPersistencia();
        this.persistencia.loadDefault(sistema);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveState();
                dispose();
                System.exit(0);
            }
        });

        JPanel loginPanel = new LoginPanel(this::onLogin, sistema);
        container.add(loginPanel, "login");
        add(container, BorderLayout.CENTER);
        status.setHorizontalAlignment(SwingConstants.CENTER);
        add(status, BorderLayout.SOUTH);
        layout.show(container, "login");
    }

    private void onLogin(UsuarioGenerico usuario) {
        if (usuario == null) {
            return;
        }
        JPanel dashboard;
        if (usuario instanceof Administrador) {
            dashboard = new AdminPanel(sistema, (Administrador) usuario, this::logoutAndSave, this::saveState);
        } else if (usuario instanceof Organizador) {
            dashboard = new OrganizerPanel(sistema, (Organizador) usuario, this::logoutAndSave, this::saveState);
        } else if (usuario instanceof Natural) {
            dashboard = new UserPanel(sistema, (Natural) usuario, this::logoutAndSave, this::saveState);
        } else {
            JOptionPane.showMessageDialog(this, "Tipo de usuario no soportado.");
            return;
        }
        container.add(dashboard, "dash");
        layout.show(container, "dash");
        setStatus("Sesión iniciada como " + usuario.getLogin());
    }

    private void logoutAndSave() {
        saveState();
        setStatus("Sesión cerrada.");
        layout.show(container, "login");
    }

    private void setStatus(String msg) {
        status.setText(msg);
    }

    public void saveState() {
        try {
            persistencia.saveDefault(sistema);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar: " + e.getMessage());
        }
    }

    /**
     * Panel de login y registro.
     */
    private static class LoginPanel extends JPanel {
        private final MasterTicket sistema;
        private final Consumer<UsuarioGenerico> onLogin;

        LoginPanel(Consumer<UsuarioGenerico> onLogin, MasterTicket sistema) {
            super(new BorderLayout());
            this.onLogin = onLogin;
            this.sistema = sistema;
            add(buildContent(), BorderLayout.CENTER);
        }

        private JPanel buildContent() {
            JPanel panel = new JPanel(new BorderLayout());
            CardLayout cards = new CardLayout();
            JPanel cardPanel = new JPanel(cards);
            JPanel loginForm = buildLoginForm();
            JPanel registerForm = buildRegisterForm();
            cardPanel.add(loginForm, "login");
            cardPanel.add(registerForm, "register");

            JPanel selector = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            JButton btnLogin = new JButton("Iniciar sesión");
            JButton btnRegister = new JButton("Crear cuenta");
            selector.add(btnLogin);
            selector.add(btnRegister);
            btnLogin.addActionListener(e -> cards.show(cardPanel, "login"));
            btnRegister.addActionListener(e -> cards.show(cardPanel, "register"));

            panel.add(selector, BorderLayout.NORTH);
            panel.add(cardPanel, BorderLayout.CENTER);
            return panel;
        }

        private JPanel buildLoginForm() {
            javax.swing.JTextField login = new javax.swing.JTextField();
            javax.swing.JPasswordField pass = new javax.swing.JPasswordField();
            javax.swing.JButton btn = new javax.swing.JButton("Ingresar");

            btn.addActionListener(ev -> {
                String l = login.getText().trim();
                String p = new String(pass.getPassword());
                Map<String, UsuarioGenerico> usuarios = sistema.getUsuarios();
                UsuarioGenerico u = usuarios.get(l);
                if (u == null) {
                    JOptionPane.showMessageDialog(this, "Usuario no existe o pendiente de aprobación.");
                    return;
                }
                if (!u.getPassword().equals(p)) {
                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta.");
                    return;
                }
                onLogin.accept(u);
            });

            return UIUtils.formPanel("Iniciar sesión", btn, new UIUtils.LabeledField("Login", login),
                    new UIUtils.LabeledField("Contraseña", pass));
        }

        private JPanel buildRegisterForm() {
            javax.swing.JTextField login = new javax.swing.JTextField();
            javax.swing.JPasswordField pass = new javax.swing.JPasswordField();
            String[] roles = { "Natural", "Organizador" };
            javax.swing.JComboBox<String> role = new javax.swing.JComboBox<>(roles);
            javax.swing.JButton btn = new javax.swing.JButton("Crear cuenta / Solicitar");

            btn.addActionListener(ev -> {
                String l = login.getText().trim();
                String p = new String(pass.getPassword());
                String r = (String) role.getSelectedItem();
                if (l.isEmpty() || p.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Complete usuario y contraseña.");
                    return;
                }
                if (!sistema.esLoginDisponible(l)) {
                    JOptionPane.showMessageDialog(this, "Login ya existe o pendiente.");
                    return;
                }
                if ("Natural".equals(r)) {
                    Natural n = new Natural(l, p);
                    sistema.getUsuarios().put(l, n);
                    JOptionPane.showMessageDialog(this, "Usuario creado. Ingrese con sus datos.");
                    onLogin.accept(n);
                } else {
                    boolean ok = sistema.registrarSolicitudOrganizador(l, p);
                    if (ok) {
                        JOptionPane.showMessageDialog(this,
                                "Solicitud de organizador enviada. Espere aprobación del admin.");
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo registrar la solicitud.");
                    }
                }
            });

            return UIUtils.formPanel("Crear / Solicitar", btn, new UIUtils.LabeledField("Login", login),
                    new UIUtils.LabeledField("Contraseña", pass), new UIUtils.LabeledField("Tipo", role));
        }
    }
}

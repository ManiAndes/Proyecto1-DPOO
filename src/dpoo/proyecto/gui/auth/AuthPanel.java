package dpoo.proyecto.gui.auth;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.usuarios.Administrador;
import dpoo.proyecto.usuarios.Natural;
import dpoo.proyecto.usuarios.Organizador;
import dpoo.proyecto.usuarios.UsuarioGenerico;
import persistencia.CentralPersistencia;

public class AuthPanel extends JPanel {

    public interface AuthListener {
        void onLogin(UsuarioGenerico usuario);
    }

    private final MasterTicket sistema;
    private final CentralPersistencia persistencia;
    private final AuthListener listener;

    private JTextField usuarioField;
    private JPasswordField passwordField;
    private JLabel info;

    public AuthPanel(MasterTicket sistema, CentralPersistencia persistencia, AuthListener listener) {
        this.sistema = sistema;
        this.persistencia = persistencia;
        this.listener = listener;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(32, 32, 32, 32));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Usuario"), gbc);
        gbc.gridx = 1;
        usuarioField = new JTextField(18);
        form.add(usuarioField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Contraseña"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        form.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel buttons = new JPanel();
        JButton loginBtn = new JButton("Ingresar");
        loginBtn.addActionListener(e -> intentarLogin());
        JButton naturalBtn = new JButton("Registrar Natural");
        naturalBtn.addActionListener(e -> registrarNatural());
        JButton orgBtn = new JButton("Solicitar Organizador");
        orgBtn.addActionListener(e -> registrarOrganizador());
        buttons.add(loginBtn);
        buttons.add(naturalBtn);
        buttons.add(orgBtn);
        form.add(buttons, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        info = new JLabel(" ");
        form.add(info, gbc);

        add(form, BorderLayout.CENTER);
    }

    private void intentarLogin() {
        String login = usuarioField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (login.isEmpty() || pass.isEmpty()) {
            info.setText("Ingrese usuario y contraseña.");
            return;
        }
        UsuarioGenerico u = sistema.getUsuarios().get(login);
        if (u == null) {
            if (sistema.esLoginPendienteOrganizador(login)) {
                info.setText("Solicitud de organizador pendiente.");
            } else {
                info.setText("Usuario no encontrado.");
            }
            return;
        }
        if (!u.getPassword().equals(pass)) {
            info.setText("Contraseña incorrecta.");
            return;
        }
        info.setText("Bienvenido " + login);
        if (listener != null) {
            listener.onLogin(u);
        }
    }

    private void registrarNatural() {
        String login = usuarioField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (login.isEmpty() || pass.isEmpty()) {
            info.setText("Complete usuario y contraseña para registrar.");
            return;
        }
        if (!sistema.esLoginDisponible(login)) {
            info.setText("Login ya existe o está pendiente.");
            return;
        }
        Natural n = new Natural(login, pass);
        sistema.getUsuarios().put(login, n);
        if (persistencia != null) {
            persistencia.saveDefault(sistema);
        }
        JOptionPane.showMessageDialog(this, "Usuario natural creado. Inicia sesión.", "Registro",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarOrganizador() {
        String login = usuarioField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (login.isEmpty() || pass.isEmpty()) {
            info.setText("Complete usuario y contraseña para solicitar.");
            return;
        }
        if (!sistema.esLoginDisponible(login)) {
            info.setText("Login ya existe o está pendiente.");
            return;
        }
        boolean ok = sistema.registrarSolicitudOrganizador(login, pass);
        if (ok) {
            if (persistencia != null) persistencia.saveDefault(sistema);
            JOptionPane.showMessageDialog(this, "Solicitud enviada al administrador.", "Solicitud",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            info.setText("No se pudo registrar la solicitud.");
        }
    }
}

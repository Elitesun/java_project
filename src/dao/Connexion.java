package dao;

import interfaces.Dashboard;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.AbonneDAO; // Import the AbonneDAO
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connexion extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private AbonneDAO abonneDAO; // Use AbonneDAO instead of AdminDAO

    public Connexion() throws SQLException {
        abonneDAO = new AbonneDAO(); // Initialize AbonneDAO

        // Configuration de la fenêtre
        setTitle("Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        
        // Panneau principal avec marges plus importantes
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Titre centré avec plus d'espace
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 33, 33));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        // Conteneur pour les champs de saisie
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Champs de saisie
        JLabel userLabel = new JLabel("Nom d'utilisateur");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(new Color(66, 66, 66));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsPanel.add(userLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        usernameField = new JTextField(15);
        styleTextField(usernameField);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsPanel.add(usernameField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passLabel.setForeground(new Color(66, 66, 66));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsPanel.add(passLabel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        passwordField = new JPasswordField(15);
        styleTextField(passwordField);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsPanel.add(passwordField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        mainPanel.add(fieldsPanel);

        // Bouton de connexion
        loginButton = new JButton("Se connecter");
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(25, 118, 210));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(21, 101, 192));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(25, 118, 210));
            }
        });

        //logique de la connexion
        loginButton.addActionListener(_ -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                boolean isAuthenticated = authentifier(username, password);
                if (isAuthenticated) {
                    JOptionPane.showMessageDialog(this,
                        "Connexion réussie",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new Dashboard().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Identifiants incorrects",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this,
                    "Erreur de connexion",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(loginButton);
        add(mainPanel);
    }

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(300, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 189, 189)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new Connexion().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    // Authenticate a user with fixed credentials
    public boolean authentifier(String username, String password) {
        return "admin".equals(username) && "1234".equals(password);
    }
}
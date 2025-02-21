package interfaces;

import javax.swing.*;
import dao.Connexion;
import java.awt.*;
import dao.AbonneDAO;
import dao.AbonnementDAO;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import abonnes.Abonnes;
import abonnements.Abonnements;
import Model.Abonne; // Correct import for Abonne model
import dao.Statistiques;
import souscriptions.Souscriptions;
import dao.exceptions.DAOException;
import java.sql.SQLException;

public class Dashboard extends JFrame {
    private final AbonneDAO abonneDAO;
    private final AbonnementDAO abonnementDAO;
    private final DecimalFormat df = new DecimalFormat("#,##0.00 €");

    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color LIGHT_BLUE = new Color(66, 139, 202);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);

    public Dashboard() {
        try {
            abonneDAO = new AbonneDAO();
            abonnementDAO = new AbonnementDAO();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + ex.getMessage());
            dispose();
            throw new RuntimeException(ex);
        }

        setTitle("Tableau de Bord");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(30, 30));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Tableau de Bord");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(30, 30));
        centerPanel.setBackground(BACKGROUND_COLOR);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(BACKGROUND_COLOR);

        double revenuTotal = 0.0;
        int totalAbonnes = 0;
        int abonnesActifs = 0;

        try {
            revenuTotal = abonnementDAO.getRevenuMensuelTotal();
            totalAbonnes = abonneDAO.getAllAbonnes().size();
            abonnesActifs = (int) abonneDAO.getAllAbonnes().stream().filter(Abonne::isStatutSouscription).count();
        } catch (DAOException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des statistiques: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        cardsPanel.add(createCard("Chiffre d'Affaires", df.format(revenuTotal), CARD_COLOR, PRIMARY_BLUE));
        cardsPanel.add(createCard("Abonnements Actifs", String.valueOf(abonnesActifs), CARD_COLOR, PRIMARY_BLUE));
        cardsPanel.add(createCard("Total Abonnés", String.valueOf(totalAbonnes), CARD_COLOR, PRIMARY_BLUE));

        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        buttonsPanel.add(createRoundedButton("Gérer Abonnés", PRIMARY_BLUE));
        buttonsPanel.add(createRoundedButton("Gérer Abonnements", PRIMARY_BLUE));
        buttonsPanel.add(createRoundedButton("Gérer Souscriptions", PRIMARY_BLUE));
        buttonsPanel.add(createRoundedButton("Statistiques", LIGHT_BLUE));

        centerPanel.add(buttonsPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JButton refreshButton = createRoundedButton("Rafraîchir", LIGHT_BLUE);
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.addActionListener(e -> refreshDashboard());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(refreshButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Panel des boutons d'action
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setBackground(BACKGROUND_COLOR);

        JButton deconnexionButton = createActionButton("Déconnexion", Color.RED); // Déconnexion button

        buttonContainer.add(deconnexionButton);

        mainPanel.add(buttonContainer, BorderLayout.SOUTH);

        add(mainPanel);

        for (Component comp : buttonsPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                switch (button.getText()) {
                    case "Gérer Abonnés":
                        button.addActionListener(e -> new Abonnes().setVisible(true));
                        break;
                    case "Gérer Abonnements":
                        button.addActionListener(e -> {
                            try {
                                new Abonnements().setVisible(true);
                            } catch (Exception ex) {
                                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        break;
                    case "Gérer Souscriptions":
                        button.addActionListener(e -> {
                            try {
                                new Souscriptions().setVisible(true);
                            } catch (Exception ex) {
                                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        break;
                    case "Statistiques":
                        button.addActionListener(e -> new Statistiques(abonneDAO, abonnementDAO).setVisible(true));
                        break;
                }
            }
        }

        // Configuration des actions des boutons
        deconnexionButton.addActionListener(e -> {
            try {
                // Logic to log out and return to the login screen
                dispose();
                new Connexion().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Erreur lors de la d  connexion: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JPanel createCard(String title, String value, Color bgColor, Color textColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BACKGROUND_COLOR);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(textColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);

        return card;
    }

    private JButton createRoundedButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 60));

        return button;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    private void refreshDashboard() {
        dispose();
        new Dashboard().setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }
}

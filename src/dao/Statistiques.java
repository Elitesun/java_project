package dao;

import javax.swing.*;
import interfaces.Dashboard;
import java.awt.*;
import java.text.DecimalFormat;
import java.sql.SQLException;
import java.util.Map;

public class Statistiques extends JFrame {
    // Objets d'accès aux données
    private AbonneDAO abonneDAO;
    private AbonnementDAO abonnementDAO;
    private DecimalFormat df = new DecimalFormat("#,##0.00 CFA"); // Format pour afficher les montants en CFA

    // Réutilisation de la palette de couleurs du Dashboard pour la cohérence visuelle
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);    // Couleur principale
    private static final Color LIGHT_BLUE = new Color(66, 139, 202);      // Couleur secondaire
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);// Couleur de fond
    private static final Color CARD_COLOR = Color.WHITE;                  // Couleur des panneaux
    private static final Color TEXT_COLOR = new Color(33, 37, 41);       // Couleur du texte
    private static final Color BORDER_COLOR = new Color(222, 226, 230);  // Couleur des bordures

    public Statistiques(AbonneDAO abonneDAO, AbonnementDAO abonnementDAO) {
        this.abonneDAO = abonneDAO;
        this.abonnementDAO = abonnementDAO;

        // Configuration de la fenêtre
        setTitle("Statistiques Détaillées");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Configuration du titre
        JLabel titleLabel = new JLabel("Statistiques Détaillées");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel central avec grille 2x2 pour les différentes statistiques
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setBackground(BACKGROUND_COLOR);

        try {
            // 1. Statistiques des abonnés
            JPanel abonnesPanel = createStatsPanel("Statistiques des Abonnés", PRIMARY_BLUE);
            int totalAbonnes = abonneDAO.getNombreTotalAbonnes();
            int abonnesActifs = abonneDAO.getNombreAbonnesActifs();
            addStatLine(abonnesPanel, "Nombre total d'abonnés", String.valueOf(totalAbonnes));
            addStatLine(abonnesPanel, "Abonnés actifs", String.valueOf(abonnesActifs));
            addStatLine(abonnesPanel, "Taux d'activité", String.format("%.1f%%", 
                totalAbonnes > 0 ? (abonnesActifs * 100.0 / totalAbonnes) : 0));

            // 2. Statistiques des inscriptions mensuelles
            JPanel inscriptionsPanel = createStatsPanel("Nouvelles Inscriptions par Mois", PRIMARY_BLUE);
            Map<String, Integer> statsParMois = abonneDAO.getStatistiquesParMois();
            for (Map.Entry<String, Integer> entry : statsParMois.entrySet()) {
                addStatLine(inscriptionsPanel, entry.getKey(), String.valueOf(entry.getValue()));
            }

            // 3. Statistiques des revenus
            JPanel abonnementsPanel = createStatsPanel("Statistiques des Abonnements", PRIMARY_BLUE);
            double revenuTotal = abonnementDAO.getRevenuMensuelTotal();
            addStatLine(abonnementsPanel, "Revenu mensuel total", df.format(revenuTotal));
            Map<String, Double> revenusParType = abonnementDAO.getRevenusParType();
            for (Map.Entry<String, Double> entry : revenusParType.entrySet()) {
                addStatLine(abonnementsPanel, entry.getKey(), df.format(entry.getValue()));
            }

            // Ajout des panels au centre
            centerPanel.add(abonnesPanel);
            centerPanel.add(inscriptionsPanel);
            centerPanel.add(abonnementsPanel);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération des statistiques: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Ajout des boutons de rafraîchissement et de retour
        JButton refreshButton = new JButton("Rafraîchir");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setBackground(LIGHT_BLUE);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshStats());

        JButton returnButton = new JButton("Retour");
        returnButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        returnButton.setBackground(Color.GRAY);
        returnButton.setForeground(Color.WHITE);
        returnButton.setFocusPainted(false);
        returnButton.addActionListener(e -> {
            dispose();
        
        });
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(returnButton);
        bottomPanel.add(refreshButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Crée un panneau de statistiques avec un titre.
     * @param title Le titre du panneau
     * @param headerColor La couleur de l'en-tête
     * @return Un JPanel configuré pour afficher des statistiques
     */
    private JPanel createStatsPanel(String title, Color headerColor) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(headerColor);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(CARD_COLOR);
        JScrollPane scrollPane = new JScrollPane(statsPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Ajoute une ligne de statistique à un panneau.
     * @param panel Le panneau auquel ajouter la statistique
     * @param label Le libellé de la statistique
     * @param value La valeur de la statistique
     */
    private void addStatLine(JPanel panel, String label, String value) {
        JPanel linePanel = new JPanel(new BorderLayout(10, 0));
        linePanel.setBackground(CARD_COLOR);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        linePanel.add(labelComponent, BorderLayout.WEST);
        linePanel.add(valueComponent, BorderLayout.EAST);
        
        JPanel statsPanel = (JPanel)((JScrollPane)panel.getComponent(1)).getViewport().getView();
        statsPanel.add(linePanel);
        statsPanel.add(Box.createVerticalStrut(10));
    }

    /**
     * Rafraîchit l'interface des statistiques en la recréant.
     */
    private void refreshStats() {
        dispose();
        try {
            new Statistiques(abonneDAO, abonnementDAO).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Erreur lors du rafraîchissement des statistiques: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Point d'entrée pour tester l'interface des statistiques.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> {
                try {
                    new Statistiques(new AbonneDAO(), new AbonnementDAO()).setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, 
                        "Erreur de connexion à la base de données: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
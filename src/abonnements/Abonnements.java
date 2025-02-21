package abonnements;

import Model.Abonnement;
import dao.AbonnementDAO;
import interfaces.Dashboard; // Import Dashboard
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.*;

public class Abonnements extends JFrame {
    private AbonnementDAO abonnementDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField libelleField, prixField, dureeField, rechercheField;
    private JButton ajouterBtn, modifierBtn, supprimerBtn, rechercherBtn, backBtn, refreshBtn;
    private int selectedId = -1;

    // Couleurs
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BG_COLOR = new Color(236, 240, 241);

    public Abonnements() throws SQLException {
        abonnementDAO = new AbonnementDAO();
        initializeUI();
        chargerDonnees();
    }

    private void initializeUI() {
        setTitle("Gestion des Abonnements - Salle de Sport");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel formulaire
        JPanel formPanel = createFormPanel();
        
        // Panel boutons
        JPanel buttonPanel = createButtonPanel();
        
        // Panel recherche
        JPanel searchPanel = createSearchPanel();

        // Panel table
        JPanel tablePanel = createTablePanel();

        // Assembly
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_COLOR);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(createTitledBorder("Informations de l'abonnement"));

        libelleField = createStyledTextField();
        prixField = createStyledTextField();
        dureeField = createStyledTextField();

        panel.add(createStyledLabel("Libellé:"));
        panel.add(libelleField);
        panel.add(createStyledLabel("Prix mensuel:"));
        panel.add(prixField);
        panel.add(createStyledLabel("Durée (mois):"));
        panel.add(dureeField);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(BG_COLOR);

        ajouterBtn = createStyledButton("Ajouter", SUCCESS_COLOR);
        modifierBtn = createStyledButton("Modifier", WARNING_COLOR);
        supprimerBtn = createStyledButton("Supprimer", DANGER_COLOR);
        backBtn = createStyledButton("Retour", PRIMARY_COLOR);
        refreshBtn = createStyledButton("Actualiser", PRIMARY_COLOR);

        panel.add(ajouterBtn);
        panel.add(modifierBtn);
        panel.add(supprimerBtn);
        panel.add(refreshBtn);
        panel.add(backBtn);

        // Ajout des listeners
        ajouterBtn.addActionListener(e -> ajouterAbonnement());
        modifierBtn.addActionListener(e -> modifierAbonnement());
        supprimerBtn.addActionListener(e -> supprimerAbonnement());
        refreshBtn.addActionListener(e -> chargerDonnees());
        backBtn.addActionListener(e -> retourDashboard());

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BG_COLOR);
        panel.setBorder(createTitledBorder("Recherche"));

        rechercheField = createStyledTextField();
        rechercheField.setPreferredSize(new Dimension(200, 30));
        rechercherBtn = createStyledButton("Rechercher", SECONDARY_COLOR);

        panel.add(createStyledLabel("Rechercher:"));
        panel.add(rechercheField);
        panel.add(rechercherBtn);

        rechercherBtn.addActionListener(e -> rechercherAbonnements());

        return panel;
    }

    private JPanel createTablePanel() {
        String[] columns = {"ID", "Libellé", "Prix mensuel", "Durée (mois)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(createTitledBorder("Liste des abonnements"));
        panel.add(scrollPane);

        // Sélection dans la table
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                remplirFormulaire(table.getSelectedRow());
            }
        });

        return panel;
    }

    // Méthodes utilitaires pour le style
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK); // Set text color to black
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(44, 62, 80));
        return label;
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            title
        );
        border.setTitleFont(new Font("Arial", Font.BOLD, 14));
        border.setTitleColor(PRIMARY_COLOR);
        return border;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK); // Change header text color to black
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(189, 195, 199));
        table.setShowVerticalLines(true);
    }

    // Méthodes de gestion des données
    private void chargerDonnees() {
        try {
            List<Abonnement> abonnements = abonnementDAO.getAllAbonnements();
            mettreAJourTable(abonnements);
        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des données", e);
        }
    }

    private void mettreAJourTable(List<Abonnement> abonnements) {
        tableModel.setRowCount(0);
        for (Abonnement a : abonnements) {
            tableModel.addRow(new Object[]{
                a.getId(),
                a.getLibelle(),
                a.getPrixMensuel(),
                a.getDureeMois()
            });
        }
    }

    // Implémentation des actions
    private void ajouterAbonnement() {
        try {
            Abonnement abonnement = new Abonnement();
            abonnement.setLibelle(libelleField.getText());
            abonnement.setPrixMensuel(Double.parseDouble(prixField.getText()));
            abonnement.setDureeMois(Integer.parseInt(dureeField.getText()));
            abonnementDAO.addAbonnement(abonnement);
            chargerDonnees();
            viderFormulaire();
            JOptionPane.showMessageDialog(this, "Abonnement ajouté avec succès!");
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ajout", e);
        }
    }

    private void modifierAbonnement() {
        try {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un abonnement à modifier.");
                return;
            }
            Abonnement abonnement = new Abonnement();
            abonnement.setId(selectedId);
            abonnement.setLibelle(libelleField.getText());
            abonnement.setPrixMensuel(Double.parseDouble(prixField.getText()));
            abonnement.setDureeMois(Integer.parseInt(dureeField.getText()));
            abonnementDAO.updateAbonnement(abonnement);
            chargerDonnees();
            viderFormulaire();
            JOptionPane.showMessageDialog(this, "Abonnement modifié avec succès!");
        } catch (Exception e) {
            afficherErreur("Erreur lors de la modification", e);
        }
    }

    private void supprimerAbonnement() {
        try {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un abonnement à supprimer.");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                abonnementDAO.deleteAbonnement(selectedId);
                chargerDonnees();
                viderFormulaire();
                JOptionPane.showMessageDialog(this, "Abonnement supprimé avec succès!");
            }
        } catch (Exception e) {
            afficherErreur("Erreur lors de la suppression", e);
        }
    }

    private void rechercherAbonnements() {
        try {
            String searchText = rechercheField.getText().trim();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un terme de recherche");
                return;
            }
            List<Abonnement> results = abonnementDAO.searchAbonnementByLibelle(searchText);
            mettreAJourTable(results);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun résultat trouvé");
            }
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la recherche", e);
        }
    }

    private void remplirFormulaire(int selectedRow) {
        selectedId = (int) tableModel.getValueAt(selectedRow, 0);
        libelleField.setText((String) tableModel.getValueAt(selectedRow, 1));
        prixField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
        dureeField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
    }

    private void viderFormulaire() {
        libelleField.setText("");
        prixField.setText("");
        dureeField.setText("");
        selectedId = -1;
        table.clearSelection();
    }

    private void retourDashboard() {
        dispose();
        new Dashboard().setVisible(true);
    }

    private void afficherErreur(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message + ": " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> {
                try {
                    new Abonnements().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(Abonnements.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
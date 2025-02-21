package souscriptions;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.text.*;
import javax.swing.text.MaskFormatter;
import dao.AbonneDAO;
import dao.AbonnementDAO;
import dao.SouscriptionDAO;
import Model.Souscription;
import java.sql.SQLException;
import dao.exceptions.DAOException;

public class AjouterSouscription extends JFrame {
    private SouscriptionDAO souscriptionDAO;
    private AbonneDAO abonneDAO;
    private AbonnementDAO abonnementDAO;
    private Map<String, Integer> abonnes;
    private Map<String, Integer> abonnements;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private JTextField searchField;
    private JComboBox<String> abonneCombo;
    private JComboBox<String> abonnementCombo;
    private DefaultComboBoxModel<String> abonneModel;

    // Réutilisation de la palette de couleurs
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);

    public AjouterSouscription() throws DAOException {
        souscriptionDAO = new SouscriptionDAO();
        abonneDAO = new AbonneDAO();
        abonnementDAO = new AbonnementDAO();
        abonnes = getAbonnes();
        try {
            abonnements = getAbonnements();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des abonnements: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        // Define the missing variables
        String status = "actif"; // Example status, replace with actual logic
        String nomAbonnement = ""; // Add logic to get nomAbonnement
        String nomAbonne = ""; // Add logic to get nomAbonne

        setTitle("Ajouter une Souscription");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Nouvelle Souscription");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Panel de recherche
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchLabel = new JLabel("Rechercher un abonné:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterAbonnes(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterAbonnes(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterAbonnes(); }
        });

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // ComboBox pour l'abonné
        JPanel abonnePanel = new JPanel(new BorderLayout(10, 0));
        abonnePanel.setBackground(BACKGROUND_COLOR);
        abonnePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        abonnePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel abonneLabel = new JLabel("Sélectionner l'abonné:");
        abonneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        abonneModel = new DefaultComboBoxModel<>(abonnes.keySet().toArray(new String[0]));
        abonneCombo = new JComboBox<>(abonneModel);
        abonneCombo.setBackground(CARD_COLOR);

        abonnePanel.add(abonneLabel, BorderLayout.WEST);
        abonnePanel.add(abonneCombo, BorderLayout.CENTER);
        mainPanel.add(abonnePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ComboBox pour l'abonnement
        JPanel abonnementPanel = new JPanel(new BorderLayout(10, 0));
        abonnementPanel.setBackground(BACKGROUND_COLOR);
        abonnementPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        abonnementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel abonnementLabel = new JLabel("Sélectionner l'abonnement:");
        abonnementLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        abonnementCombo = new JComboBox<>(abonnements.keySet().toArray(new String[0]));
        abonnementCombo.setBackground(CARD_COLOR);

        abonnementPanel.add(abonnementLabel, BorderLayout.WEST);
        abonnementPanel.add(abonnementCombo, BorderLayout.CENTER);
        mainPanel.add(abonnementPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Champ de date
        JPanel datePanel = new JPanel(new BorderLayout(10, 0));
        datePanel.setBackground(BACKGROUND_COLOR);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("Date de début:");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JFormattedTextField dateField = createDateField();
        dateField.setText(dateFormat.format(new Date()));

        datePanel.add(dateLabel, BorderLayout.WEST);
        datePanel.add(dateField, BorderLayout.CENTER);
        mainPanel.add(datePanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelButton = new JButton("Annuler");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Enregistrer");
        styleButton(saveButton, PRIMARY_BLUE);
        saveButton.addActionListener(e -> {
            try {
                if (abonneCombo.getSelectedItem() == null || abonnementCombo.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Veuillez sélectionner un abonné et un abonnement.");
                    return;
                }

                int idAbonne = abonnes.get(abonneCombo.getSelectedItem());
                int idAbonnement = abonnements.get(abonnementCombo.getSelectedItem());
                Date dateDebut = dateFormat.parse(dateField.getText());

                Souscription newSouscription = new Souscription(
                    0,
                    idAbonne,
                    idAbonnement,
                    dateDebut,
                    status,
                    nomAbonnement,
                    nomAbonne
                );

                souscriptionDAO.addSouscription(newSouscription);
                JOptionPane.showMessageDialog(this, "Souscription ajoutée avec succès.");
                dispose();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez YYYY-MM-DD");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la souscription: " + ex.getMessage());
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private void filterAbonnes() {
        String searchText = searchField.getText().toLowerCase();
        DefaultComboBoxModel<String> filteredModel = new DefaultComboBoxModel<>();
        
        for (String abonne : abonnes.keySet()) {
            if (abonne.toLowerCase().contains(searchText)) {
                filteredModel.addElement(abonne);
            }
        }
        
        abonneCombo.setModel(filteredModel);
        if (filteredModel.getSize() > 0) {
            abonneCombo.setSelectedIndex(0);
        }
    }

    private JFormattedTextField createDateField() {
        JFormattedTextField field = null;
        try {
            MaskFormatter formatter = new MaskFormatter("####-##-##");
            formatter.setPlaceholderCharacter('_');
            field = new JFormattedTextField(formatter);
        } catch (ParseException e) {
            field = new JFormattedTextField();
            e.printStackTrace();
        }
        field.setBackground(CARD_COLOR);
        return field;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
    }

    private Map<String, Integer> getAbonnes() throws DAOException {
        Map<String, Integer> abonnesMap = new HashMap<>();
        abonneDAO.getAllAbonnes().forEach(abonne -> abonnesMap.put(abonne.getNom() + " " + abonne.getPrenom(), abonne.getId()));
        return abonnesMap;
    }

    private Map<String, Integer> getAbonnements() throws SQLException {
        Map<String, Integer> abonnementsMap = new HashMap<>();
        abonnementDAO.getAllAbonnements().forEach(abonnement -> abonnementsMap.put(abonnement.getLibelle(), abonnement.getId()));
        return abonnementsMap;
    }
}
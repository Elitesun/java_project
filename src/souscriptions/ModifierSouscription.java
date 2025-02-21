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

public class ModifierSouscription extends JFrame {
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
    private Souscription souscription;

    // Réutilisation de la palette de couleurs
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = Color.BLACK; // Set text color to black
    private static final Color BORDER_COLOR = new Color(222, 226, 230);

    public ModifierSouscription(Souscription souscription) throws SQLException {
        this.souscription = souscription;
        souscriptionDAO = new SouscriptionDAO();
        abonneDAO = new AbonneDAO();
        abonnementDAO = new AbonnementDAO();
        abonnes = getAbonnes();
        abonnements = getAbonnements();

        setTitle("Modifier une Souscription");
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
        JLabel titleLabel = new JLabel("Modifier la Souscription");
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
        searchLabel.setForeground(TEXT_COLOR);
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
        abonneLabel.setForeground(TEXT_COLOR);
        abonneModel = new DefaultComboBoxModel<>(abonnes.keySet().toArray(new String[0]));
        abonneCombo = new JComboBox<>(abonneModel);
        abonneCombo.setBackground(CARD_COLOR);
        abonneCombo.setForeground(TEXT_COLOR);
        abonneCombo.setSelectedItem(getAbonneNameById(souscription.getIdAbonne()));

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
        abonnementLabel.setForeground(TEXT_COLOR);
        abonnementCombo = new JComboBox<>(abonnements.keySet().toArray(new String[0]));
        abonnementCombo.setBackground(CARD_COLOR);
        abonnementCombo.setForeground(TEXT_COLOR);
        abonnementCombo.setSelectedItem(getAbonnementNameById(souscription.getIdAbonnement()));

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
        dateLabel.setForeground(TEXT_COLOR);
        JFormattedTextField dateField = createDateField();
        dateField.setText(dateFormat.format(souscription.getDateDebut()));

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

                Integer idAbonne = abonnes.get(abonneCombo.getSelectedItem());
                Integer idAbonnement = abonnements.get(abonnementCombo.getSelectedItem());

                if (idAbonne == null || idAbonnement == null) {
                    JOptionPane.showMessageDialog(this, "Abonné ou abonnement sélectionné est invalide.");
                    return;
                }

                Date dateDebut = dateFormat.parse(dateField.getText());
                String status = souscription.getStatus(); // Use existing status
                String nomAbonnement = souscription.getNomAbonnement(); // Use existing nomAbonnement
                String nomAbonne = souscription.getNomAbonne(); // Use existing nomAbonne

                Souscription updatedSouscription = new Souscription(
                    souscription.getId(),
                    idAbonne,
                    idAbonnement,
                    dateDebut,
                    status,
                    nomAbonnement,
                    nomAbonne
                );

                souscriptionDAO.updateSouscription(updatedSouscription);
                JOptionPane.showMessageDialog(this, "Souscription modifiée avec succès.");
                dispose();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez YYYY-MM-DD");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification de la souscription: " + ex.getMessage());
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
        field.setForeground(TEXT_COLOR);
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

    private Map<String, Integer> getAbonnes() throws SQLException {
        Map<String, Integer> abonnesMap = new HashMap<>();
        try {
            abonneDAO.getAllAbonnes().forEach(abonne -> abonnesMap.put(abonne.getNom() + " " + abonne.getPrenom(), abonne.getId()));
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des abonn s: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
        return abonnesMap;
    }

    private Map<String, Integer> getAbonnements() throws SQLException {
        Map<String, Integer> abonnementsMap = new HashMap<>();
        abonnementDAO.getAllAbonnements().forEach(abonnement -> abonnementsMap.put(abonnement.getLibelle(), abonnement.getId()));
        return abonnementsMap;
    }

    private String getAbonneNameById(int id) {
        for (Map.Entry<String, Integer> entry : abonnes.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getAbonnementNameById(int id) {
        for (Map.Entry<String, Integer> entry : abonnements.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        return null;
    }
}
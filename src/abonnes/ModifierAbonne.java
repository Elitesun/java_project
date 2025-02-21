package abonnes;

import javax.swing.*;
import java.awt.*;
import dao.AbonneDAO;
import Model.Abonne; // Correct import for Abonne model

public class ModifierAbonne extends JFrame {
    private AbonneDAO abonneDAO;
    private Abonne abonne;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField telephoneField;
    private JComboBox<String> statutCombo;

    // Réutilisation de la palette de couleurs
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);

    public ModifierAbonne(AbonneDAO abonneDAO, Abonne abonne) {
        this.abonneDAO = abonneDAO;
        this.abonne = abonne;
        
        setTitle("Modifier un Abonné");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Modifier l'Abonné");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Champs de saisie
        nomField = createField("Nom:", mainPanel);
        nomField.setText(abonne.getNom());
        
        prenomField = createField("Prénom:", mainPanel);
        prenomField.setText(abonne.getPrenom());
        
        telephoneField = createField("Téléphone:", mainPanel);
        telephoneField.setText(abonne.getNumeroTelephone());

        // ComboBox pour le statut
        JPanel statutPanel = new JPanel(new BorderLayout(10, 0));
        statutPanel.setBackground(BACKGROUND_COLOR);
        statutPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        statutPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statutLabel = new JLabel("Statut:");
        statutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statutCombo = new JComboBox<>(new String[]{"actif", "inactif"});
        statutCombo.setSelectedItem(abonne.isStatutSouscription() ? "actif" : "inactif");
        statutCombo.setBackground(CARD_COLOR);

        statutPanel.add(statutLabel, BorderLayout.WEST);
        statutPanel.add(statutCombo, BorderLayout.CENTER);
        mainPanel.add(statutPanel);
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
                if (validateFields()) {
                    abonne.setNom(nomField.getText());
                    abonne.setPrenom(prenomField.getText());
                    abonne.setNumeroTelephone(telephoneField.getText());
                    abonne.setStatutSouscription(statutCombo.getSelectedItem().equals("actif"));
                    abonneDAO.updateAbonne(abonne);
                    JOptionPane.showMessageDialog(this, "Abonné modifié avec succès.");
                    dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage());
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private JTextField createField(String label, JPanel container) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField field = new JTextField();
        field.setBackground(CARD_COLOR);

        panel.add(jLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        container.add(panel);
        container.add(Box.createVerticalStrut(10));

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

    private boolean validateFields() {
        if (nomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire.");
            return false;
        }
        if (prenomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le prénom est obligatoire.");
            return false;
        }
        if (telephoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le numéro de téléphone est obligatoire.");
            return false;
        }
        return true;
    }
}
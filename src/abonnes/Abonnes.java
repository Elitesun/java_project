package abonnes;

import dao.AbonneDAO;
import Model.Abonne; // Correct import for Abonne model
import interfaces.Dashboard; // Import Dashboard
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableCellRenderer;

public class Abonnes extends JFrame {
    private AbonneDAO abonneDAO;
    private JTable abonneTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, backButton, refreshButton, searchButton;
    private JTextField searchField;
    private static final String[] COLUMN_NAMES = {"ID", "Nom", "Prénom", "Date Inscription", "Téléphone", "Statut"};

    public Abonnes() {
        abonneDAO = new AbonneDAO();
        setTitle("Gestion des Abonnés");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 58, 138)); // Dark blue
        JLabel headerLabel = new JLabel("Gestion des Abonnés");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        abonneTable = new JTable(tableModel);
        abonneTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(abonneTable);
        add(scrollPane, BorderLayout.CENTER);

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = createStyledButton("Rechercher", new Color(0, 123, 255));
        searchPanel.add(new JLabel("Rechercher:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(255, 255, 255)); // White background

        addButton = createStyledButton("Ajouter Abonné", new Color(0, 123, 255));
        editButton = createStyledButton("Modifier Abonné", new Color(0, 123, 255));
        deleteButton = createStyledButton("Supprimer Abonné", new Color(0, 123, 255));
        backButton = createStyledButton("Retour", new Color(0, 123, 255));
        refreshButton = createStyledButton("Actualiser", new Color(0, 123, 255));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load existing subscribers
        loadAbonnes();
        centerTableText();

        // Action listeners
        addButton.addActionListener(e -> addAbonne());
        editButton.addActionListener(e -> editAbonne());
        deleteButton.addActionListener(e -> deleteAbonne());
        refreshButton.addActionListener(e -> loadAbonnes());
        searchButton.addActionListener(e -> searchAbonne());
        backButton.addActionListener(e -> {
            dispose(); // Close the current window
            new Dashboard().setVisible(true); // Open the dashboard
        });
    }

    private void centerTableText() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < abonneTable.getColumnCount(); i++) {
            abonneTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(color); // Blue
        button.setForeground(Color.BLACK); // Set text color to black
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40)); // Set button size

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

    private void loadAbonnes() {
        new SwingWorker<List<Abonne>, Void>() {
            @Override
            protected List<Abonne> doInBackground() throws Exception {
                return abonneDAO.getAllAbonnes();
            }

            @Override
            protected void done() {
                try {
                    List<Abonne> abonnes = get();
                    populateTable(abonnes);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Abonnes.this, 
                        "Erreur lors du chargement des abonnés:\n" + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(Abonnes.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }.execute();
    }

    private void populateTable(List<Abonne> abonnes) {
        tableModel.setRowCount(0); // Clear existing rows
        for (Abonne abonne : abonnes) {
            tableModel.addRow(new Object[]{
                abonne.getId(),
                abonne.getNom(),
                abonne.getPrenom(),
                abonne.getDateInscription(),
                abonne.getNumeroTelephone(),
                abonne.isStatutSouscription() ? "Actif" : "Inactif"
            });
        }
    }

    private void addAbonne() {
        Abonne abonne = showAbonneDialog(null);
        if (abonne != null) {
            try {
                abonneDAO.addAbonne(abonne);
                loadAbonnes();
                JOptionPane.showMessageDialog(this, "Abonné ajouté avec succès.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'abonné: " + e.getMessage());
            }
        }
    }

    private void editAbonne() {
        int selectedRow = abonneTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Abonne abonne = new Abonne();
            abonne.setId(id);
            abonne.setNom((String) tableModel.getValueAt(selectedRow, 1));
            abonne.setPrenom((String) tableModel.getValueAt(selectedRow, 2));
            abonne.setDateInscription((java.sql.Date) tableModel.getValueAt(selectedRow, 3));
            abonne.setNumeroTelephone((String) tableModel.getValueAt(selectedRow, 4));
            abonne.setStatutSouscription("Actif".equals(tableModel.getValueAt(selectedRow, 5)));

            Abonne updatedAbonne = showAbonneDialog(abonne);
            if (updatedAbonne != null) {
                try {
                    abonneDAO.updateAbonne(updatedAbonne);
                    loadAbonnes();
                    JOptionPane.showMessageDialog(this, "Abonné modifié avec succès.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification de l'abonné: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un abonné à modifier.");
        }
    }

    private void deleteAbonne() {
        int selectedRow = abonneTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cet abonné?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    abonneDAO.deleteAbonne(id);
                    loadAbonnes();
                    JOptionPane.showMessageDialog(this, "Abonné supprimé avec succès.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'abonné: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un abonné à supprimer.");
        }
    }

    private void searchAbonne() {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            try {
                List<Abonne> abonnes = abonneDAO.searchAbonneByName(searchTerm);
                populateTable(abonnes);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la recherche des abonnés: " + e.getMessage());
            }
        } else {
            loadAbonnes(); // Reload all if search term is empty
        }
    }

    private Abonne showAbonneDialog(Abonne abonne) {
        JTextField nomField = new JTextField(20);
        JTextField prenomField = new JTextField(20);
        JTextField telephoneField = new JTextField(20);
        JCheckBox statutCheckBox = new JCheckBox("Actif");

        if (abonne != null) {
            nomField.setText(abonne.getNom());
            prenomField.setText(abonne.getPrenom());
            telephoneField.setText(abonne.getNumeroTelephone());
            statutCheckBox.setSelected(abonne.isStatutSouscription());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Nom:"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom:"));
        panel.add(prenomField);
        panel.add(new JLabel("Téléphone:"));
        panel.add(telephoneField);
        panel.add(new JLabel("Statut:"));
        panel.add(statutCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            abonne == null ? "Ajouter Abonné" : "Modifier Abonné", 
            JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || telephoneField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
                return null;
            }

            Abonne newAbonne = new Abonne();
            newAbonne.setNom(nomField.getText());
            newAbonne.setPrenom(prenomField.getText());
            newAbonne.setDateInscription(java.sql.Date.valueOf(LocalDate.now()));
            newAbonne.setNumeroTelephone(telephoneField.getText());
            newAbonne.setStatutSouscription(statutCheckBox.isSelected());
            if (abonne != null) {
                newAbonne.setId(abonne.getId());
            }
            return newAbonne;
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Abonnes().setVisible(true));
    }
}

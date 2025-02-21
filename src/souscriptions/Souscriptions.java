package souscriptions;

import dao.SouscriptionDAO;
import interfaces.Dashboard;
import Model.Souscription;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.MaskFormatter;
import dao.exceptions.DAOException;

public class Souscriptions extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;
    private SouscriptionDAO souscriptionDAO;
    private Map<String, Integer> abonnes;
    private Map<String, Integer> abonnements;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;

    // Réutilisation de la palette de couleurs
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color LIGHT_BLUE = new Color(66, 139, 202);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);

    public Souscriptions() throws SQLException {
        souscriptionDAO = new SouscriptionDAO();
        abonnes = souscriptionDAO.getAbonnes();
        abonnements = souscriptionDAO.getAbonnements();
        
        setTitle("Gestion des Souscriptions");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // En-tête avec titre et recherche
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Gestion des Souscriptions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // Panel des boutons d'action
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setBackground(BACKGROUND_COLOR);

        JButton addButton = createActionButton("Ajouter", PRIMARY_BLUE);
        JButton editButton = createActionButton("Modifier", LIGHT_BLUE);
        JButton deleteButton = createActionButton("Supprimer", Color.RED);
        JButton returnButton = createActionButton("Retour", Color.GRAY); // Return button
        JButton refreshButton = createActionButton("Rafraîchir", PRIMARY_BLUE); // Refresh button

        buttonContainer.add(addButton);
        buttonContainer.add(editButton);
        buttonContainer.add(deleteButton);
        buttonContainer.add(returnButton);
        buttonContainer.add(refreshButton);

        // Assemblage de l'en-tête
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        headerPanel.add(buttonContainer, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Configuration de la table
        table = new JTable();
        refreshTable();
        
        // Panel de la table avec ombre portée
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(BACKGROUND_COLOR);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JPanel tableWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2d.setColor(BORDER_COLOR);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        tableWrapper.setBackground(CARD_COLOR);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        tableWrapper.add(scrollPane);
        
        tableContainer.add(tableWrapper);
        mainPanel.add(tableContainer, BorderLayout.CENTER);
        
        add(mainPanel);

        // Configuration des actions des boutons
        addButton.addActionListener(e -> {
            try {
                AjouterSouscription dialog = new AjouterSouscription();
                dialog.setVisible(true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        refreshTable();
                    }
                });
            } catch (DAOException ex) {
                Logger.getLogger(Souscriptions.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ouverture du dialogue d'ajout: " + ex.getMessage());
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une souscription à modifier.");
                return;
            }
            
            selectedRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) table.getModel().getValueAt(selectedRow, 3);
            int idAbonne = abonnes.get((String) table.getModel().getValueAt(selectedRow, 0));
            int idAbonnement = abonnements.get((String) table.getModel().getValueAt(selectedRow, 1));
            Date dateDebut = null;
            try {
                dateDebut = dateFormat.parse((String) table.getModel().getValueAt(selectedRow, 2));
            } catch (Exception ex) {
                Logger.getLogger(Souscriptions.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Erreur lors de la lecture de la date: " + ex.getMessage());
                return;
            }

            Souscription souscription = new Souscription(id, idAbonne, idAbonnement, dateDebut, "actif", "", "");
            try {
                ModifierSouscription dialog = new ModifierSouscription(souscription);
                dialog.setVisible(true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        refreshTable();
                    }
                });
            } catch (SQLException ex) {
                Logger.getLogger(Souscriptions.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ouverture du dialogue de modification: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une souscription à supprimer.");
                return;
            }
            
            selectedRow = table.convertRowIndexToModel(selectedRow);
            int id = (int) table.getModel().getValueAt(selectedRow, 3);
            String nomAbonne = (String) table.getModel().getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer la souscription de " + nomAbonne + " ?", 
                "Confirmation de suppression", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    souscriptionDAO.supprimerSouscription(id);
                    JOptionPane.showMessageDialog(this, "Souscription supprimée avec succès.");
                    refreshTable();
                } catch (Exception ex) {
                    Logger.getLogger(Souscriptions.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage());
                }
            }
        });

        returnButton.addActionListener(e -> {
            // Logic to return to the dashboard
            dispose();
            // Assuming Dashboard is another JFrame class
            new Dashboard().setVisible(true);
        });

        refreshButton.addActionListener(e -> {
            refreshTable();
        });
    }

    private void filterTable() {
        String text = searchField.getText().toLowerCase();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
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

    private void refreshTable() {
        try {
            List<Souscription> souscriptions = souscriptionDAO.avoirToutesSouscriptions();
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Abonné", "Abonnement", "Date Début", "ID"}, 
                0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Souscription s : souscriptions) {
                model.addRow(new Object[]{
                    s.getNomAbonne(),
                    s.getNomAbonnement(),
                    s.getDateDebut() != null ? dateFormat.format(s.getDateDebut()) : "",
                    s.getId()
                });
            }

            table.setModel(model);
            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);

            // Personnalisation de l'apparence
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.setRowHeight(40);
            table.setShowGrid(false);
            table.setIntercellSpacing(new Dimension(0, 0));
            table.setSelectionBackground(new Color(232, 240, 254));
            table.setForeground(Color.BLACK); // Set text color to black
            
            JTableHeader header = table.getTableHeader();
            header.setFont(new Font("Segoe UI", Font.BOLD, 14));
            header.setBackground(CARD_COLOR);
            header.setForeground(TEXT_COLOR);
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
            
            // Cacher la colonne ID
            table.getColumnModel().getColumn(3).setMinWidth(0);
            table.getColumnModel().getColumn(3).setMaxWidth(0);
            table.getColumnModel().getColumn(3).setWidth(0);

        } catch (Exception e) {
            Logger.getLogger(Souscriptions.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(this, "Erreur lors du rafraîchissement de la table: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Logger.getLogger(Souscriptions.class.getName()).log(Level.SEVERE, null, e);
        }
        SwingUtilities.invokeLater(() -> {
            try {
                Souscriptions souscriptions = new Souscriptions();
                souscriptions.setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Souscriptions.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Erreur lors du démarrage de l'application: " + ex.getMessage());
            }
        });
    }

    private JFormattedTextField createDateField() {
        JFormattedTextField field = null;
        try {
            MaskFormatter formatter = new MaskFormatter("####-##-##");
            formatter.setPlaceholderCharacter('_');
            field = new JFormattedTextField(formatter);
        } catch (ParseException e) {
            Logger.getLogger(Souscriptions.class.getName()).log(Level.SEVERE, null, e);
            field = new JFormattedTextField();
        }
        return field;
    }
}
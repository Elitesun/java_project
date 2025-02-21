package dao;

import Model.Abonne;
import dao.exceptions.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbonneDAO {
    private static final Logger logger = Logger.getLogger(AbonneDAO.class.getName());

    // Add a new subscriber
    public void addAbonne(Abonne abonne) throws DAOException {
        String query = "INSERT INTO abonne (nom, prenom, date_inscription, numero_telephone, statut_souscription) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, abonne.getNom());
            stmt.setString(2, abonne.getPrenom());
            stmt.setDate(3, new java.sql.Date(abonne.getDateInscription().getTime()));
            stmt.setString(4, abonne.getNumeroTelephone());
            stmt.setBoolean(5, abonne.isStatutSouscription());
            stmt.executeUpdate();
            logger.log(Level.INFO, "Abonne added successfully: {0}", abonne.getNom());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding abonne", e);
            throw new DataAccessException("Failed to add abonne", e);
        }
    }

    // Retrieve all subscribers
    public List<Abonne> getAllAbonnes() throws DAOException {
        List<Abonne> abonnes = new ArrayList<>();
        String query = "SELECT * FROM abonne";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Abonne abonne = new Abonne();
                abonne.setId(rs.getInt("id"));
                abonne.setNom(rs.getString("nom"));
                abonne.setPrenom(rs.getString("prenom"));
                abonne.setDateInscription(rs.getDate("date_inscription"));
                abonne.setNumeroTelephone(rs.getString("numero_telephone"));
                abonne.setStatutSouscription(rs.getBoolean("statut_souscription"));
                abonnes.add(abonne);
            }
            logger.log(Level.INFO, "Retrieved {0} abonnes", abonnes.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving abonnes", e);
            throw new DataAccessException("Failed to retrieve abonnes", e);
        }
        return abonnes;
    }

    // Retrieve a subscriber by ID
    public Abonne getAbonneById(int id) throws DAOException {
        String query = "SELECT * FROM abonne WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Abonne abonne = new Abonne();
                    abonne.setId(rs.getInt("id"));
                    abonne.setNom(rs.getString("nom"));
                    abonne.setPrenom(rs.getString("prenom"));
                    abonne.setDateInscription(rs.getDate("date_inscription"));
                    abonne.setNumeroTelephone(rs.getString("numero_telephone"));
                    abonne.setStatutSouscription(rs.getBoolean("statut_souscription"));
                    return abonne;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving abonne by ID", e);
            throw new DataAccessException("Failed to retrieve abonne by ID", e);
        }
    }

    // Update an existing subscriber
    public void updateAbonne(Abonne abonne) throws DAOException {
        String query = "UPDATE abonne SET nom = ?, prenom = ?, date_inscription = ?, numero_telephone = ?, statut_souscription = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, abonne.getNom());
            stmt.setString(2, abonne.getPrenom());
            stmt.setDate(3, new java.sql.Date(abonne.getDateInscription().getTime()));
            stmt.setString(4, abonne.getNumeroTelephone());
            stmt.setBoolean(5, abonne.isStatutSouscription());
            stmt.setInt(6, abonne.getId());
            stmt.executeUpdate();
            logger.log(Level.INFO, "Abonne updated successfully: {0}", abonne.getId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating abonne", e);
            throw new DataAccessException("Failed to update abonne", e);
        }
    }

    // Delete a subscriber
    public void deleteAbonne(int id) throws DAOException {
        String query = "DELETE FROM abonne WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.log(Level.INFO, "Abonne deleted successfully: {0}", id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting abonne", e);
            throw new DataAccessException("Failed to delete abonne", e);
        }
    }

    // Search for subscribers by name
    public List<Abonne> searchAbonneByName(String name) throws DAOException {
        List<Abonne> abonnes = new ArrayList<>();
        String query = "SELECT * FROM abonne WHERE nom LIKE ? OR prenom LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + name + "%");
            stmt.setString(2, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Abonne abonne = new Abonne();
                    abonne.setId(rs.getInt("id"));
                    abonne.setNom(rs.getString("nom"));
                    abonne.setPrenom(rs.getString("prenom"));
                    abonne.setDateInscription(rs.getDate("date_inscription"));
                    abonne.setNumeroTelephone(rs.getString("numero_telephone"));
                    abonne.setStatutSouscription(rs.getBoolean("statut_souscription"));
                    abonnes.add(abonne);
                }
            }
            logger.log(Level.INFO, "Found {0} abonnes for search term: {1}", new Object[]{abonnes.size(), name});
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching abonnes", e);
            throw new DataAccessException("Failed to search abonnes", e);
        }
        return abonnes;
    }

    // Authenticate a user
    public boolean authentifier(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        }
    }

    // Get the total number of subscribers
    public int getNombreTotalAbonnes() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM abonne";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    // Get the number of active subscribers
    public int getNombreAbonnesActifs() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM abonne WHERE statut_souscription = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    // Get monthly subscription statistics
    public Map<String, Integer> getStatistiquesParMois() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT DATE_FORMAT(date_debut, '%Y-%m') AS mois, COUNT(*) AS total FROM souscription GROUP BY mois";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("mois"), rs.getInt("total"));
            }
        }
        return stats;
    }
}
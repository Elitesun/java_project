package dao;

import Model.Souscription;
import Model.Abonne;
import Model.Abonnement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SouscriptionDAO {
    
    // Add a new subscription
    public void addSouscription(Souscription souscription) throws SQLException {
        String query = "INSERT INTO souscription (id_abonne, id_abonnement, date_debut, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, souscription.getIdAbonne());
            stmt.setInt(2, souscription.getIdAbonnement());
            stmt.setDate(3, new java.sql.Date(souscription.getDateDebut().getTime()));
            stmt.setString(4, souscription.getStatus());
            stmt.executeUpdate();
        }
    }

    // Retrieve all subscriptions
    public List<Souscription> getAllSouscriptions() throws SQLException {
        List<Souscription> souscriptions = new ArrayList<>();
        String query = "SELECT * FROM souscription";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Souscription souscription = new Souscription();
                souscription.setId(rs.getInt("id"));
                souscription.setIdAbonne(rs.getInt("id_abonne"));
                souscription.setIdAbonnement(rs.getInt("id_abonnement"));
                souscription.setDateDebut(rs.getDate("date_debut"));
                souscription.setStatus(rs.getString("status"));
                souscriptions.add(souscription);
            }
        }
        return souscriptions;
    }

    // Update an existing subscription
    public void updateSouscription(Souscription souscription) throws SQLException {
        String query = "UPDATE souscription SET id_abonne = ?, id_abonnement = ?, date_debut = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, souscription.getIdAbonne());
            stmt.setInt(2, souscription.getIdAbonnement());
            stmt.setDate(3, new java.sql.Date(souscription.getDateDebut().getTime()));
            stmt.setString(4, souscription.getStatus());
            stmt.setInt(5, souscription.getId());
            stmt.executeUpdate();
        }
    }

    // Delete a subscription
    public void supprimerSouscription(int id) throws SQLException {
        String query = "DELETE FROM souscription WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Retrieve subscriptions by subscriber ID
    public List<Souscription> getSouscriptionsByAbonneId(int idAbonne) throws SQLException {
        List<Souscription> souscriptions = new ArrayList<>();
        String query = "SELECT * FROM souscription WHERE id_abonne = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idAbonne);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Souscription souscription = new Souscription();
                    souscription.setId(rs.getInt("id"));
                    souscription.setIdAbonne(rs.getInt("id_abonne"));
                    souscription.setIdAbonnement(rs.getInt("id_abonnement"));
                    souscription.setDateDebut(rs.getDate("date_debut"));
                    souscription.setStatus(rs.getString("status"));
                    souscriptions.add(souscription);
                }
            }
        }
        return souscriptions;
    }

    // Retrieve active subscriptions for a specific subscriber
    public List<Souscription> getActiveSouscriptionsByAbonneId(int idAbonne) throws SQLException {
        List<Souscription> souscriptions = new ArrayList<>();
        String query = "SELECT * FROM souscription WHERE id_abonne = ? AND status = 'actif'"; // Assuming you have a status column
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idAbonne);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Souscription souscription = new Souscription();
                    souscription.setId(rs.getInt("id"));
                    souscription.setIdAbonne(rs.getInt("id_abonne"));
                    souscription.setIdAbonnement(rs.getInt("id_abonnement"));
                    souscription.setDateDebut(rs.getDate("date_debut"));
                    souscription.setStatus(rs.getString("status"));
                    souscriptions.add(souscription);
                }
            }
        }
        return souscriptions;
    }

    // Retrieve all subscribers
    public Map<String, Integer> getAbonnes() throws SQLException {
        Map<String, Integer> abonnes = new HashMap<>();
        String query = "SELECT id, nom, prenom FROM abonne";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String fullName = rs.getString("nom") + " " + rs.getString("prenom");
                abonnes.put(fullName, rs.getInt("id"));
            }
        }
        return abonnes;
    }

    // Retrieve all subscriptions
    public Map<String, Integer> getAbonnements() throws SQLException {
        Map<String, Integer> abonnements = new HashMap<>();
        String query = "SELECT id, libelle FROM abonnement";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                abonnements.put(rs.getString("libelle"), rs.getInt("id"));
            }
        }
        return abonnements;
    }

    // Retrieve all subscriptions with details
    public List<Souscription> avoirToutesSouscriptions() throws SQLException {
        List<Souscription> souscriptions = new ArrayList<>();
        String query = "SELECT s.id, a.nom AS nom_abonne, ab.libelle AS nom_abonnement, s.date_debut, s.status " +
                       "FROM souscription s " +
                       "JOIN abonne a ON s.id_abonne = a.id " +
                       "JOIN abonnement ab ON s.id_abonnement = ab.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Souscription souscription = new Souscription();
                souscription.setId(rs.getInt("id"));
                souscription.setNomAbonne(rs.getString("nom_abonne"));
                souscription.setNomAbonnement(rs.getString("nom_abonnement"));
                souscription.setDateDebut(rs.getDate("date_debut"));
                souscription.setStatus(rs.getString("status"));
                souscriptions.add(souscription);
            }
        }
        return souscriptions;
    }
}
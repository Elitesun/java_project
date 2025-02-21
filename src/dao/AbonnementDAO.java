package dao;

import Model.Abonnement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AbonnementDAO {
    
    // Add a new subscription plan
    public static void addAbonnement(Abonnement abonnement) throws SQLException {
        String query = "INSERT INTO abonnement (libelle, duree_mois, prix_mensuel) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, abonnement.getLibelle());
            stmt.setInt(2, abonnement.getDureeMois());
            stmt.setDouble(3, abonnement.getPrixMensuel());
            stmt.executeUpdate();
        }
    }

    // Retrieve all subscription plans
    public static List<Abonnement> getAllAbonnements() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT * FROM abonnement";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id"));
                abonnement.setLibelle(rs.getString("libelle"));
                abonnement.setDureeMois(rs.getInt("duree_mois"));
                abonnement.setPrixMensuel(rs.getDouble("prix_mensuel"));
                abonnements.add(abonnement);
            }
        }
        return abonnements;
    }

    // Update an existing subscription plan
    public static void updateAbonnement(Abonnement abonnement) throws SQLException {
        String query = "UPDATE abonnement SET libelle = ?, duree_mois = ?, prix_mensuel = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, abonnement.getLibelle());
            stmt.setInt(2, abonnement.getDureeMois());
            stmt.setDouble(3, abonnement.getPrixMensuel());
            stmt.setInt(4, abonnement.getId());
            stmt.executeUpdate();
        }
    }

    // Delete a subscription plan
    public static void deleteAbonnement(int id) throws SQLException {
        String query = "DELETE FROM abonnement WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Retrieve a subscription plan by ID
    public static Abonnement getAbonnementById(int idAbonnement) throws SQLException {
        Abonnement abonnement = null;
        String query = "SELECT * FROM abonnement WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idAbonnement);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    abonnement = new Abonnement();
                    abonnement.setId(rs.getInt("id"));
                    abonnement.setLibelle(rs.getString("libelle"));
                    abonnement.setDureeMois(rs.getInt("duree_mois"));
                    abonnement.setPrixMensuel(rs.getDouble("prix_mensuel"));
                }
            }
        }
        return abonnement; // Return the found abonnement or null if not found
    }

    // Search for subscription plans by libelle
    public static List<Abonnement> searchAbonnementByLibelle(String libelle) throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String query = "SELECT * FROM abonnement WHERE libelle LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + libelle + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Abonnement abonnement = new Abonnement();
                    abonnement.setId(rs.getInt("id"));
                    abonnement.setLibelle(rs.getString("libelle"));
                    abonnement.setDureeMois(rs.getInt("duree_mois"));
                    abonnement.setPrixMensuel(rs.getDouble("prix_mensuel"));
                    abonnements.add(abonnement);
                }
            }
        }
        return abonnements;
    }

    // Retrieve the total monthly revenue
    public double getRevenuMensuelTotal() throws SQLException {
        String query = "SELECT SUM(abonnement.prix_mensuel) AS total FROM souscription JOIN abonnement ON souscription.id_abonnement = abonnement.id WHERE DATE_FORMAT(date_debut, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }

    // Get revenue by subscription type
    public Map<String, Double> getRevenusParType() throws SQLException {
        Map<String, Double> revenus = new HashMap<>();
        String query = "SELECT abonnement.libelle, SUM(abonnement.prix_mensuel) AS total FROM souscription JOIN abonnement ON souscription.id_abonnement = abonnement.id GROUP BY abonnement.libelle";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                revenus.put(rs.getString("libelle"), rs.getDouble("total"));
            }
        }
        return revenus;
    }
}
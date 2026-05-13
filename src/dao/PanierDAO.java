package dao;

import database.DatabaseConnection;
import model.LigneCommande;
import model.Panier;
import model.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierDAO {

    // Ajouter ou mettre à jour un produit dans le panier persistant
    public boolean ajouterOuMettreAJour(int userId, int produitId, int quantite) {
        String sql = "INSERT INTO panier (user_id, produit_id, quantite) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantite = quantite + ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, produitId);
            ps.setInt(3, quantite);
            ps.setInt(4, quantite);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur ajout panier BD: " + e.getMessage());
        }
        return false;
    }

    // Modifier la quantité d'un produit dans le panier
    public boolean modifierQuantite(int userId, int produitId, int nouvelleQuantite) {
        if (nouvelleQuantite <= 0) {
            return supprimer(userId, produitId);
        }
        String sql = "UPDATE panier SET quantite = ? WHERE user_id = ? AND produit_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nouvelleQuantite);
            ps.setInt(2, userId);
            ps.setInt(3, produitId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modification quantité panier: " + e.getMessage());
        }
        return false;
    }

    // Supprimer un produit du panier
    public boolean supprimer(int userId, int produitId) {
        String sql = "DELETE FROM panier WHERE user_id = ? AND produit_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, produitId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur suppression panier: " + e.getMessage());
        }
        return false;
    }

    // Vider tout le panier d'un utilisateur
    public boolean vider(int userId) {
        String sql = "DELETE FROM panier WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur vidage panier: " + e.getMessage());
        }
        return false;
    }

    // Charger le panier depuis la base de données
    public Panier chargerPanier(int userId) {
        Panier panier = new Panier();
        String sql = "SELECT p.id, p.nom, p.description, p.prix, p.stock, pa.quantite " +
                "FROM panier pa " +
                "JOIN produits p ON pa.produit_id = p.id " +
                "WHERE pa.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Produit prod = new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getInt("stock")
                );
                int quantite = rs.getInt("quantite");
                panier.ajouterProduitSilencieux(prod, quantite);
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement panier: " + e.getMessage());
        }
        return panier;
    }
}
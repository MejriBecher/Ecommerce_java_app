package dao;

import database.DatabaseConnection;
import model.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public boolean ajouter(Produit produit) {
        String sql = "INSERT INTO produits (nom, description, prix, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setDouble(3, produit.getPrix());
            ps.setInt(4, produit.getStock());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) produit.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajout produit: " + e.getMessage());
        }
        return false;
    }

    public boolean modifier(Produit produit) {
        String sql = "UPDATE produits SET nom=?, description=?, prix=?, stock=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setDouble(3, produit.getPrix());
            ps.setInt(4, produit.getStock());
            ps.setInt(5, produit.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modification produit: " + e.getMessage());
        }
        return false;
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM produits WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur suppression produit: " + e.getMessage());
        }
        return false;
    }

    public Produit findById(int id) {
        String sql = "SELECT * FROM produits WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Produit(rs.getInt("id"), rs.getString("nom"),
                        rs.getString("description"), rs.getDouble("prix"), rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById produit: " + e.getMessage());
        }
        return null;
    }

    public List<Produit> getTousLesProduits() {
        List<Produit> liste = new ArrayList<>();
        String sql = "SELECT * FROM produits";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new Produit(rs.getInt("id"), rs.getString("nom"),
                        rs.getString("description"), rs.getDouble("prix"), rs.getInt("stock")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur liste produits: " + e.getMessage());
        }
        return liste;
    }

    public boolean mettreAJourStock(int produitId, int nouvelleQuantite) {
        String sql = "UPDATE produits SET stock=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nouvelleQuantite);
            ps.setInt(2, produitId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour stock: " + e.getMessage());
        }
        return false;
    }
}
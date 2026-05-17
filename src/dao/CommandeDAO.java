package dao;

import database.DatabaseConnection;
import model.Commande;
import model.LigneCommande;
import model.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {

    public boolean creerCommande(Commande commande) {
        String sqlCommande = "INSERT INTO commandes (user_id, date, total, statut) VALUES (?, ?, ?, ?)";
        String sqlLigne = "INSERT INTO ligne_commande (commande_id, produit_id, quantite, sous_total) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Transaction

            // Insérer la commande
            PreparedStatement psCmd = conn.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS);
            psCmd.setInt(1, commande.getUserId());
            psCmd.setDate(2, Date.valueOf(commande.getDate()));
            psCmd.setDouble(3, commande.getTotal());
            psCmd.setString(4, commande.getStatut().name());
            psCmd.executeUpdate();

            ResultSet rs = psCmd.getGeneratedKeys();
            if (rs.next()) commande.setId(rs.getInt(1));

            // Insérer les lignes de commande
            PreparedStatement psLigne = conn.prepareStatement(sqlLigne);
            ProduitDAO produitDAO = new ProduitDAO();

            for (LigneCommande ligne : commande.getLignes()) {
                psLigne.setInt(1, commande.getId());
                psLigne.setInt(2, ligne.getProduit().getId());
                psLigne.setInt(3, ligne.getQuantite());
                psLigne.setDouble(4, ligne.getSousTotal());
                psLigne.addBatch();

                // Mettre à jour le stock
                int nouveauStock = ligne.getProduit().getStock() - ligne.getQuantite();
                produitDAO.mettreAJourStock(ligne.getProduit().getId(), nouveauStock);
            }
            psLigne.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Erreur création commande: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return false;
    }

    public List<Commande> getCommandesByUser(int userId) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commandes WHERE user_id=? ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Commande c = new Commande();
                c.setId(rs.getInt("id"));
                c.setUserId(rs.getInt("user_id"));
                c.setDate(rs.getDate("date").toLocalDate());
                c.setTotal(rs.getDouble("total"));
                c.setStatut(Commande.Statut.valueOf(rs.getString("statut")));
                c.setLignes(getLignesCommande(c.getId()));
                commandes.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erreur liste commandes: " + e.getMessage());
        }
        return commandes;
    }

    public List<Commande> getToutesLesCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commandes ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Commande c = new Commande();
                c.setId(rs.getInt("id"));
                c.setUserId(rs.getInt("user_id"));
                c.setDate(rs.getDate("date").toLocalDate());
                c.setTotal(rs.getDouble("total"));
                c.setStatut(Commande.Statut.valueOf(rs.getString("statut")));
                commandes.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erreur liste commandes admin: " + e.getMessage());
        }
        return commandes;
    }

    public boolean updateStatut(int commandeId, Commande.Statut statut) {
        String sql = "UPDATE commandes SET statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setInt(2, commandeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour statut commande #" + commandeId + ": " + e.getMessage());
        }
        return false;
    }

    private List<LigneCommande> getLignesCommande(int commandeId) {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT lc.*, p.nom, p.prix, p.stock, p.description " +
                "FROM ligne_commande lc JOIN produits p ON lc.produit_id = p.id " +
                "WHERE lc.commande_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Produit p = new Produit(rs.getInt("produit_id"), rs.getString("nom"),
                        rs.getString("description"), rs.getDouble("prix"), rs.getInt("stock"));
                LigneCommande lc = new LigneCommande(p, rs.getInt("quantite"));
                lc.setId(rs.getInt("id"));
                lc.setCommandeId(commandeId);
                lc.setSousTotal(rs.getDouble("sous_total"));
                lignes.add(lc);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lignes commande: " + e.getMessage());
        }
        return lignes;
    }
}
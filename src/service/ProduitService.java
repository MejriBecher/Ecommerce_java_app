package service;

import dao.ProduitDAO;
import model.Produit;

import java.util.List;

public class ProduitService {
    private final ProduitDAO produitDAO = new ProduitDAO();

    public boolean ajouterProduit(String nom, String desc, double prix, int stock) {
        if (prix <= 0) { System.out.println("❌ Le prix doit être positif."); return false; }
        if (stock < 0)  { System.out.println("❌ Le stock ne peut pas être négatif."); return false; }
        Produit p = new Produit(nom, desc, prix, stock);
        boolean ok = produitDAO.ajouter(p);
        if (ok) System.out.println("✅ Produit ajouté : " + p);
        return ok;
    }

    public boolean modifierProduit(int id, String nom, String desc, double prix, int stock) {
        Produit p = new Produit(id, nom, desc, prix, stock);
        boolean ok = produitDAO.modifier(p);
        if (ok) System.out.println("✅ Produit modifié.");
        else    System.out.println("❌ Produit introuvable.");
        return ok;
    }

    public boolean supprimerProduit(int id) {
        boolean ok = produitDAO.supprimer(id);
        if (ok) System.out.println("✅ Produit supprimé.");
        else    System.out.println("❌ Impossible de supprimer ce produit.");
        return ok;
    }

    public List<Produit> listerProduits() {
        return produitDAO.getTousLesProduits();
    }

    public Produit trouverProduit(int id) {
        return produitDAO.findById(id);
    }

    public void afficherProduits() {
        List<Produit> produits = listerProduits();
        if (produits.isEmpty()) {
            System.out.println("📦 Aucun produit disponible.");
            return;
        }
        System.out.println("\n========== 📦 CATALOGUE PRODUITS ==========");
        System.out.printf("%-5s %-20s %-30s %-10s %-6s%n", "ID", "Nom", "Description", "Prix", "Stock");
        System.out.println("-".repeat(75));
        for (Produit p : produits) {
            System.out.printf("%-5d %-20s %-30s %-10.2f %-6d%n",
                    p.getId(), p.getNom(), p.getDescription(), p.getPrix(), p.getStock());
        }
        System.out.println("===========================================\n");
    }
}
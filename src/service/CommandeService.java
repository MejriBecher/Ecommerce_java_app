package service;

import dao.CommandeDAO;
import model.Commande;
import model.LigneCommande;
import model.Panier;
import model.Produit;

import java.util.List;

public class CommandeService {
    private final CommandeDAO commandeDAO = new CommandeDAO();

    public boolean passerCommande(int userId, Panier panier) {
        if (panier.estVide()) {
            System.out.println("❌ Votre panier est vide.");
            return false;
        }

        // Vérifier le stock
        for (LigneCommande ligne : panier.getItems()) {
            Produit p = ligne.getProduit();
            if (p.getStock() < ligne.getQuantite()) {
                System.out.printf("❌ Stock insuffisant pour '%s' (stock disponible: %d)%n",
                        p.getNom(), p.getStock());
                return false;
            }
        }

        Commande commande = new Commande(userId);
        commande.setLignes(panier.getItems());
        commande.calculerTotal();

        boolean ok = commandeDAO.creerCommande(commande);
        if (ok) {
            System.out.println("✅ Commande #" + commande.getId() + " passée avec succès !");
            System.out.printf("   Total: %.2f DT%n", commande.getTotal());
            panier.vider();
        } else {
            System.out.println("❌ Erreur lors de la validation de la commande.");
        }
        return ok;
    }

    public void afficherMesCommandes(int userId) {
        List<Commande> commandes = commandeDAO.getCommandesByUser(userId);
        if (commandes.isEmpty()) {
            System.out.println("📋 Vous n'avez aucune commande.");
            return;
        }
        System.out.println("\n========== 📋 MES COMMANDES ==========");
        for (Commande c : commandes) {
            System.out.println(c);
            c.getLignes().forEach(System.out::println);
            System.out.println("--------------------------------------");
        }
    }

    public void afficherToutesLesCommandes() {
        List<Commande> commandes = commandeDAO.getToutesLesCommandes();
        if (commandes.isEmpty()) {
            System.out.println("📋 Aucune commande enregistrée.");
            return;
        }
        System.out.println("\n========== 📋 TOUTES LES COMMANDES ==========");
        for (Commande c : commandes) {
            System.out.printf("Commande #%d | User:%d | %s | %.2f DT | %s%n",
                    c.getId(), c.getUserId(), c.getDate(), c.getTotal(), c.getStatut());
        }
        System.out.println("==============================================");
    }
}
package ui;

import dao.PanierDAO;
import model.Panier;
import model.Produit;
import service.AuthService;
import service.CommandeService;
import service.ProduitService;

import java.util.Scanner;

public class ClientMenu {
    private final Scanner sc;
    private final AuthService auth;
    private final ProduitService produitService;
    private final CommandeService commandeService;
    private final PanierDAO panierDAO;
    private Panier panier;

    public ClientMenu(Scanner sc, AuthService auth) {
        this.sc = sc;
        this.auth = auth;
        this.produitService = new ProduitService();
        this.commandeService = new CommandeService();
        this.panierDAO = new PanierDAO();

        // Charger le panier depuis la base de données à la connexion
        int userId = auth.getUtilisateurConnecte().getId();
        this.panier = panierDAO.chargerPanier(userId);

        if (!panier.estVide()) {
            System.out.println("🛒 Panier restauré depuis votre dernière session.");
            panier.afficher();
        }
    }

    public void afficher() {
        int choix = -1;
        while (choix != 0) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║      🛍️  ESPACE CLIENT        ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║ 1. Voir les produits         ║");
            System.out.println("║ 2. Ajouter au panier         ║");
            System.out.println("║ 3. Voir mon panier           ║");
            System.out.println("║ 4. Modifier quantité panier  ║");
            System.out.println("║ 5. Retirer du panier         ║");
            System.out.println("║ 6. Valider commande          ║");
            System.out.println("║ 7. Mes commandes             ║");
            System.out.println("║ 0. Se déconnecter            ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Votre choix : ");

            try {
                choix = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                choix = -1;
            }

            switch (choix) {
                case 1 -> produitService.afficherProduits();
                case 2 -> ajouterAuPanier();
                case 3 -> panier.afficher();
                case 4 -> modifierQuantite();
                case 5 -> retirerDuPanier();
                case 6 -> validerCommande();
                case 7 -> commandeService.afficherMesCommandes(auth.getUtilisateurConnecte().getId());
                case 0 -> auth.deconnecter();
                default -> System.out.println("❌ Choix invalide.");
            }
        }
    }

    private void ajouterAuPanier() {
        produitService.afficherProduits();
        System.out.print("ID du produit : ");
        int id;
        try { id = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("❌ ID invalide."); return; }

        Produit p = produitService.trouverProduit(id);
        if (p == null) { System.out.println("❌ Produit introuvable."); return; }
        if (p.getStock() == 0) { System.out.println("❌ Produit en rupture de stock."); return; }

        System.out.print("Quantité : ");
        int qte;
        try { qte = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("❌ Quantité invalide."); return; }

        if (qte <= 0) { System.out.println("❌ La quantité doit être positive."); return; }
        if (qte > p.getStock()) {
            System.out.println("⚠️  Stock insuffisant. Max disponible : " + p.getStock());
            return;
        }

        // Sauvegarder en BD ET mettre à jour le panier en mémoire
        int userId = auth.getUtilisateurConnecte().getId();
        boolean ok = panierDAO.ajouterOuMettreAJour(userId, p.getId(), qte);
        if (ok) {
            panier.ajouterProduit(p, qte);
        } else {
            System.out.println("❌ Erreur lors de la sauvegarde du panier.");
        }
    }

    private void modifierQuantite() {
        panier.afficher();
        if (panier.estVide()) return;

        System.out.print("ID du produit à modifier : ");
        int id;
        try { id = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("❌ ID invalide."); return; }

        System.out.print("Nouvelle quantité (0 pour supprimer) : ");
        int qte;
        try { qte = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("❌ Quantité invalide."); return; }

        int userId = auth.getUtilisateurConnecte().getId();
        panierDAO.modifierQuantite(userId, id, qte);
        panier.modifierQuantite(id, qte);
    }

    private void retirerDuPanier() {
        panier.afficher();
        if (panier.estVide()) return;

        System.out.print("ID du produit à retirer : ");
        int id;
        try { id = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("❌ ID invalide."); return; }

        int userId = auth.getUtilisateurConnecte().getId();
        panierDAO.supprimer(userId, id);
        panier.supprimerProduit(id);
    }

    private void validerCommande() {
        int userId = auth.getUtilisateurConnecte().getId();
        boolean ok = commandeService.passerCommande(userId, panier);
        if (ok) {
            // Vider le panier en BD après commande validée
            panierDAO.vider(userId);
            panier.viderSilencieux();
        }
    }
}
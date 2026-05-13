package ui;

import service.AuthService;
import service.CommandeService;
import service.ProduitService;

import java.util.Scanner;

public class AdminMenu {
    private final Scanner sc;
    private final AuthService auth;
    private final ProduitService produitService;
    private final CommandeService commandeService;

    public AdminMenu(Scanner sc, AuthService auth) {
        this.sc = sc;
        this.auth = auth;
        this.produitService = new ProduitService();
        this.commandeService = new CommandeService();
    }

    public void afficher() {
        int choix = -1;
        while (choix != 0) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║    🛠️  MENU ADMINISTRATEUR    ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║ 1. Ajouter un produit        ║");
            System.out.println("║ 2. Modifier un produit       ║");
            System.out.println("║ 3. Supprimer un produit      ║");
            System.out.println("║ 4. Lister les produits       ║");
            System.out.println("║ 5. Voir toutes les commandes ║");
            System.out.println("║ 0. Se déconnecter            ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Votre choix : ");

            try {
                choix = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                choix = -1;
            }

            switch (choix) {
                case 1 -> ajouterProduit();
                case 2 -> modifierProduit();
                case 3 -> supprimerProduit();
                case 4 -> produitService.afficherProduits();
                case 5 -> commandeService.afficherToutesLesCommandes();
                case 0 -> auth.deconnecter();
                default -> System.out.println("❌ Choix invalide.");
            }
        }
    }

    private void ajouterProduit() {
        System.out.println("\n--- Ajouter un Produit ---");
        System.out.print("Nom : "); String nom = sc.nextLine();
        System.out.print("Description : "); String desc = sc.nextLine();
        System.out.print("Prix : "); double prix = Double.parseDouble(sc.nextLine());
        System.out.print("Stock : "); int stock = Integer.parseInt(sc.nextLine());
        produitService.ajouterProduit(nom, desc, prix, stock);
    }

    private void modifierProduit() {
        produitService.afficherProduits();
        System.out.print("ID du produit à modifier : "); int id = Integer.parseInt(sc.nextLine());
        System.out.print("Nouveau nom : "); String nom = sc.nextLine();
        System.out.print("Nouvelle description : "); String desc = sc.nextLine();
        System.out.print("Nouveau prix : "); double prix = Double.parseDouble(sc.nextLine());
        System.out.print("Nouveau stock : "); int stock = Integer.parseInt(sc.nextLine());
        produitService.modifierProduit(id, nom, desc, prix, stock);
    }

    private void supprimerProduit() {
        produitService.afficherProduits();
        System.out.print("ID du produit à supprimer : "); int id = Integer.parseInt(sc.nextLine());
        System.out.print("Confirmer la suppression ? (oui/non) : ");
        if ("oui".equalsIgnoreCase(sc.nextLine().trim())) {
            produitService.supprimerProduit(id);
        }
    }
}
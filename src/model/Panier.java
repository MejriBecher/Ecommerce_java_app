package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Panier {
    private List<LigneCommande> items;

    public Panier() {
        this.items = new ArrayList<>();
    }

    // Ajouter avec message console (utilisé par le client)
    public void ajouterProduit(Produit produit, int quantite) {
        for (LigneCommande ligne : items) {
            if (ligne.getProduit().getId() == produit.getId()) {
                ligne.setQuantite(ligne.getQuantite() + quantite);
                System.out.println("✅ Quantité mise à jour pour: " + produit.getNom());
                return;
            }
        }
        items.add(new LigneCommande(produit, quantite));
        System.out.println("✅ Produit ajouté au panier: " + produit.getNom());
    }

    // Ajouter sans message console (utilisé au chargement depuis la BD)
    public void ajouterProduitSilencieux(Produit produit, int quantite) {
        for (LigneCommande ligne : items) {
            if (ligne.getProduit().getId() == produit.getId()) {
                ligne.setQuantite(ligne.getQuantite() + quantite);
                return;
            }
        }
        items.add(new LigneCommande(produit, quantite));
    }

    public void supprimerProduit(int produitId) {
        Iterator<LigneCommande> it = items.iterator();
        while (it.hasNext()) {
            if (it.next().getProduit().getId() == produitId) {
                it.remove();
                System.out.println("🗑️  Produit retiré du panier.");
                return;
            }
        }
        System.out.println("⚠️  Produit non trouvé dans le panier.");
    }

    public void modifierQuantite(int produitId, int nouvelleQuantite) {
        for (LigneCommande ligne : items) {
            if (ligne.getProduit().getId() == produitId) {
                if (nouvelleQuantite <= 0) {
                    supprimerProduit(produitId);
                } else {
                    ligne.setQuantite(nouvelleQuantite);
                    System.out.println("✅ Quantité modifiée.");
                }
                return;
            }
        }
        System.out.println("⚠️  Produit non trouvé dans le panier.");
    }

    public double calculerTotal() {
        return items.stream().mapToDouble(LigneCommande::getSousTotal).sum();
    }

    public void vider() {
        items.clear();
        System.out.println("🗑️  Panier vidé.");
    }

    public void viderSilencieux() {
        items.clear();
    }

    public boolean estVide() {
        return items.isEmpty();
    }

    public List<LigneCommande> getItems() { return items; }

    public void afficher() {
        if (items.isEmpty()) {
            System.out.println("🛒 Votre panier est vide.");
            return;
        }
        System.out.println("\n========== 🛒 PANIER ==========");
        items.forEach(System.out::println);
        System.out.printf("💰 Total: %.2f DT%n", calculerTotal());
        System.out.println("================================");
    }
}
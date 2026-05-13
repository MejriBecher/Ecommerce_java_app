package model;

public class LigneCommande {
    private int id;
    private int commandeId;
    private Produit produit;
    private int quantite;
    private double sousTotal;

    public LigneCommande() {}

    public LigneCommande(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
        this.sousTotal = produit.getPrix() * quantite;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCommandeId() { return commandeId; }
    public void setCommandeId(int commandeId) { this.commandeId = commandeId; }

    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) {
        this.quantite = quantite;
        if (this.produit != null)
            this.sousTotal = this.produit.getPrix() * quantite;
    }

    public double getSousTotal() { return sousTotal; }
    public void setSousTotal(double sousTotal) { this.sousTotal = sousTotal; }

    @Override
    public String toString() {
        return String.format("  - %s x%d = %.2f DT", produit.getNom(), quantite, sousTotal);
    }
}
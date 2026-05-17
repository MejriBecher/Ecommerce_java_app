package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    public enum Statut { EN_ATTENTE, VALIDEE, ANNULEE, LIVREE }

    private int id;
    private int userId;
    private LocalDate date;
    private double total;
    private Statut statut;
    private List<LigneCommande> lignes;

    public Commande() {
        this.lignes = new ArrayList<>();
        this.date = LocalDate.now();
        this.statut = Statut.EN_ATTENTE;
    }

    public Commande(int userId) {
        this();
        this.userId = userId;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public List<LigneCommande> getLignes() { return lignes; }
    public void setLignes(List<LigneCommande> lignes) { this.lignes = lignes; }

    public void calculerTotal() {
        this.total = lignes.stream().mapToDouble(LigneCommande::getSousTotal).sum();
    }

    @Override
    public String toString() {
        return String.format("Commande{id=%d, date=%s, total=%.2f DT, statut=%s}", id, date, total, statut);
    }
}
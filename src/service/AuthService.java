package service;

import dao.UserDAO;
import model.Utilisateur;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private Utilisateur utilisateurConnecte = null;

    public boolean inscrire(String nom, String email, String motDePasse) {
        if (nom == null || nom.isBlank()) {
            System.out.println("❌ Le nom ne peut pas être vide.");
            return false;
        }
        if (!email.contains("@")) {
            System.out.println("❌ Email invalide.");
            return false;
        }
        if (motDePasse == null || motDePasse.length() < 4) {
            System.out.println("❌ Mot de passe trop court (min 4 caractères).");
            return false;
        }
        if (userDAO.emailExiste(email)) {
            System.out.println("❌ Cet email est déjà utilisé.");
            return false;
        }
        Utilisateur user = new Utilisateur(nom, email, motDePasse, "CLIENT");
        boolean ok = userDAO.inscrire(user);
        if (ok) System.out.println("✅ Inscription réussie ! Bienvenue " + nom);
        return ok;
    }

    public boolean connecter(String email, String motDePasse) {
        Utilisateur user = userDAO.connecter(email, motDePasse);
        if (user != null) {
            utilisateurConnecte = user;
            System.out.println("✅ Connecté en tant que " + user.getNom() + " [" + user.getRole() + "]");
            return true;
        }
        System.out.println("❌ Email ou mot de passe incorrect.");
        return false;
    }

    public void deconnecter() {
        utilisateurConnecte = null;
        System.out.println("👋 Déconnexion réussie.");
    }

    public boolean estConnecte() { return utilisateurConnecte != null; }
    public boolean estAdmin() { return estConnecte() && "ADMIN".equals(utilisateurConnecte.getRole()); }
    public boolean estClient() { return estConnecte() && "CLIENT".equals(utilisateurConnecte.getRole()); }
    public Utilisateur getUtilisateurConnecte() { return utilisateurConnecte; }
}
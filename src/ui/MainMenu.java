package ui;

import service.AuthService;

import java.util.Scanner;

public class MainMenu {
    private final Scanner sc = new Scanner(System.in);
    private final AuthService auth = new AuthService();

    public void lancer() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   🛒  BIENVENUE SUR E-COMMERCE     ║");
        System.out.println("╚════════════════════════════════════╝");

        int choix = -1;
        while (choix != 0) {
            if (!auth.estConnecte()) {
                System.out.println("\n╔══════════════════════╗");
                System.out.println("║    MENU PRINCIPAL     ║");
                System.out.println("╠══════════════════════╣");
                System.out.println("║ 1. Se connecter       ║");
                System.out.println("║ 2. S'inscrire         ║");
                System.out.println("║ 0. Quitter            ║");
                System.out.println("╚══════════════════════╝");
                System.out.print("Votre choix : ");

                try {
                    choix = Integer.parseInt(sc.nextLine().trim());
                } catch (NumberFormatException e) { choix = -1; }

                switch (choix) {
                    case 1 -> seConnecter();
                    case 2 -> sInscrire();
                    case 0 -> System.out.println("Au revoir ! 👋");
                    default -> System.out.println("❌ Choix invalide.");
                }
            } else {
                // Rediriger selon le rôle
                if (auth.estAdmin()) {
                    new AdminMenu(sc, auth).afficher();
                } else {
                    new ClientMenu(sc, auth).afficher();
                }
                choix = 1; // Rester dans la boucle principale
            }
        }
        sc.close();
    }

    private void seConnecter() {
        System.out.print("Email : "); String email = sc.nextLine();
        System.out.print("Mot de passe : "); String mdp = sc.nextLine();
        auth.connecter(email, mdp);
    }

    private void sInscrire() {
        System.out.print("Nom : "); String nom = sc.nextLine();
        System.out.print("Email : "); String email = sc.nextLine();
        System.out.print("Mot de passe : "); String mdp = sc.nextLine();
        auth.inscrire(nom, email, mdp);
    }
}
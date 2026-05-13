# STRUCTURE.md — Directory Layout & Organization

Last updated: 2026-05-13

## Top-Level Layout

```
ECommerceJava/
├── .gitignore                  # IntelliJ + Eclipse + VS Code ignores
├── ECommerceJava.iml           # IntelliJ IDEA module descriptor
├── .idea/                      # IntelliJ project settings
│   ├── misc.xml                # JDK 23 config
│   ├── modules.xml             # Module registry
│   ├── workspace.xml           # IDE workspace state
│   ├── .gitignore              # IDE-local ignores
│   └── libraries/
│       └── mysql_connector_j_9_6_0.xml  # MySQL JAR dependency definition
├── resources/
│   └── ecommerce_db.sql        # Database schema + seed data
├── out/                        # Compiled bytecode (gitignored)
└── src/                        # Source root
    ├── Main.java               # Application entry point
    ├── database/               # Database connection layer
    ├── model/                  # Domain entities
    ├── dao/                    # Data access objects
    ├── service/                # Business logic
    ├── servlet/                # Empty — reserved
    └── ui/                     # Console interface
```

## Source Package Details

### `src/` (root package)
| File | Purpose |
|------|---------|
| `Main.java` | `public static void main(String[])` — application entry |

### `src/database/`
| File | Purpose |
|------|---------|
| `DatabaseConnection.java` | JDBC connection factory. Provides `getConnection()` static method |

### `src/model/`
| File | Entity | Fields |
|------|--------|--------|
| `Utilisateur.java` | User | id, nom, email, motDePasse, role |
| `Produit.java` | Product | id, nom, description, prix, stock |
| `Commande.java` | Order | id, userId, date, total, statut, lignes (List) |
| `LigneCommande.java` | Order line | id, commandeId, produit, quantite, sousTotal |
| `Panier.java` | Shopping cart | items (List of LigneCommande) |

### `src/dao/`
| File | Target Table | Key Methods |
|------|-------------|-------------|
| `UserDAO.java` | `utilisateurs` | inscrire, connecter, emailExiste, getTousLesUtilisateurs |
| `ProduitDAO.java` | `produits` | ajouter, modifier, supprimer, findById, getTousLesProduits, mettreAJourStock |
| `CommandeDAO.java` | `commandes`, `ligne_commande` | creerCommande, getCommandesByUser, getToutesLesCommandes |
| `PanierDAO.java` | `panier` | ajouterOuMettreAJour, modifierQuantite, supprimer, vider, chargerPanier |

### `src/service/`
| File | Dependencies | Purpose |
|------|-------------|---------|
| `AuthService.java` | UserDAO | Registration, login, session state, role checks |
| `ProduitService.java` | ProduitDAO | Product CRUD with validation |
| `CommandeService.java` | CommandeDAO | Order placement with stock checks |

### `src/ui/`
| File | Routes To | Purpose |
|------|-----------|---------|
| `MainMenu.java` | AdminMenu / ClientMenu | Login/register menu, role-based routing |
| `AdminMenu.java` | — | Admin: product CRUD, view all orders |
| `ClientMenu.java` | — | Client: browse, cart, checkout, order history |

### `src/servlet/`
**Empty.** No files. This package exists in the directory tree but has no Java source files. Likely reserved for future web migration (e.g., Java Servlets/JSP).

## Naming Conventions (Directories & Files)

- **Package names**: lowercase, single word (`model`, `dao`, `service`, `ui`, `database`)
- **Source files**: PascalCase matching public class name (`ProduitDAO.java` → `class ProduitDAO`)
- **SQL file**: lowercase with underscores (`ecommerce_db.sql`)
- **IML file**: Project name in PascalCase (`ECommerceJava.iml`)

## Configuration Files

| File | What It Configures |
|------|-------------------|
| `.gitignore` | Ignores `out/`, IDE directories, OS files |
| `ECommerceJava.iml` | Module: source root = `src/`, JDK, library deps |
| `.idea/misc.xml` | JDK 23, output to `out/` |
| `.idea/libraries/mysql_connector_j_9_6_0.xml` | Path to MySQL Connector JAR |
| `resources/ecommerce_db.sql` | Full schema + test data (run once to initialize DB) |

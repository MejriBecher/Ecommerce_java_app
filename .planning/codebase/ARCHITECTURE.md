# ARCHITECTURE.md — System Architecture

Last updated: 2026-05-13

## Architectural Pattern

**Layered architecture** with 5 horizontal layers. The application follows a strict top-down dependency flow. No dependency injection framework — all dependencies are instantiated manually with `new`.

```
┌─────────────────────────────────────────────────────────┐
│                     UI Layer (console)                   │
│   MainMenu / AdminMenu / ClientMenu                      │
│   (Scanner input, display output via System.out)         │
├─────────────────────────────────────────────────────────┤
│                    Service Layer                          │
│   AuthService / ProduitService / CommandeService          │
│   (Business logic, validation, orchestration)            │
├─────────────────────────────────────────────────────────┤
│                      DAO Layer                           │
│   UserDAO / ProduitDAO / CommandeDAO / PanierDAO          │
│   (SQL queries, CRUD operations)                         │
├─────────────────────────────────────────────────────────┤
│                   Database Layer                          │
│   DatabaseConnection                                     │
│   (JDBC connection factory, MySQL driver)                │
├─────────────────────────────────────────────────────────┤
│                     Model Layer                           │
│   Utilisateur / Produit / Commande / LigneCommande / Panier│
│   (Plain Java objects, no persistence annotations)       │
└─────────────────────────────────────────────────────────┘
```

## Layer Details

### 1. Entry Point — `src/Main.java`
- Single `main()` method — creates `MainMenu` and calls `lancer()`
- No command-line arguments processed

### 2. UI Layer — `src/ui/`
- **MainMenu** (`MainMenu.java:7-63`) — Login/register loop, routes to AdminMenu or ClientMenu based on role
- **AdminMenu** (`AdminMenu.java:9-82`) — Product CRUD + view all orders
- **ClientMenu** (`ClientMenu.java:12-148`) — Browse products, manage cart, place orders, view order history
- All menus use `Scanner` for input and `System.out` for output
- Menu loops run until user explicitly logs out

### 3. Service Layer — `src/service/`
- **AuthService** (`AuthService.java:6-53`) — Register, login, logout, session state, role checks
- **ProduitService** (`ProduitService.java:8-58`) — Product CRUD with input validation, formatted display
- **CommandeService** (`CommandeService.java:11-72`) — Order creation (stock check, total calc), order listing
- Services contain business logic but also print user-facing messages directly (UI concern leak)

### 4. DAO Layer — `src/dao/`
- **UserDAO** (`UserDAO.java:10-89`) — `inscrire()`, `connecter()`, `emailExiste()`, `getTousLesUtilisateurs()`
- **ProduitDAO** (`ProduitDAO.java:10-108`) — Full CRUD + `mettreAJourStock()`
- **CommandeDAO** (`CommandeDAO.java:12-135`) — `creerCommande()` with transaction, order queries
- **PanierDAO** (`PanierDAO.java:12-104`) — Persistent cart: add/update/remove/clear/load
- Each DAO method opens its own connection (no connection pooling)
- SQL is inline in Java strings (no query builders or ORM)

### 5. Database Layer — `src/database/`
- **DatabaseConnection** (`DatabaseConnection.java:7-26`) — Singleton-style utility with private constructor
- Static `getConnection()` returns a new `Connection` each time
- MySQL JDBC URL, credentials hardcoded as constants

### 6. Model Layer — `src/model/`
- **Utilisateur** — User entity with role string
- **Produit** — Product entity with price/stock
- **Commande** — Order entity with `Statut` enum, `List<LigneCommande>`, total calculation
- **LigneCommande** — Order line with product reference, quantity, subtotal calculation
- **Panier** — In-memory cart with `List<LigneCommande>`, add/remove/update/clear operations
- All models are POJOs with constructors, getters/setters, and `toString()`

## Data Flow

### Authentication Flow
```
User Input → MainMenu → AuthService.connecter() → UserDAO.connecter() → MySQL
                                                      ↓
                                              AuthService stores Utilisateur (in-memory session)
                                                      ↓
                                              MainMenu routes to AdminMenu or ClientMenu
```

### Order Placement Flow
```
ClientMenu.validerCommande()
  → CommandeService.passerCommande(userId, panier)
    → Stock check (reads Produit.stock for each line)
    → CommandeDAO.creerCommande(commande)
      → BEGIN TRANSACTION
        → INSERT INTO commandes
        → INSERT INTO ligne_commande (batch)
        → UPDATE produits SET stock = stock - quantite
      → COMMIT (or ROLLBACK on error)
    → PanierDAO.vider(userId) (clear persistent cart)
    → panier.viderSilencieux() (clear in-memory cart)
```

### Cart Flow (Persistent)
```
ClientMenu constructor
  → PanierDAO.chargerPanier(userId)
    → JOIN panier + produits → read rows
    → Panier.ajouterProduitSilencieux() (no console output)

On add/modify/remove:
  → PanierDAO.ajouterOuMettreAJour() / modifierQuantite() / supprimer()
  → Panier.ajouterProduit() / modifierQuantite() / supprimerProduit()
```

## Key Design Decisions

1. **Console UI** — Text-based menus with box-drawing characters. No web frontend.
2. **No ORM** — Raw JDBC with PreparedStatements. Manual object mapping in DAOs.
3. **No Connection Pooling** — New connection per query. No HikariCP or similar.
4. **Password in Plain Text** — Stored and compared as-is in SQL. No hashing (commented `// En production: hasher le mot de passe`).
5. **Manual Dependency Wiring** — Services create DAO instances directly via `new`. No DI container.
6. **Empty servlet package** — `src/servlet/` exists but contains no files. Possibly intended for future web expansion.

## Transaction Handling

Only `CommandeDAO.creerCommande()` uses transactions (`conn.setAutoCommit(false)`, `conn.commit()`, `conn.rollback()` in catch). All other DAO operations execute as individual auto-commit statements.

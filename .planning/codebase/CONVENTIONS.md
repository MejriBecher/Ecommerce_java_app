# CONVENTIONS.md — Coding Conventions & Patterns

Last updated: 2026-05-13

## Language & Syntax

- **Java version**: 23 (uses modern features like `switch` expressions with `->` arrows)
- **No records, no sealed classes, no pattern matching** beyond simple `instanceof` — models use traditional classes

## Naming Conventions

| Element | Convention | Examples |
|---------|-----------|----------|
| Classes | PascalCase | `ProduitDAO`, `AuthService`, `MainMenu` |
| Methods | camelCase | `getConnection()`, `ajouterProduit()`, `emailExiste()` |
| Variables | camelCase | `utilisateurConnecte`, `motDePasse`, `panierDAO` |
| Constants | UPPER_SNAKE_CASE | `URL`, `USER`, `PASSWORD` (in `DatabaseConnection`) |
| Packages | lowercase | `model`, `dao`, `service`, `ui` |
| Enums | UPPER_SNAKE_CASE | `EN_ATTENTE`, `VALIDEE`, `LIVREE` |

## Language

**French** — All identifiers, comments, SQL column names, console output, and seed data are in French.

| English | French |
|---------|--------|
| User | Utilisateur |
| Product | Produit |
| Order | Commande |
| Shopping cart | Panier |
| Order line | LigneCommande |
| Password | motDePasse |
| Add | ajouter |
| Modify | modifier |
| Delete | supprimer |

## Code Style

### Curly Braces
- Opening brace on same line (K&R style)
- Always used even for single-line blocks

### Indentation
- 4-space indentation (IntelliJ default)
- No tabs

### Visibility
- Classes are `public`
- Instance fields are `private`
- Methods are `public` unless internal (`private` for helper methods like `getLignesCommande()`)

### Constructor Overloading
Models provide 2-3 constructors:
```java
// No-arg
public Produit() {}

// Business constructor (without id)
public Produit(String nom, String description, double prix, int stock) {}

// Full constructor
public Produit(int id, String nom, String description, double prix, int stock) {}
```

### Getter/Setter Pattern
- Standard JavaBean convention for all model fields:
```java
public int getId() { return id; }
public void setId(int id) { this.id = id; }
```

## Error Handling

### Pattern: Print + Return
Methods return a boolean/object and print error messages directly via `System.err.println()`:
```java
catch (SQLException e) {
    System.err.println("Erreur inscription: " + e.getMessage());
}
return false;
```

### No Checked Exception Propagation
All `SQLException` instances are caught at the DAO level and swallowed (converted to return values). No custom exceptions are defined.

### User-Facing Errors
Services print user-facing messages via `System.out.println()`:
```java
if (email == null || email.isBlank()) {
    System.out.println("❌ Le nom ne peut pas être vide.");
    return false;
}
```

This mixes UI concerns into the service layer.

## Key Patterns

### 1. Try-With-Resources
All DAO methods use try-with-resources for Connection, PreparedStatement, and ResultSet:
```java
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql)) {
    // ...
} catch (SQLException e) {
    System.err.println(...);
}
```

### 2. Manual Object Mapping
ResultSet rows are manually mapped to model objects:
```java
return new Utilisateur(
    rs.getInt("id"),
    rs.getString("nom"),
    rs.getString("email"),
    rs.getString("mot_de_passe"),
    rs.getString("role")
);
```

### 3. Manual Dependency Injection
Dependencies are field-initialized with `new`:
```java
private final ProduitDAO produitDAO = new ProduitDAO();
```

### 4. Batch Operations
Used in `CommandeDAO.creerCommande()` for inserting order lines:
```java
psLigne.addBatch();
// ...
psLigne.executeBatch();
```

### 5. Transaction Management
Manual `setAutoCommit(false)`, `commit()`, `rollback()` — only in `CommandeDAO.creerCommande()`.

### 6. Console Menu Loop
All menus use a `while (choix != 0)` pattern with `switch`:
```java
int choix = -1;
while (choix != 0) {
    // display menu
    // read input
    try { choix = Integer.parseInt(sc.nextLine().trim()); }
    catch (NumberFormatException e) { choix = -1; }
    switch (choix) {
        case 1 -> /* action */;
        // ...
        case 0 -> /* logout/exit */;
        default -> System.out.println("❌ Choix invalide.");
    }
}
```

### 7. UPSERT Pattern
`PanierDAO.ajouterOuMettreAJour()` uses MySQL `ON DUPLICATE KEY UPDATE`.

## Common Imports

```java
import database.DatabaseConnection;
import model.*;
import java.sql.*;
import java.util.*;
```

## toString Format
All models override `toString()` with a descriptive format:
```java
public String toString() {
    return String.format("Produit{id=%d, nom='%s', prix=%.2f DT, stock=%d}", id, nom, prix, stock);
}
```

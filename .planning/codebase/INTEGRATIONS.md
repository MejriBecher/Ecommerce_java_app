# INTEGRATIONS.md — External Integrations

Last updated: 2026-05-13

## Database

| Attribute | Value |
|-----------|-------|
| **Database** | MySQL 8+ |
| **Database Name** | `ecommerce_db` |
| **Charset** | `utf8mb4` / `utf8mb4_unicode_ci` |
| **Host** | `localhost:3306` |
| **Connection URL** | `jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true` |
| **Driver** | `com.mysql.cj.jdbc.Driver` (MySQL Connector/J 9.6.0) |
| **Credentials** | Hardcoded — `root` / empty password (see `src/database/DatabaseConnection.java:13-14`) |
| **Connection Mgmt** | Manual — `DriverManager.getConnection()` in each DAO method |

### Tables

| Table | Purpose | Key Columns |
|-------|---------|-------------|
| `utilisateurs` | Users/accounts | `id`, `nom`, `email`, `mot_de_passe`, `role` (ENUM: ADMIN/CLIENT) |
| `produits` | Product catalog | `id`, `nom`, `description`, `prix`, `stock` |
| `commandes` | Orders | `id`, `user_id`, `date`, `total`, `statut` (ENUM: EN_ATTENTE/VALIDEE/LIVREE) |
| `ligne_commande` | Order line items | `id`, `commande_id`, `produit_id`, `quantite`, `sous_total` |
| `panier` | Persistent shopping cart | `id`, `user_id`, `produit_id`, `quantite` (unique on user+product) |

### Schema File

`resources/ecommerce_db.sql` — Contains CREATE statements for all 5 tables, plus test data:
- 1 admin user (`admin@ecommerce.com` / `admin123`)
- 2 client users
- 5 sample products

## External APIs

**None.** The application has no REST API, no third-party service integrations, and no webhook consumers or producers. All functionality is local database operations.

## Authentication

| Aspect | Implementation |
|--------|----------------|
| **Method** | Plain-text password comparison in SQL (`WHERE email = ? AND mot_de_passe = ?`) |
| **Session** | In-memory `Utilisateur` object held by `AuthService` |
| **Roles** | Two roles: `ADMIN` and `CLIENT` (enum stored as string) |
| **Registration** | Basic server-side validation (non-empty name, `@` in email, password >= 4 chars) |

## Libraries

**MySQL Connector/J 9.6.0** is the only external dependency. Referenced as a local JAR file at `$USER_HOME$/Downloads/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar`. Not bundled in the repository — must be manually configured per developer machine.

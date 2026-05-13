# STACK.md — Technology Stack

Last updated: 2026-05-13

## Language & Runtime

| Attribute | Value |
|-----------|-------|
| **Language** | Java (SE) |
| **JDK Version** | OpenJDK 23 (`JDK_23` per `.idea/misc.xml`) |
| **Build Tool** | None (IntelliJ IDEA module, no Maven/Gradle) |
| **IDE** | IntelliJ IDEA (`.iml` module at `ECommerceJava.iml`) |
| **Output Directory** | `out/production/ECommerceJava` |

## Framework & Libraries

| Library | Version | Type | Scope |
|---------|---------|------|-------|
| MySQL Connector/J | 9.6.0 | JDBC Driver | Database connectivity |
| JDK Standard Library | 23 | Built-in | All packages |

**No web framework, no ORM, no dependency injection framework.** The project is a standalone Java SE console application.

## Dependencies

All dependencies are managed via IntelliJ library declarations (`.idea/libraries/mysql_connector_j_9_6_0.xml`):

- **mysql-connector-j-9.6.0.jar** — Located at `$USER_HOME$/Downloads/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar` (local filesystem reference, not in repo)

No `pom.xml`, `build.gradle`, or `package.json` exists. Adding the project to a new machine requires manually configuring the MySQL connector JAR in IntelliJ.

## Java Packages Used

| Package | Usage |
|---------|-------|
| `java.sql` | JDBC connection, PreparedStatement, ResultSet, SQLException |
| `java.time.LocalDate` | Order date handling |
| `java.util.ArrayList`, `List`, `Iterator` | Collections |
| `java.util.Scanner` | Console input |

## Project Structure Overview

```
ECommerceJava/
├── src/
│   ├── Main.java                     (entry point)
│   ├── database/                     (1 file)
│   ├── model/                        (5 files)
│   ├── dao/                          (4 files)
│   ├── service/                      (3 files)
│   ├── servlet/                      (empty — no files)
│   └── ui/                           (3 files)
├── resources/
│   └── ecommerce_db.sql              (DB schema + test data)
├── .idea/                            (IntelliJ project config)
├── out/                              (compiled classes)
└── ECommerceJava.iml                 (IntelliJ module)
```

## File Summary

17 Java source files across 6 packages, 1 SQL file.

| Package | File | Lines |
|---------|------|-------|
| (root) | `Main.java` | 7 |
| `database` | `DatabaseConnection.java` | 26 |
| `model` | `Utilisateur.java` | 47 |
| `model` | `Produit.java` | 47 |
| `model` | `Commande.java` | 55 |
| `model` | `LigneCommande.java` | 42 |
| `model` | `Panier.java` | 94 |
| `dao` | `UserDAO.java` | 89 |
| `dao` | `ProduitDAO.java` | 108 |
| `dao` | `CommandeDAO.java` | 135 |
| `dao` | `PanierDAO.java` | 104 |
| `service` | `AuthService.java` | 53 |
| `service` | `ProduitService.java` | 58 |
| `service` | `CommandeService.java` | 72 |
| `ui` | `MainMenu.java` | 63 |
| `ui` | `AdminMenu.java` | 82 |
| `ui` | `ClientMenu.java` | 148 |

# TESTING.md — Testing Strategy & Coverage

Last updated: 2026-05-13

## Current State

**No tests exist.** The project has:
- Zero test source files
- No test directory (`src/test/` does not exist)
- No test dependencies (no JUnit, no Mockito, no TestNG)
- No CI configuration
- No test runner configuration in `.idea/`

## Testability Assessment

### What Is Testable (with effort)

| Layer | Testability | Reason |
|-------|-------------|--------|
| **Model** | High | POJOs with no external dependencies. Pure data + calculation (`Commande.calculerTotal()`, `Panier.calculerTotal()`, `LigneCommande.getSousTotal()`) |
| **Service** | Medium | Business logic mixed with `System.out.println()`. Requires refactoring to separate output from logic. `AuthService` has session state that can be tested |
| **DAO** | Low | Tightly coupled to `DatabaseConnection.getConnection()`. No interface/abstraction → hard to mock. Requires real MySQL or embedded DB |
| **UI** | Very Low | Console menus print directly via `System.out`. No abstraction layer for I/O |

### Testable Methods (candidates)

**Model layer** — No dependencies, easy to unit test:
- `Commande.calculerTotal()` — sum of line subtotals
- `Panier.calculerTotal()` — sum of items
- `Panier.ajouterProduit()` — adds or increments
- `Panier.supprimerProduit()` — removes by product ID
- `LigneCommande.setQuantite()` — recalculates `sousTotal`
- `LigneCommande` constructor — computes `sousTotal = prix * quantite`
- `Produit` getters/setters

**Service layer** — Some testable methods:
- `AuthService.inscrire()` — validation rules (blank name, invalid email, short password, duplicate email)
- `ProduitService.ajouterProduit()` — price <= 0, stock < 0
- `CommandeService.passerCommande()` — empty cart, insufficient stock

## Recommended Test Framework

| Tool | Purpose |
|------|---------|
| **JUnit 5** | Unit test framework |
| **Mockito** | Mocking DAO layer for service tests |
| **AssertJ** | Fluent assertions |

If following Maven/Gradle migration, add to dependencies:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.12.0</version>
    <scope>test</scope>
</dependency>
```

## Testing Strategy Recommendations

### Unit Tests
- Model POJOs: 100% coverage
- Service validation logic: mock DAOs, test each validation path
- Panier in-memory logic: add, remove, update, clear, total

### Integration Tests
- DAO layer: use an embedded H2 or Testcontainers with MySQL
- DatabaseConnection: test connection string, driver loading

### Architecture Improvements for Testability
1. Extract `System.out` calls to a `ConsoleUI` interface to allow test capture
2. Add repository interfaces (e.g., `UserRepository`) for DAO mocking
3. Separate validation from output in service layer
4. Add a test source root `src/test/java/`

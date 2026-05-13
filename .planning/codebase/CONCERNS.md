# CONCERNS.md — Technical Debt, Risks & Areas of Concern

Last updated: 2026-05-13

## Critical Issues

### 1. Plain-Text Passwords (Security)
**Severity: HIGH** | Location: `src/dao/UserDAO.java:19`, `src/dao/UserDAO.java:34-40`

Passwords are stored and compared as plain text in SQL queries:
```java
// En production: hasher le mot de passe
ps.setString(3, user.getMotDePasse());
```
```java
String sql = "SELECT * FROM utilisateurs WHERE email = ? AND mot_de_passe = ?";
```

The codebase has a comment acknowledging this should be hashed. No hashing library (e.g., BCrypt) is included.

### 2. Hardcoded Database Credentials (Security)
**Severity: HIGH** | Location: `src/database/DatabaseConnection.java:13-14`

```java
private static final String USER = "root";
private static final String PASSWORD = "";
```

Credentials are compiled into the JAR. Anyone with access to the bytecode or source can read them. The empty password is a production risk if this were deployed.

### 3. No Input Validation on Integer/Double Inputs (Stability)
**Severity: MEDIUM** | Location: `src/ui/AdminMenu.java:59-60`, `src/ui/AdminMenu.java:69-70`

```java
double prix = Double.parseDouble(sc.nextLine());   // NumberFormatException if not a number
int stock = Integer.parseInt(sc.nextLine());         // NumberFormatException if not a number
```

No try-catch around price/stock input in AdminMenu. Invalid input crashes the menu and exits the loop. ClientMenu does handle these with try-catch (better pattern).

## Moderate Issues

### 4. No Test Coverage
**Severity: MEDIUM** | Entire project

Zero tests. No JUnit, no test directory, no CI. Any change risks regressions. See `TESTING.md` for assessment.

### 5. Service Layer Leaks UI Concerns
**Severity: MEDIUM** | All service files

Services print messages directly via `System.out.println()` instead of returning status objects. This mixes business logic with presentation, making UI changes or unit testing harder.

Example from `src/service/AuthService.java:25`:
```java
System.out.println("❌ Cet email est déjà utilisé.");
```

### 6. Connection Management — No Pooling
**Severity: LOW-MEDIUM** | All DAO classes

Every DAO method opens a new connection via `DatabaseConnection.getConnection()`. No connection pooling, no retry logic. Under load this will exhaust MySQL connections. For a single-user console app this is acceptable, but would fail under concurrent access.

### 7. No Logging Framework
**Severity: LOW-MEDIUM** | All files

Errors are written to stderr with `System.err.println()`. No SLF4J, Log4j, or java.util.logging. No log levels, no log files, no structured logging.

## Minor Issues

### 8. Empty `servlet/` Package
**Severity: LOW** | `src/servlet/`

The `src/servlet/` directory exists but has no files. This is dead weight in the source tree. Either remove it or populate it.

### 9. No Build Tool
**Severity: LOW** | Project root

No Maven, Gradle, or Ant build file. Dependency management is manual (IntelliJ library pointing to a JAR in Downloads). New developers must manually configure the MySQL connector JAR path.

### 10. SQL Injection Risk — Minimal (Due to PreparedStatements)
**Severity: LOW** | All DAO files

The codebase correctly uses `PreparedStatement` for all queries. However, two methods use `Statement` (not `PreparedStatement`) for simple SELECTs:
- `UserDAO.getTousLesUtilisateurs()` (`src/dao/UserDAO.java:75`)
- `ProduitDAO.getTousLesProduits()` (`src/dao/ProduitDAO.java:84`)
- `CommandeDAO.getToutesLesCommandes()` (`src/dao/CommandeDAO.java:95`)

These are safe because no user input is interpolated, but the pattern is inconsistent.

### 11. No JavaDoc or Documentation Comments
**Severity: LOW** | All source files

No `/** ... */` documentation comments on any class or method. Only inline comments in French. This makes API understanding harder for new contributors.

### 12. Password Validation Inconsistent
**Severity: LOW** | `src/service/AuthService.java`

Registration requires password >= 4 chars, but login has no validation at all — empty password is accepted.

## Risk Summary

| Risk | Impact | Likelihood | Priority |
|------|--------|------------|----------|
| Plain-text passwords | Credential theft | High (if deployed) | **CRITICAL** |
| Hardcoded creds | Database compromise | High (if deployed) | **CRITICAL** |
| No tests | Regression bugs | Medium | HIGH |
| No input validation (admin) | Crash on bad input | Medium | MEDIUM |
| Service-UI coupling | Hard to test/refactor | Low | MEDIUM |
| No connection pooling | Performance under load | Low (solo app) | LOW |
| No build tool | Onboarding friction | Medium | LOW |

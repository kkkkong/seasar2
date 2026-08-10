# Seasar2 — S2Container

**A lightweight, high-performance Dependency Injection (DI) container with powerful Aspect-Oriented Programming (AOP) capabilities for Java applications.**

[![JDK](https://img.shields.io/badge/JDK-8%20%7C%2011%20%7C%2017-blue?logo=openjdk)](MIGRATION_GUIDE.md#44-jdk-version-compatibility-matrix)
[![Maven](https://img.shields.io/badge/Maven-3.0+-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green)](seasar2/LICENSE.txt)
[![Status](https://img.shields.io/badge/Status-Modernized%20Fork-brightgreen)](#about-this-fork)

> **This is a modernized fork** of the original [seasarorg/seasar2](https://github.com/seasarorg/seasar2) framework, upgraded to support JDK 8, 11, and 17 with critical security patches for XXE and OGNL injection vulnerabilities.

**Original Project:** [s2container.seasar.org](http://s2container.seasar.org/)

---

## About This Fork

The original Seasar2 framework — first released in 2004 by [The Seasar Foundation](http://www.seasarfoundation.org/) — was a pioneering DI/AOP container for Java. This fork modernizes the codebase to run on contemporary JDK versions while maintaining full backward compatibility with existing Seasar2 applications.

### Modernization Highlights

| Area | Status | Details |
|---|---|---|
| **JDK 8 / 11 / 17** | ✅ Supported | Build and runtime compatibility |
| **XXE Protection** | ✅ Patched | XML parsers hardened against external entity attacks |
| **OGNL Sandbox** | ✅ Patched | Expression evaluator locked down against RCE |
| **Test Suite** | ✅ Passing | No code regressions on JDK 17 |
| **`s2jdbc-gen`** | ✅ Working | Compiles without `tools.jar` on JDK 9+ |

---

## Features

- **Lightweight DI Container** — Automatic dependency injection with minimal configuration
- **Powerful AOP Framework** — Aspect-oriented programming with dynamic proxy support
- **S2JDBC** — High-performance database access with automatic SQL binding
- **S2DBCP** — Database connection pooling
- **S2Tx** — Declarative transaction management (JTA/JDBC)
- **S2Unit** — Integration testing framework
- **S2Dxo** — Data transfer object mapping utilities
- **Convention over Configuration** — Automatic component discovery and wiring
- **JPA Support** — Java Persistence API integration
- **Multi-database Support** — Derby, H2, HSQLDB, MySQL, PostgreSQL, Oracle, DB2, SQL Server

---

## Security Hardening

### XXE (XML External Entity) Protection

All XML parsers used for `.dicon` file loading have been hardened against XXE injection:

- [`SAXParserFactoryUtil.newInstance()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/SAXParserFactoryUtil.java:47): DOCTYPE, external entities, and XInclude explicitly disabled
- [`DocumentBuilderFactoryUtil.newInstance()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/DocumentBuilderFactoryUtil.java:43): Same hardening applied to DOM parser factory
- [`XmlS2ContainerBuilder.createSaxHandlerParser()`](seasar2/s2-framework/src/main/java/org/seasar/framework/container/factory/XmlS2ContainerBuilder.java:185): DTD validation re-enabled for `.dicon` schema validation while keeping external entities blocked

### OGNL Expression Sandbox

A new security sandbox prevents Remote Code Execution (RCE) through OGNL expressions in `.dicon`, S2JDBC SQL templates, and JSP views:

| Blocked Operation | Risk Mitigated |
|---|---|
| `Runtime.exec()` / `ProcessBuilder` | System command execution |
| `System.exit()` | JVM termination / DoS |
| `Class.forName()` | Arbitrary class loading |
| `ClassLoader` access | Classloader manipulation |
| Reflective `invoke()` / `setAccessible()` | Access-control bypass |

> ⚠️ **Legacy applications** that use these operations in OGNL expressions will need migration. See the [Migration Guide](MIGRATION_GUIDE.md#2-ognl-security-sandbox) for safe alternatives.

---

## Getting Started

### Prerequisites

- Java 8 or later (JDK 11+ recommended; JDK 17 is the primary target)
- Maven 3.0+

### Build from Source

**Build order matters** due to inter-module dependencies:

```bash
# Step 1: Build core framework (s2-framework + s2-extension)
cd seasar2
mvn clean install

# Step 2: Build annotation-based extensions (s2-tiger)
cd ../s2-tiger
mvn clean install

# Step 3: Build code generation tool (s2jdbc-gen)
cd ../s2jdbc-gen
mvn clean install
```

> **Important:** Run `mvn clean install` from each **module directory** — NOT the repository root. The root [`pom.xml`](pom.xml) is an aggregator-only POM.

### Running Tests

```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Run with specific database profile
mvn test -Ppostgre      # PostgreSQL
mvn test -Pmysql        # MySQL
mvn test -Ph2           # H2 (embedded)
mvn test -Phsqldb       # HSQLDB (default)

# Skip tests during build
mvn clean install -DskipTests
```

### Maven Dependency

Add to your `pom.xml`:

```xml
<dependency>
  <groupId>org.seasar.container</groupId>
  <artifactId>s2-framework</artifactId>
  <version>2.4.49</version>
</dependency>

<dependency>
  <groupId>org.seasar.container</groupId>
  <artifactId>s2-extension</artifactId>
  <version>2.4.49</version>
</dependency>

<dependency>
  <groupId>org.seasar.container</groupId>
  <artifactId>s2-tiger</artifactId>
  <version>2.4.49</version>
</dependency>
```

---

## Project Structure

```
├── seasar2/                              # Root Maven project (aggregator)
│   ├── s2-framework/                     # Core DI and AOP engine
│   │   ├── aop/                          # AOP implementation (interceptors, invocations)
│   │   ├── container/                    # S2Container core (dependency resolution)
│   │   ├── beans/                        # Bean metadata and property inspection
│   │   ├── util/                         # Common utilities
│   │   └── xml/                          # XML configuration parsing
│   ├── s2-extension/                     # Database and extension libraries
│   │   ├── jdbc/                         # S2JDBC — SQL execution and binding
│   │   ├── dao/                          # DAO pattern utilities
│   │   ├── dbcp/                         # Connection pooling
│   │   ├── tx/                           # Transaction management
│   │   └── dxo/                          # Data transfer objects
│   ├── s2-dist/                          # Distribution packaging
│   └── pom.xml                           # Parent POM
├── s2-tiger/                             # Annotations & Java 5+ features
│   ├── @Tx, @Dxo, @Query annotations
│   ├── EJB 3.0 / JPA support
│   └── JUnit 4 integration
├── s2jdbc-gen/                           # Entity code generation tool
├── s2jdbc-gen-it/                        # Integration tests for s2jdbc-gen
├── s2jdbc-tutorial/                      # Tutorial and examples
└── pom.xml                               # Top-level module aggregator
```

### How It Fits Together

1. **S2 Framework** (Core) — `S2Container` manages component lifecycle and DI; AOP provides cross-cutting concerns via dynamic proxies; `BeanDesc` enables property inspection.

2. **S2 Extension** (Database & Utilities) — `S2JDBC` for SQL execution with automatic binding; `DAO` for CRUD operations; `DBCP` for connection pooling; `Tx` for declarative transactions.

3. **S2 Tiger** (Annotations) — `@Tx`, `@Dxo`, `@Query` annotations; EJB 3.0 / JPA compatibility; FreeMarker-based code generation.

---

## Documentation

| Document | Description |
|---|---|
| [**Changelog**](CHANGELOG.md) | Detailed list of all changes in this modernized release |
| [**Migration Guide**](MIGRATION_GUIDE.md) | Step-by-step upgrade instructions (JVM args, OGNL sandbox, CLDR note, build) |
| [**Technical Debt & Future Work**](TECHNICAL_DEBT_ISSUES.md) | Known limitations and planned improvements |
| [**Development Guide**](DEVELOPMENT.md) | Build and contribution instructions |
| [**Contributing**](CONTRIBUTING.md) | How to contribute to this project |

---

## Upgrading from Legacy Seasar2

If you are migrating from the original `seasarorg/seasar2`, see the **[Migration Guide](MIGRATION_GUIDE.md)** for:

- Required `--add-opens` JVM flags for JDK 9+
- OGNL expression migration patterns
- Date format CLDR provider notes
- Maven build configuration updates

---

## Configuration Example

### `.dicon` File (XML-based IoC Config)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE components PUBLIC
  "-//SEASAR//DTD S2Container 2.4//EN"
  "http://www.seasar.org/dtd/components24.dtd">
<components>
  <component name="dataSource" class="org.seasar.extension.dbcp.impl.DataSourceImpl">
    <property name="driverClassName">org.hsqldb.jdbcDriver</property>
    <property name="url">jdbc:hsqldb:mem:testdb</property>
    <property name="user">sa</property>
    <property name="password"></property>
  </component>

  <component name="userDao" class="com.example.dao.UserDao" />

  <component name="userService" class="com.example.service.UserService">
    <property name="userDao">userDao</property>
  </component>
</components>
```

### Test Case (S2Unit)

```java
public class UserDaoTest extends S2FrameworkTestCase {

    private UserDao userDao;

    protected void setUp() throws Exception {
        super.setUp();
        include(getClass().getName().replace('.', '/') + ".dicon");
    }

    public void testFindById() throws Exception {
        User user = userDao.findById(1);
        assertNotNull(user);
        assertEquals("John", user.getName());
    }
}
```

---

## Database Support

| Database | Status |
|---|---|
| HSQLDB | ✅ (default for tests) |
| H2 | ✅ |
| Apache Derby | ✅ |
| MySQL | ✅ |
| PostgreSQL | ✅ |
| Oracle | ✅ |
| IBM DB2 | ✅ |
| Microsoft SQL Server | ✅ |

---

## License

Apache License 2.0 — See [`LICENSE.txt`](seasar2/LICENSE.txt)

---

## Attribution

This is a modernized fork of [seasarorg/seasar2](https://github.com/seasarorg/seasar2), originally created by:

- **HIGA Yasuo** (Lead Architect)
- SATO Taichi
- KOBAYASHI Koichi
- YOKOTA Takehiko
- HONMA Hirotaka
- KOMORI Yusuke
- And many contributors to [The Seasar Foundation](http://www.seasarfoundation.org/)

**Original Project Inception:** 2004

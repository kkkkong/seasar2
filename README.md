# Seasar2 - S2Container

A lightweight, high-performance dependency injection (DI) container with powerful aspect-oriented programming (AOP) capabilities for Java applications. S2Container provides seamless integration with database access, transaction management, and comprehensive testing utilities.

**Website:** [s2container.seasar.org](http://s2container.seasar.org/)

---

## Features

- **Lightweight DI Container** - Automatic dependency injection with minimal configuration
- **Powerful AOP Framework** - Aspect-oriented programming with dynamic proxy support
- **S2JDBC** - High-performance database access with automatic SQL binding
- **S2DBCP** - Database connection pooling
- **S2Tx** - Declarative transaction management (JTA/JDBC)
- **S2Unit** - Integration testing framework
- **S2Dxo** - Data transfer object mapping utilities
- **Convention over Configuration** - Automatic component discovery and wiring
- **JPA Support** - Java Persistence API integration
- **Multi-database Support** - Works with Derby, H2, HSQLDB, MySQL, PostgreSQL, Oracle, and more

---

## Stack

- **Language:** Java 1.4+ (with backward compatibility)
- **Build System:** Maven 3
- **Framework:** Seasar2 (v2.4.49-SNAPSHOT)
- **Notable Libraries:**
  - Javassist 3.4 - Bytecode manipulation
  - OGNL 2.6.9 - Object-Graph Navigation Language
  - Apache Commons Logging 1.1 - Logging abstraction
  - AOP Alliance 1.0 - AOP standards
  - JUnit 3.8.2 / 4.4 - Testing framework

---

## Project Structure

```
├── seasar2/                         # Root Maven project
│   ├── s2-framework/               # Core DI and AOP engine
│   │   ├── aop/                    # AOP implementation (interceptors, invocations)
│   │   ├── container/              # S2Container core (dependency resolution)
│   │   ├── beans/                  # Bean metadata and property inspection
│   │   ├── unit/                   # S2Unit testing utilities
│   │   ├── util/                   # Common utilities
│   │   └── xml/                    # XML configuration parsing
│   ├── s2-extension/               # Database and extension libraries
│   │   ├── jdbc/                   # S2JDBC - SQL execution and binding
│   │   ├── dao/                    # DAO pattern utilities
│   │   ├── dbcp/                   # Connection pooling
│   │   ├── dataset/                # Test data fixtures
│   │   ├── dxo/                    # Data transfer objects
│   │   ├── tx/                     # Transaction management
│   │   ├── jta/                    # JTA transaction handling
│   │   └── unit/                   # Integration testing
│   ├── s2-dist/                    # Distribution packaging
│   ├── s2-framework/               # Framework pom
│   └── pom.xml                     # Parent pom (v2.4.49-SNAPSHOT)
├── s2-tiger/                       # Annotations and advanced features
│   ├── org.seasar.extension.jdbc*  # S2JDBC with annotations
│   ├── org.seasar.extension.dxo*   # S2Dxo
│   └── org.seasar.extension.tx*    # Advanced transaction support
├── s2jdbc-gen/                     # Code generation tool
├── s2jdbc-gen-it/                  # Integration tests for generator
├── s2jdbc-tutorial/                # Tutorial and examples
└── pom.xml                         # Top-level module aggregator
```

### How It Fits Together

The project follows a layered architecture:

1. **S2 Framework** (Core)
   - `S2Container` manages component lifecycle and dependency injection
   - `AOP` framework provides cross-cutting concerns via dynamic proxies
   - `BeanDesc` and reflection utilities enable property inspection
   - `S2Unit` integrates testing with the container

2. **S2 Extension** (Database & Utilities)
   - `S2JDBC` executes SQL with automatic parameter binding
   - `DAO` utilities simplify CRUD operations
   - `DBCP` manages database connections
   - `Tx` handles declarative transactions (method-level)
   - `Dxo` maps data between objects

3. **S2 Tiger** (Annotations & Java 5+ Features)
   - Provides `@Tx`, `@Dxo`, `@Query` annotations
   - EJB 3.0 and JPA annotations support
   - Code generation with FreeMarker templates

---

## Getting Started

### Prerequisites

- Java 1.4+ (JDK 6+ recommended for best compatibility)
- Maven 3.0+

### Build from Source

```bash
# Clone the repository
git clone https://github.com/kkkkong/seasar2.git
cd seasar2

# Build core framework
cd seasar2
mvn clean install

# Build extensions
cd ../s2-tiger
mvn clean install

# Build code generation tools
cd ../s2jdbc-gen
mvn clean install

# Generate from integration tests (optional)
cd ../s2jdbc-gen-it
mvn clean test
```

### Running Tests

```bash
# Run all tests
mvn test

# Run with specific database (default: hsqldb)
mvn test -Pdatabase

# Supported profiles: standard, hsqldb, h2, postgre, mysql, oracle, db2, mssql2005
mvn test -Ppostgre
```

### Maven Dependency

Add to your `pom.xml`:

```xml
<dependency>
  <groupId>org.seasar.container</groupId>
  <artifactId>s2-framework</artifactId>
  <version>2.4.49</version>
</dependency>

<!-- For database access and extensions -->
<dependency>
  <groupId>org.seasar.container</groupId>
  <artifactId>s2-extension</artifactId>
  <version>2.4.49</version>
</dependency>

<!-- For annotations and advanced features -->
<dependency>
  <groupId>org.seasar.container</groupId>
  <artifactId>s2-tiger</artifactId>
  <version>2.4.49</version>
</dependency>
```

---

## Core Components

### S2 Framework

The foundation of Seasar2, providing:

- **Container** - IoC/DI dependency resolution engine
- **AOP** - Aspect-oriented programming with method interceptors
- **BeanDesc** - Introspection and metadata caching
- **Convention** - Auto-detection and naming conventions
- **Unit** - TestCase integration for component testing

Key classes:
- `S2Container` - Main container interface
- `S2ContainerImpl` - Default implementation
- `Interceptor` - AOP interceptor interface
- `BeanDesc` - Bean metadata descriptor

### S2 Extension

Database and utility extensions:

- **S2JDBC** - Object-relational mapping and SQL execution
- **DAO** - Data access object patterns
- **DBCP** - Connection pooling
- **Tx** - Transaction management
- **Dxo** - Data transfer objects
- **Unit** - Integration testing utilities

Key classes:
- `S2Jdbc` - SQL execution
- `StatementFactory` - SQL statement generation
- `ConnectionPool` - Connection management
- `TxInterceptor` - Transaction aspect

### S2 Tiger

Java 5+ features and annotations:

- Annotation support (`@Tx`, `@Dxo`, `@Query`)
- EJB 3.0 compatibility
- JPA integration
- Advanced code generation

---

## Configuration

### XML Configuration Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.seasar.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.seasar.org/schema/beans
       http://www.seasar.org/schema/beans.xsd">

  <!-- Define components -->
  <component name="datasource" class="org.seasar.extension.datasource.DataSourceImpl">
    <property name="driverClassName">org.hsqldb.jdbcDriver</property>
    <property name="url">jdbc:hsqldb:mem:testdb</property>
    <property name="user">sa</property>
    <property name="password"></property>
  </component>

  <component name="userDao" class="example.dao.UserDao"/>
  
  <!-- Service with dependency injection -->
  <component name="userService" class="example.service.UserService">
    <property name="userDao">userDao</property>
  </component>

</beans>
```

### Convention-based Configuration

Seasar2 automatically detects and wires components:

```java
// Automatically detected as component 'helloService'
@Component
public class HelloService {
  private MessageRepository messageRepository; // Auto-wired
}
```

---

## Database Support

### Supported Databases

- Apache Derby
- H2
- HSQLDB (default for tests)
- MySQL
- PostgreSQL
- Oracle
- IBM DB2
- Microsoft SQL Server 2005+

### Configure Database Connection

```xml
<component name="connectionPool" class="org.seasar.extension.dbcp.impl.ConnectionPoolImpl">
  <property name="driver">org.postgresql.Driver</property>
  <property name="url">jdbc:postgresql://localhost:5432/mydb</property>
  <property name="user">postgres</property>
  <property name="password">password</property>
  <property name="maxPoolSize">10</property>
</component>
```

---

## Transaction Management

### Declarative Transactions

```java
@Component
public class UserService {
  
  @Tx
  public void saveUser(User user) {
    // Transaction automatically managed
  }

  @Tx(rollback = {Exception.class})
  public void complexOperation() {
    // Custom rollback rules
  }
}
```

### Supported Transaction Types

- **JDBC Transactions** - Local database transactions
- **JTA Transactions** - Distributed/container-managed transactions
- **Read-only Transactions** - Optimized for queries

---

## Testing

### S2Unit Integration Testing

```java
public class UserDaoTest extends S2TestCase {
  
  private UserDao userDao;
  
  protected void setUp() throws Exception {
    super.setUp();
    // Container automatically injects userDao
  }
  
  public void testFindById() throws Exception {
    User user = userDao.findById(1);
    assertNotNull(user);
    assertEquals("John", user.getName());
  }
}
```

### Test Data with S2Dataset

```java
public class UserServiceTest extends S2TestCase {
  
  public void testWithData() throws Exception {
    // Load test data from CSV/XLS
    UnitUtil.loadData("users.csv");
    
    // Test business logic
    List<User> users = userService.findActive();
    assertEquals(5, users.size());
  }
}
```

---

## Additional Resources

### Official Documentation
- [S2Container Documentation](http://s2container.seasar.org/)
- [Seasar Forum](https://www.seasar.org/mailman/listinfo/seasar-user)

### Mailing Lists
- **Seasar-user** - General user discussions
- **Seasar-dev** - Development discussions
- **Seasar-user-en** - English-language support

### License

Apache License 2.0 - See [LICENSE.txt](seasar2/LICENSE.txt)

---

## Project Information

**Version:** 2.4.49-SNAPSHOT  
**Inception Year:** 2004  
**Organization:** [The Seasar Foundation](http://www.seasarfoundation.org/)

### Key Contributors

- HIGA Yasuo (Lead Architect)
- SATO Taichi
- KOBAYASHI Koichi
- YOKOTA Takehiko
- HONMA Hirotaka
- KOMORI Yusuke
- And many others (see pom.xml for full credits)

---

## Related Projects

This is a fork of the official [seasarorg/seasar2](https://github.com/seasarorg/seasar2) repository. For the upstream project, please refer to the official GitHub organization.

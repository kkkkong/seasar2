# Migration Guide — Seasar2 Modernized Fork (JDK 8 → 17)

This guide covers the required steps when migrating your Seasar2-based application from the legacy version to the modernized release, which adds support for JDK 8, 11, and 17 alongside critical security hardening.

---

## Table of Contents

1. [JVM Arguments (Java 17+)](#1-jvm-arguments-java-17)
2. [OGNL Security Sandbox](#2-ognl-security-sandbox)
3. [Java 8 CLDR Note](#3-java-8-cldr-note)
4. [Build Instructions](#4-build-instructions)
5. [Verification Checklist](#5-verification-checklist)

---

## 1. JVM Arguments (Java 17+)

### 1.1 Required `--add-opens` Flags

On **Java 9 and later** (JDK 11, JDK 17, JDK 21+), you **MUST** add the following `--add-opens` JVM arguments:

```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.math=ALL-UNNAMED
--add-opens java.base/java.net=ALL-UNNAMED
```

### 1.2 Why These Are Needed

Seasar2 uses Java reflection to access internal JDK APIs that the Java Platform Module System (JPMS) encapsulates starting from JDK 9. The key operations affected are:

| Internal API | Used By | Purpose |
|---|---|---|
| `ClassLoader.defineClass()` | DI Container, AOP Proxy, HotdeployBehavior | Dynamic bytecode class definition for generated proxies and hot-deployed components |
| `java.lang.reflect` internals | DI Container, OGNL Expression Evaluator | Field/method access for dependency injection |
| `java.util` / `java.math` internals | Type Conversion Utilities | `BigDecimal` and collection manipulation via reflection |
| `java.net` internals | Resource Loading, Classpath Scanning | `URLClassLoader` access for component auto-discovery |

Without these flags, you will encounter `java.lang.reflect.InaccessibleObjectException` at startup:

```
Unable to make field private final ... accessible: module java.base does not "opens java.lang" to unnamed module
```

### 1.3 Configuration Examples

#### Maven Surefire / Failsafe (`pom.xml`)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>
            --add-opens java.base/java.lang=ALL-UNNAMED
            --add-opens java.base/java.util=ALL-UNNAMED
            --add-opens java.base/java.math=ALL-UNNAMED
            --add-opens java.base/java.net=ALL-UNNAMED
        </argLine>
    </configuration>
</plugin>
```

#### Gradle (`build.gradle`)

```groovy
tasks.withType(Test) {
    jvmArgs += [
        '--add-opens', 'java.base/java.lang=ALL-UNNAMED',
        '--add-opens', 'java.base/java.util=ALL-UNNAMED',
        '--add-opens', 'java.base/java.math=ALL-UNNAMED',
        '--add-opens', 'java.base/java.net=ALL-UNNAMED'
    ]
}
```

#### IntelliJ IDEA Run Configuration

1. Open **Run → Edit Configurations**
2. Select your run configuration
3. In **VM options**, paste:

```
--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED
```

#### Eclipse Run Configuration

1. Open **Run → Run Configurations**
2. Select your configuration → **Arguments** tab
3. In **VM arguments**, paste the same flags as above

#### Production `java` Command Line

```bash
java \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.util=ALL-UNNAMED \
  --add-opens java.base/java.math=ALL-UNNAMED \
  --add-opens java.base/java.net=ALL-UNNAMED \
  -cp your-app.jar:seasar2-jars/* \
  com.example.Main
```

> **Important:** If you use both `<argLine>` in Surefire and Failsafe, they are independent — configure both plugins.

---

## 2. OGNL Security Sandbox

### 2.1 What Changed

A new security sandbox, [`OgnlSecurityMemberAccess`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/OgnlSecurityMemberAccess.java:27), enforces strict access control on all OGNL expression evaluations. This prevents Remote Code Execution (RCE) through malicious OGNL expressions embedded in `.dicon` files, S2JDBC SQL templates, or JSP views.

### 2.2 Blocked Operations

OGNL expressions can **NO LONGER** access the following:

| Category | Blocked Classes / Methods | Risk Prevented |
|---|---|---|
| System command execution | `java.lang.Runtime.exec()`, `java.lang.ProcessBuilder` | Remote code execution (RCE) |
| JVM termination | `java.lang.System.exit()` | Denial of service (DoS) |
| Dynamic class loading | `java.lang.Class.forName()` | Arbitrary class loading, RCE |
| ClassLoader manipulation | `java.lang.ClassLoader` and all subclasses | Classloader injection attacks |
| Reflection bypass | `java.lang.reflect.AccessibleObject.setAccessible()` | Access-control bypass |
| Reflective invocation | `java.lang.reflect.Method.invoke()`, `Field.set()`, `Constructor.newInstance()` | Arbitrary code execution |
| Internal packages | `java.lang.invoke.*`, `sun.*`, `com.sun.*` | Unsupported API exploitation |

### 2.3 What Is Still Allowed

- Reading `public static final` constants (e.g., `@java.lang.reflect.Modifier@PUBLIC`)
- Calling getters and setters on application beans (e.g., `propertyName`, `#method.name`)
- Standard OGNL navigation: property chains, method calls on whitelisted objects
- Boolean expressions, arithmetic, string operations, and collection access

### 2.4 Migration for Affected Expressions

If your `.dicon` or S2JDBC SQL template hits the sandbox, you will see:

```
ognl.MethodFailedException: java.lang.SecurityException: Access denied ...
```

**Before (Blocked — will now fail with `SecurityException`):**

```xml
<!-- DANGEROUS: Runtime.exec() via OGNL — BLOCKED -->
<component name="service" class="com.example.Service">
    <property name="command">@java.lang.Runtime@getRuntime().exec("ls")</property>
</component>
```

**After (Safe Migration):**

```java
// Move system-level logic into Java component code
package com.example;

public class Service {
    private String commandResult;

    public Service() {
        // Use Seasar2's component binding for complex initialization
        this.commandResult = executeBusinessLogic();
    }

    private String executeBusinessLogic() {
        // System interactions implemented safely here
        return "...";
    }
}
```

**Migration Patterns:**

1. **Audit your `.dicon` files** — search for `@java.lang.Runtime`, `@java.lang.Class`, `@java.lang.System`, `@java.lang.reflect`, and `Class.forName` references
2. **Move system-level logic** out of OGNL expressions into Java component code
3. **Use component binding** — inject dependencies via Seasar2's container rather than constructing them through reflection in OGNL
4. **For S2JDBC SQL templates**: Ensure `IF` / `BEGIN` blocks in `.sql` files do not reference system classes or reflective operations

---

## 3. Java 8 CLDR Note

### 3.1 Background

Starting with JDK 9, the JVM switched its default locale provider from `JRE` to `CLDR` (Unicode Common Locale Data Repository). This affects how `java.text.DateFormat` resolves date format patterns.

### 3.2 Impact on Tests

- **5 date-format tests** in [`DateConversionUtilTest`](seasar2/s2-framework/src/test/java/org/seasar/framework/util/DateConversionUtilTest.java) and [`TimestampConversionUtilTest`](seasar2/s2-framework/src/test/java/org/seasar/framework/util/TimestampConversionUtilTest.java) may **fail on native JDK 8** but **pass on JDK 11+**.
- **This is NOT a functional regression.** Production date-formatting behavior is correct on all supported JDK versions. The JRE locale provider on JDK 8 uses slightly different pattern strings than the CLDR provider on JDK 11+, but both produce correct dates.
- **Recommendation**: Run your test suite on JDK 11+ for accurate results. JDK 8 failures in these specific tests are expected and can be ignored if your production target is JDK 11+.

### 3.3 Workaround for JDK 8 Test Execution

If you need test parity on JDK 8, force the CLDR locale provider:

```
-Djava.locale.providers=CLDR,JRE
```

This makes JDK 8 use the same CLDR locale data as JDK 11+, eliminating the test differences.

### 3.4 Forcing JRE Locale Provider on JDK 9+

Conversely, to maintain JDK 8-compatible date behavior on JDK 9+, force the legacy JRE provider:

```
-Djava.locale.providers=COMPAT,CLDR
```

---

## 4. Build Instructions

### 4.1 Build Order

The module hierarchy requires building in this order due to inter-module dependencies:

```
1. seasar2/           (s2-framework → s2-extension)
2. s2-tiger/          (depends on s2-framework + s2-extension)
3. s2jdbc-gen/        (depends on s2-framework + s2-extension + s2-tiger)
```

### 4.2 Maven Build Commands

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

> **Important:** `mvn clean install` must be run from each **module directory**, NOT the repository root. The root [`pom.xml`](pom.xml) is an aggregator-only POM (`<packaging>pom</packaging>`) and does not contain buildable source.

### 4.3 Useful Build Flags

| Command | Purpose |
|---|---|
| `mvn test -Dtest=ClassName` | Run a single test class |
| `mvn test -P<profile>` | Run tests with specific database profile |
| `mvn clean install -DskipTests` | Build without running tests |
| `mvn javadoc:javadoc` | Generate Javadoc |

### 4.4 Database Profiles

For tests requiring a database connection:

```
mvn test -Ppostgre     # PostgreSQL
mvn test -Pmysql       # MySQL
mvn test -Poracle      # Oracle
mvn test -Pdb2         # IBM DB2
mvn test -Pmssql2005   # Microsoft SQL Server
mvn test -Ph2          # H2 (embedded)
```

Default profile is HSQLDB (embedded, no external database required).

### 4.5 JDK Version Compatibility Matrix

| JDK Version | Build | Runtime | Notes |
|---|---|---|---|
| JDK 8 | ✅ | ✅ | Requires `tools.jar` for full `s2jdbc-gen` Javadoc support |
| JDK 11 | ✅ | ✅ | `--add-opens` required; `s2jdbc-gen` Javadoc extraction silently skipped |
| JDK 17 | ✅ | ✅ | `--add-opens` required; primary target version |
| JDK 21+ | ⚠️ Untested | ⚠️ Untested | May need additional `--add-opens` as more APIs are encapsulated |

### 4.6 `s2jdbc-gen` Javadoc Limitation

On **JDK 9+**, Javadoc comment extraction in `s2jdbc-gen` is **silently skipped**. The `com.sun.javadoc` API was removed from the JDK. Entity metadata generation completes successfully — only doc-comment-based metadata (e.g., `@Column` descriptions from Javadoc) is absent. This is expected behavior and not a bug.

---

## 5. Verification Checklist

After migration, confirm each item:

- [ ] **JVM `--add-opens` flags** added to all environments (development, CI, staging, production)
- [ ] **Maven Surefire/Failsafe** `<argLine>` includes all four `--add-opens` flags
- [ ] **IDE run configurations** updated (IntelliJ IDEA and/or Eclipse)
- [ ] **OGNL audit complete** — no expressions accessing `Runtime.exec()`, `ProcessBuilder`, `System.exit()`, `Class.forName()`, `ClassLoader`, or reflection APIs
- [ ] **S2JDBC SQL templates** reviewed — `IF`/`BEGIN` blocks do not reference system classes
- [ ] **Test suite passes** on target JDK version (JDK 11+ recommended)
- [ ] **Date format tests** confirmed on JDK 11+ (JDK 8 failures are expected and acceptable)
- [ ] **`s2jdbc-gen` entity generation** verified — Javadoc extraction silently skipped on JDK 9+
- [ ] **XXE protection** confirmed — no external entity references in `.dicon` files
- [ ] **Production deployment** smoke-tested with `--add-opens` flags on target JVM

---

## Related Documents

- [Changelog](CHANGELOG.md) — detailed list of all changes in this release
- [Technical Debt & Future Work](TECHNICAL_DEBT_ISSUES.md) — known limitations and planned improvements
- [Development Guide](DEVELOPMENT.md) — build and contribution instructions

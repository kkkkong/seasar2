# Migration Guide — Seasar2 Modernized Fork (JDK 8 → 21)

This guide covers the required steps when migrating your Seasar2-based application from the legacy version to the modernized release, which adds support for JDK 8, 11, 17, and 21 alongside critical security hardening and the `s2jdbc-gen` JavaParser refactor.

---

## Table of Contents

1. [JVM Arguments (Java 9+)](#1-jvm-arguments-java-9)
2. [v2.4.49 Migration Guide](#v2-4-49-migration-guide)
3. [OGNL Security Sandbox](#2-ognl-security-sandbox)
4. [s2jdbc-gen: Doclet API → JavaParser Migration](#3-s2jdbc-gen-doclet-api--javaparser-migration)
5. [Java 8 CLDR Note](#4-java-8-cldr-note)
6. [Build Instructions](#5-build-instructions)
7. [Upgrading from Original Seasar 2.4.x](#6-upgrading-from-original-seasar-24x)
8. [Verification Checklist](#7-verification-checklist)

---

## 1. JVM Arguments (Java 9+)

### 1.1 Required `--add-opens` Flags

On **Java 9 and later** (JDK 11, JDK 17, JDK 21), you **MUST** add the following `--add-opens` JVM arguments. These enable Seasar2's reflection-based DI container, AOP dynamic proxy generation (via javassist), OGNL expression evaluation, and HotdeployBehavior to operate under the Java Platform Module System (JPMS):

```bash
--add-opens=java.base/java.math=ALL-UNNAMED
--add-opens=java.base/java.net=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.text=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
```

On **JDK 11**, the four-package set above is also sufficient, but the full seven-package set is recommended for forward compatibility with JDK 17/21.

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

On **JDK 17+**, the `java17-plus-surefire` Maven profile is auto-activated and applies the full `--add-opens` set together with `-Duser.timezone=Asia/Tokyo`, `-Duser.language=ja`, `-Duser.country=JP`, and `-Djava.locale.providers=CLDR,JRE`.

---

## v2.4.49 Migration Guide

This section summarizes the key changes introduced in the **v2.4.49 modernization release** and the actions required when upgrading from earlier Seasar2 versions.

### OGNL Upgrade

| Aspect | Before | After |
|---|---|---|
| Dependency | `ognl:ognl:2.6.9-patch-20090427` (patched JAR) | `ognl:ognl:2.7.3` (official Maven Central) |
| Repository | `maven.seasar.org` legacy repository | Removed; uses Maven Central |
| Memory leak on HOT deploy | Static OGNL caches accumulated across ClassLoader generations | [`OgnlUtil.initialize()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/OgnlUtil.java:45) registers a [`DisposableUtil`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/DisposableUtil.java:38) hook that calls `OgnlRuntime.clearCache()` on shutdown |

**Action required:**

- Remove any local patched OGNL JARs from your build.
- Ensure your application server's shutdown path triggers Seasar2's `DisposableUtil` disposal (standard in container-managed deployments).

### CLDR Date Format Compatibility (JEP 252)

[`DateConversionUtil`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/DateConversionUtil.java:33) now sanitizes time components (`H:mm`) from Unicode CLDR patterns and supports unseparated date strings such as `YYYYMMDD` consistently on JDK 8, 11, 17, and 21.

**Action required:**

- No code changes are required.
- If your tests pin exact `SimpleDateFormat` pattern strings, expect pattern differences between JDK 8 (`JRE` provider) and JDK 11+ (`CLDR` provider). Use `-Djava.locale.providers=CLDR,JRE` for parity on JDK 8.

### Java 17+ Strong Encapsulation (JEP 403)

JDK 17+ enforces strong encapsulation of JDK internals. Seasar2's reflection-based DI, AOP, OGNL, and Hotdeploy require the following `--add-opens` flags at runtime:

```bash
--add-opens=java.base/java.math=ALL-UNNAMED
--add-opens=java.base/java.net=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.text=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
```

**Action required:**

- Add the flags above to production startup scripts, application-server `JAVA_OPTS`, IDE run configurations, and CI test `argLine` values.
- The flags are applied automatically during Maven Surefire execution via the `java17-plus-surefire` profile.

### Proprietary Dependency Handling (`uow_api`)

IBM WebSphere `uow_api:6` is a proprietary API that cannot be published to Maven Central. It is kept in the repository at [`seasar2/lib/uow_api-6.jar`](seasar2/lib/uow_api-6.jar) and the local repository at [`seasar2/local-repo/com/ibm/websphere/uow_api/6/`](seasar2/local-repo/com/ibm/websphere/uow_api/6/uow_api-6.pom). In [`s2-extension/pom.xml`](seasar2/s2-extension/pom.xml:131) it is declared as:

```xml
<dependency>
    <groupId>com.ibm.websphere</groupId>
    <artifactId>uow_api</artifactId>
    <version>6</version>
    <scope>provided</scope>
    <optional>true</optional>
</dependency>
```

**Action required:**

- For local or CI builds, install the JAR into your local `~/.m2` repository:

  ```bash
  mvn install:install-file \
      -Dfile=seasar2/lib/uow_api-6.jar \
      -DgroupId=com.ibm.websphere \
      -DartifactId=uow_api \
      -Dversion=6 \
      -Dpackaging=jar
  ```

- If you do not deploy to WebSphere, the `provided`/`optional` scope means the dependency is not propagated to your application.

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

## 3. s2jdbc-gen: Doclet API → JavaParser Migration

### 3.1 What Changed

The legacy `com.sun.javadoc` Doclet API — used by `s2jdbc-gen` to extract Javadoc comments from entity source files for column/table metadata — was **removed from the JDK starting with Java 9**. The modernized fork replaces it entirely with [`com.github.javaparser:javaparser-core:3.25.10`](s2jdbc-gen/pom.xml:124), a pure-Java AST parsing library.

| Aspect | Before (Legacy) | After (Modernized) |
|---|---|---|
| **Parser** | `com.sun.javadoc` Doclet API | `com.github.javaparser:javaparser-core:3.25.10` |
| **JDK Dependency** | Requires `tools.jar` (JDK ≤8) or fails on JDK 9+ | No JDK-internal dependencies — compiles on all JDKs |
| **CommentDoclet** | Direct `com.sun.javadoc.*` references | Removed; replaced by [`JavadocASTReader`](s2jdbc-gen/src/main/java/org/seasar/extension/jdbc/gen/internal/meta/JavadocASTReader.java:44) |
| **Javadoc Extraction** | Silent skip on JDK 9+ | Fully functional on all JDK versions via JavaParser AST |
| **`@MappedSuperclass` Support** | N/A | Traverses superclass hierarchy for inherited Javadoc |

### 3.2 Key Classes

| Class | Purpose |
|---|---|
| [`JavadocASTReader`](s2jdbc-gen/src/main/java/org/seasar/extension/jdbc/gen/internal/meta/JavadocASTReader.java:44) | Parses `.java` source files using JavaParser AST; extracts class-level and field-level Javadoc comments |
| [`EntityMetaReaderImpl`](s2jdbc-gen/src/main/java/org/seasar/extension/jdbc/gen/internal/meta/EntityMetaReaderImpl.java:78) | Integrates `JavadocASTReader` for entity metadata generation; replaced previous `CommentDoclet`-based approach |

### 3.3 Impact on Existing Projects

- **No `tools.jar` dependency**: `s2jdbc-gen` compiles and runs on JDK 11, 17, and 21 without requiring a full JDK installation (works with JRE).
- **Full Javadoc extraction**: Unlike the intermediate "graceful degradation" approach (which silently skipped Javadoc on JDK 9+), the JavaParser backend extracts comment metadata on **all** JDK versions, including `@MappedSuperclass` hierarchy traversal.
- **No configuration changes needed**: The Ant/Maven task configuration for `s2jdbc-gen` remains identical.

---

## 4. Java 8 CLDR Note

### 4.1 Background

Starting with JDK 9, the JVM switched its default locale provider from `JRE` to `CLDR` (Unicode Common Locale Data Repository). This affects how `java.text.DateFormat` resolves date format patterns.

### 4.2 Impact on Tests

- **5 date-format tests** in [`DateConversionUtilTest`](seasar2/s2-framework/src/test/java/org/seasar/framework/util/DateConversionUtilTest.java) and [`TimestampConversionUtilTest`](seasar2/s2-framework/src/test/java/org/seasar/framework/util/TimestampConversionUtilTest.java) may **fail on native JDK 8** but **pass on JDK 11+**.
- **This is NOT a functional regression.** Production date-formatting behavior is correct on all supported JDK versions. The JRE locale provider on JDK 8 uses slightly different pattern strings than the CLDR provider on JDK 11+, but both produce correct dates.
- **Recommendation**: Run your test suite on JDK 11+ for accurate results. JDK 8 failures in these specific tests are expected and can be ignored if your production target is JDK 11+.

### 4.3 Workaround for JDK 8 Test Execution

If you need test parity on JDK 8, force the CLDR locale provider:

```
-Djava.locale.providers=CLDR,JRE
```

This makes JDK 8 use the same CLDR locale data as JDK 11+, eliminating the test differences.

### 4.4 Forcing JRE Locale Provider on JDK 9+

Conversely, to maintain JDK 8-compatible date behavior on JDK 9+, force the legacy JRE provider:

```
-Djava.locale.providers=COMPAT,CLDR
```

---

## 5. Build Instructions

### 5.1 Build Order

The module hierarchy requires building in this order due to inter-module dependencies:

```
1. seasar2/           (s2-framework → s2-extension)
2. s2-tiger/          (depends on s2-framework + s2-extension)
3. s2jdbc-gen/        (depends on s2-framework + s2-extension + s2-tiger)
```

### 5.2 Maven Build Commands

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

### 5.3 Useful Build Flags

| Command | Purpose |
|---|---|
| `mvn test -Dtest=ClassName` | Run a single test class |
| `mvn test -P<profile>` | Run tests with specific database profile |
| `mvn clean install -DskipTests` | Build without running tests |
| `mvn javadoc:javadoc` | Generate Javadoc |

### 5.4 Database Profiles

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

### 5.5 JDK Version Compatibility Matrix

| JDK Version | Build | Runtime | CI Tested | Notes |
|---|---|---|---|---|
| JDK 8 | ✅ | ✅ | ✅ | Full compatibility; `s2jdbc-gen` uses JavaParser (no `tools.jar` needed) |
| JDK 11 | ✅ | ✅ | ✅ | `--add-opens` required |
| JDK 17 | ✅ | ✅ | ✅ | `--add-opens` required; primary target version |
| JDK 21 | ✅ | ✅ | ✅ | `--add-opens` required; fully tested in CI |

### 5.6 `s2jdbc-gen` Javadoc Support (JavaParser — Current)

As of the modernized release, `s2jdbc-gen` uses [`com.github.javaparser:javaparser-core:3.25.10`](s2jdbc-gen/pom.xml:124) via [`JavadocASTReader`](s2jdbc-gen/src/main/java/org/seasar/extension/jdbc/gen/internal/meta/JavadocASTReader.java:44) for Javadoc extraction. This replaces the legacy `com.sun.javadoc` Doclet API (removed in JDK 9+). Javadoc comment extraction is now **fully functional on all JDK versions** — including `@MappedSuperclass` hierarchy traversal. See [Section 3](#3-s2jdbc-gen-doclet-api--javaparser-migration) for details.

---

## 6. Upgrading from Original Seasar 2.4.x

If you are upgrading an application from the original Seasar 2.4.x (`seasarorg/seasar2`) to this modernized fork, follow this step-by-step checklist.

### 6.1 Upgrade Checklist

#### Phase 1: Preparation

- [ ] **Inventory all `.dicon` files** in your project — list every file that references `<components>` or includes other dicon files
- [ ] **Audit OGNL expressions** — in all `.dicon` and `.sql` (S2JDBC templates) files, search for:
  - `@java.lang.Runtime@`
  - `@java.lang.Class@`
  - `@java.lang.System@`
  - `@java.lang.reflect@`
  - `Class.forName`
  - `java.lang.ProcessBuilder`
- [ ] **Identify `s2jdbc-gen` usage** — if you use the code generation Ant task or Maven plugin, note your current configuration
- [ ] **Record current JDK version** used in development, CI, staging, and production

#### Phase 2: Dependency Update

- [ ] **Update Maven/Gradle dependencies**: Change Seasar2 artifact versions from `2.4.x` to `2.4.49`
- [ ] **Remove any `tools.jar` references** from build scripts if they were needed for `s2jdbc-gen` (no longer required)
- [ ] **Add `com.github.javaparser:javaparser-core:3.25.10`** to `s2jdbc-gen` dependencies if building from source

#### Phase 3: JVM Configuration

- [ ] **Add `--add-opens` flags** for JDK 9+ environments (see [Section 1.3](#13-configuration-examples)):
  - `--add-opens java.base/java.lang=ALL-UNNAMED`
  - `--add-opens java.base/java.util=ALL-UNNAMED`
  - `--add-opens java.base/java.math=ALL-UNNAMED`
  - `--add-opens java.base/java.net=ALL-UNNAMED`
- [ ] **Configure Maven Surefire/Failsafe** with these flags in `<argLine>` (for JDK 9+ profiles)
- [ ] **Update IDE run configurations** (IntelliJ IDEA, Eclipse) with these VM options
- [ ] **Update production startup scripts** (shell scripts, Docker `JAVA_OPTS`, Kubernetes manifests)

#### Phase 4: OGNL Migration

- [ ] **Replace blocked OGNL expressions** (see [Section 2.4](#24-migration-for-affected-expressions)):
  - `Runtime.exec()` / `ProcessBuilder` → move to Java component code
  - `System.exit()` → remove or replace with managed shutdown
  - `Class.forName()` → use Seasar2 component binding
  - Reflective `invoke()`/`setAccessible()` → use Seasar2's DI container
- [ ] **Validate S2JDBC SQL templates** — ensure `IF`/`BEGIN` blocks do not invoke system classes

#### Phase 5: Testing & Validation

- [ ] **Run full test suite** on target JDK (11+ recommended)
- [ ] **Verify `s2jdbc-gen` entity generation** — confirm Javadoc comments are extracted correctly with the new JavaParser backend
- [ ] **XXE security verification** — confirm no `.dicon` files reference external entities
- [ ] **Smoke test in staging** with the same `--add-opens` flags as production
- [ ] **Test HOT deploy** if used — verify the Issue #15 ClassLoader fix resolves any previous field-injection failures

### 6.2 Common Issues

| Symptom | Cause | Solution |
|---|---|---|
| `InaccessibleObjectException` at startup | Missing `--add-opens` flags | Add all four flags (see [Section 1](#1-jvm-arguments-java-9)) |
| `SecurityException: Access denied` in OGNL | OGNL expression blocked by sandbox | Migrate expression to Java code (see [Section 2.4](#24-migration-for-affected-expressions)) |
| `[ESSR0094] Can not set field` on HOT deploy | Issue #15 ClassLoader mismatch | Fixed in this release; ensure you are on `2.4.49` |
| `PersistenceException: Persistence unit not found: X` | Missing persistence unit | Now throws clear `PersistenceException` with unit name |
| `s2jdbc-gen` build fails on JDK 21 | Old version using `com.sun.javadoc` | Update to modernized `s2jdbc-gen` with JavaParser |
| Date-format test failures on JDK 8 | CLDR vs JRE locale provider | Expected; use `-Djava.locale.providers=CLDR,JRE` (see [Section 4.3](#43-workaround-for-jdk-8-test-execution)) |

---

## 7. Verification Checklist

After migration, confirm each item:

- [ ] **JVM `--add-opens` flags** added to all environments (development, CI, staging, production)
- [ ] **Maven Surefire/Failsafe** `<argLine>` includes all four `--add-opens` flags
- [ ] **IDE run configurations** updated (IntelliJ IDEA and/or Eclipse)
- [ ] **OGNL audit complete** — no expressions accessing `Runtime.exec()`, `ProcessBuilder`, `System.exit()`, `Class.forName()`, `ClassLoader`, or reflection APIs
- [ ] **S2JDBC SQL templates** reviewed — `IF`/`BEGIN` blocks do not reference system classes
- [ ] **Test suite passes** on target JDK version (JDK 11+ recommended)
- [ ] **Date format tests** confirmed on JDK 11+ (JDK 8 failures are expected and acceptable)
- [ ] **`s2jdbc-gen` entity generation** verified — Javadoc extracted via JavaParser on all JDK versions
- [ ] **`s2jdbc-gen` build** confirmed — no `tools.jar` dependency; compiles on JDK 21
- [ ] **XXE protection** confirmed — no external entity references in `.dicon` files
- [ ] **Production deployment** smoke-tested with `--add-opens` flags on target JVM

---

## Related Documents

- [Changelog](CHANGELOG.md) — detailed list of all changes in this release
- [Technical Debt & Future Work](TECHNICAL_DEBT_ISSUES.md) — known limitations and planned improvements
- [Development Guide](DEVELOPMENT.md) — build and contribution instructions

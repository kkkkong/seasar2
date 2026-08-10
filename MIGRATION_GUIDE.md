# Migration Guide — Seasar2 Modernization (JDK 8 → 17)

This guide covers the required changes when upgrading your Seasar2-based application from the legacy version to the modernized release supporting JDK 8, 11, and 17 with security hardening.

---

## 1. Overview

Seasar2 has been modernized to build and run on JDK 8, 11, and 17. Key changes include:

- **JVM module system compatibility**: JDK 9+ encapsulates internal APIs that Seasar2's DI container, AOP proxy generation, and HotdeployBehavior rely on. `--add-opens` flags are now required at runtime.
- **XXE (XML External Entity) protection**: XML parsers used for `.dicon` file loading have been hardened against XXE attacks.
- **OGNL expression sandbox**: A security sandbox now blocks dangerous OGNL expressions that could lead to remote code execution.
- **Javadoc-free compilation**: `s2jdbc-gen` now compiles and runs without `tools.jar` (which was removed in JDK 9+).

The following sections detail each change and the steps you must take.

---

## 2. JVM Arguments (CRITICAL)

### 2.1 Required `--add-opens` Flags

On **Java 9 and later** (including JDK 11, JDK 17, JDK 21+), you **MUST** add the following `--add-opens` JVM arguments:

```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.math=ALL-UNNAMED
--add-opens java.base/java.net=ALL-UNNAMED
```

### 2.2 Why These Are Needed

Seasar2's core features use Java reflection to access internal JDK APIs that are encapsulated by the Java Platform Module System (JPMS) introduced in JDK 9:

| Internal API | Used By | Purpose |
|---|---|---|
| `ClassLoader.defineClass()` | DI container, AOP proxy generation, HotdeployBehavior | Dynamic class generation for proxies and hot-deployed components |
| `java.lang.reflect.AccessibleObject` internals | DI container, OGNL expression evaluation | Field/method access for dependency injection |
| `java.util` / `java.math` internals | Type conversion utilities | `BigDecimal`, collection manipulation via reflection |
| `java.net` internals | Resource loading, classpath scanning | `URLClassLoader` access for component auto-discovery |

Without these flags, you will encounter `java.lang.reflect.InaccessibleObjectException` at startup, typically with a message like:

```
Unable to make field private final ... accessible: module java.base does not "opens java.lang" to unnamed module
```

### 2.3 Configuration Examples

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
3. In **VM options**, add:

```
--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED
```

#### Eclipse Run Configuration

1. Open **Run → Run Configurations**
2. Select your configuration → **Arguments** tab
3. In **VM arguments**, add the same flags as above.

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

### 2.4 `s2jdbc-gen` Javadoc Limitation

If you use `s2jdbc-gen` for entity generation on **JDK 9+**, note that Javadoc comment extraction is **silently skipped**. The `com.sun.javadoc` API was removed from the JDK starting with Java 9. Entity metadata generation will still complete successfully, but without doc-comment-based metadata (such as `@Column` descriptions extracted from Javadoc). This is expected behavior and not a bug.

---

## 3. OGNL Security Sandbox Restrictions

### 3.1 What Changed

A new [`OgnlSecurityMemberAccess`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/OgnlSecurityMemberAccess.java:27) sandbox has been introduced to prevent arbitrary code execution through OGNL expressions in `.dicon` files, SQL templates (`S2JDBC`), and JSP views.

### 3.2 Blocked Operations

OGNL expressions can **NO LONGER** access:

| Category | Blocked Classes/Methods |
|---|---|
| Process execution | `java.lang.Runtime.exec()`, `java.lang.ProcessBuilder` |
| System manipulation | `java.lang.System.exit()`, `System.setProperty()` |
| Dynamic class loading | `java.lang.Class.forName()` |
| ClassLoader access | `java.lang.ClassLoader` and subclasses |
| Reflection bypass | `java.lang.reflect.AccessibleObject.setAccessible()` |
| Reflective invocation | `java.lang.reflect.Method.invoke()`, `Field.set()`, `Constructor.newInstance()` |
| Internal packages | `java.lang.invoke.*`, `sun.*`, `com.sun.*` |

### 3.3 What Is Still Allowed

- Reading public static fields (e.g., `@java.lang.reflect.Modifier@PUBLIC`)
- Calling getters and setters on your own beans (e.g., `#method.name`, `propertyName`)
- Accessing component properties through standard OGNL navigation
- Boolean expressions, arithmetic, string operations, and collection access

### 3.4 Migration for Affected Expressions

If your `.dicon` or SQL template (`S2JDBC`) contains OGNL expressions that hit the sandbox, you will see errors like:

```
ognl.MethodFailedException: java.lang.SecurityException: Access denied ...
```

**Before (Blocked):**
```xml
<!-- This will now fail -->
<component name="service" class="com.example.Service">
    <property name="command">@java.lang.Runtime@getRuntime().exec("ls")</property>
</component>
```

**After (Safe Migration):**
```java
// Move system-level logic into Java code
package com.example;

public class Service {
    private String commandResult;

    public Service() {
        // Use Seasar2's component binding for complex initialization
        this.commandResult = executeBusinessLogic();
    }

    private String executeBusinessLogic() {
        // System interactions here
    }
}
```

**General Migration Advice:**

1. **Audit your `.dicon` files** — search for `@java.lang.Runtime`, `@java.lang.Class`, `@java.lang.System`, and `@java.lang.reflect` references.
2. **Move system-level logic** out of OGNL expressions into Java component code.
3. **Use component binding** — inject dependencies via Seasar2's container rather than constructing them through reflection in OGNL.
4. **For SQL templates (S2JDBC)**: Ensure `IF` / `BEGIN` blocks in `.sql` files do not reference system classes.

---

## 4. Date Format CLDR Note

### 4.1 Background

Starting with JDK 9, the JVM switched its default locale provider from `JRE` to `CLDR` (Unicode Common Locale Data Repository). This affects how `java.text.DateFormat` patterns are resolved.

### 4.2 Impact

- For **Japanese locale (`ja-JP`)**: Date patterns are identical between JRE and CLDR providers. No impact.
- For **other locales**: [`DateConversionUtil.getPattern()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/DateConversionUtil.java) may return different date format patterns on JDK 9+ compared to JDK 8.

### 4.3 Test Suite Note

If your CI/CD runs tests on both JDK 8 and JDK 11+:

- **5 date-format tests** in `DateConversionUtilTest` and `TimestampConversionUtilTest` may **fail on JDK 8** but **pass on JDK 11+**.
- This is a **test-only issue** caused by locale provider differences. Production behavior is consistent on JDK 11+.
- **Recommendation**: Run your test suite on JDK 11+ for accurate results. JDK 8 test failures in these specific tests are expected and can be ignored if your production target is JDK 11+.

### 4.4 Forcing JRE Locale Provider (JDK 9+)

If you need to maintain JDK 8-compatible date behavior on JDK 9+, you can force the JRE locale provider:

```
-Djava.locale.providers=COMPAT,CLDR
```

This restores the legacy JRE locale data and ensures identical date format patterns across JDK versions.

---

## 5. Build Configuration

### 5.1 Root POM Changes

The root [`pom.xml`](seasar2/pom.xml:337) now defines `--add-opens` flags in the `maven-surefire-plugin` `<pluginManagement>` section. If your project extends or overrides the Surefire/Failsafe configuration, ensure you merge these flags.

### 5.2 Merging with Custom Surefire Configuration

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>
            <!-- Your existing JVM args (e.g., memory settings) -->
            -Xmx1024m -XX:MaxMetaspaceSize=256m
            <!-- Required add-opens for Seasar2 -->
            --add-opens java.base/java.lang=ALL-UNNAMED
            --add-opens java.base/java.util=ALL-UNNAMED
            --add-opens java.base/java.math=ALL-UNNAMED
            --add-opens java.base/java.net=ALL-UNNAMED
        </argLine>
    </configuration>
</plugin>
```

**Important**: If you use both `<argLine>` in Surefire and Failsafe, they are independent — you must configure both plugins.

### 5.3 JDK Version Compatibility Matrix

| JDK Version | Build Status | Runtime Status | Notes |
|---|---|---|---|
| JDK 8 | ✅ Supported | ✅ Supported | Requires `tools.jar` on classpath for full `s2jdbc-gen` Javadoc support |
| JDK 11 | ✅ Supported | ✅ Supported | `--add-opens` required; `s2jdbc-gen` Javadoc extraction silently skipped |
| JDK 17 | ✅ Supported | ✅ Supported | `--add-opens` required; `s2jdbc-gen` Javadoc extraction silently skipped |
| JDK 21+ | ⚠️ Untested | ⚠️ Untested | May require additional `--add-opens` as more APIs are encapsulated |

---

## 6. Verification Checklist

Use this checklist to ensure your migration is complete:

- [ ] **JVM `--add-opens` flags** added to all runtime environments (development, CI, staging, production)
- [ ] **Maven Surefire/Failsafe** `<argLine>` includes all four `--add-opens` flags
- [ ] **IDE run configurations** updated (IntelliJ IDEA and/or Eclipse)
- [ ] **No OGNL expressions** accessing system classes (`java.lang.Runtime`, `Class.forName()`, `System.exit()`, etc.) — review all `.dicon` files
- [ ] **No OGNL expressions** accessing reflection APIs in SQL templates (`.sql` files for S2JDBC)
- [ ] **Test suite passes** on target JDK version (JDK 11+ recommended)
- [ ] **Date format tests** verified on JDK 11+ (JDK 8 failures in `DateConversionUtilTest` and `TimestampConversionUtilTest` are expected)
- [ ] **`s2jdbc-gen` entity generation** verified — Javadoc comment extraction is silently skipped on JDK 9+; confirm generated entities are functionally correct
- [ ] **XXE protection** confirmed — no external entity references in `.dicon` files (DTD validation for dicon schema is still supported)
- [ ] **Production deployment** smoke-tested with `--add-opens` flags on target JVM

---

## 7. Rollback Procedure

If you need to temporarily revert to the previous Seasar2 version:

1. Replace the Seasar2 JARs with the previous version
2. Remove `--add-opens` flags (if you added them only for Seasar2)
3. Restore any OGNL expressions that were modified for the sandbox
4. Re-run the full test suite to confirm no regressions

Note: Older Seasar2 versions will **not** run on JDK 17+ without the `--add-opens` flags. If you must run on JDK 17+, keep the flags regardless of Seasar2 version.

---

## 8. Getting Help

- Review the [Changelog](CHANGELOG.md) for a detailed list of all changes
- See [Technical Debt & Future Work](TECHNICAL_DEBT_ISSUES.md) for known limitations and planned improvements
- Check the [Seasar2 Development Guide](DEVELOPMENT.md) for build and contribution instructions

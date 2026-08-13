# Technical Debt & Future Work — Seasar2 Modernized Fork

This document tracks known technical debt and planned future improvements. Each issue is presented in a GitHub-style format with background, proposed solution, benefits, challenges, and acceptance criteria.

---

## Issue 1: Refactor `ClassLoader.defineClass()` Reflection with `MethodHandles.Lookup.defineClass()`

**Labels:** `enhancement` `java9+` `technical-debt` `security`

### Summary

Replace reflective access to `ClassLoader.defineClass()` with the supported [`java.lang.invoke.MethodHandles.Lookup.defineClass()`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#defineClass(byte%5B%5D)) API available since Java 9. This would eliminate the most security-sensitive `--add-opens` flag (`java.base/java.lang=ALL-UNNAMED`).

### Background

Seasar2's DI container, AOP proxy generation, and HotdeployBehavior dynamically generate classes at runtime using Javassist. To define these classes in the JVM, the framework reflectively calls the `protected` method `ClassLoader.defineClass()`:

```java
// Current approach (requires --add-opens java.base/java.lang=ALL-UNNAMED)
Method defineClass = ClassLoader.class.getDeclaredMethod(
    "defineClass", String.class, byte[].class, int.class, int.class
);
defineClass.setAccessible(true);
Class<?> proxyClass = (Class<?>) defineClass.invoke(
    classLoader, className, bytecode, 0, bytecode.length
);
```

JDK 9+ encapsulates `ClassLoader.defineClass()` behind module boundaries, which is why the `--add-opens java.base/java.lang=ALL-UNNAMED` flag is currently required.

### Affected Files

| File | Role |
|---|---|
| [`S2ContainerFactory.java`](seasar2/s2-framework/src/main/java/org/seasar/framework/container/factory/S2ContainerFactory.java) | DI container initialization and class loading |
| AOP proxy classes (various under `s2-framework/.../aop/`) | Bytecode generation for method interceptors |
| HotdeployBehavior classes (various under `s2-framework/.../hotdeploy/`) | Dynamic class reloading |
| [`ClassUtil.java`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/ClassUtil.java) (or equivalent utility) | `ClassLoader.defineClass()` wrapper utilities |

### Proposed Solution

Replace with the supported `java.lang.invoke.MethodHandles.Lookup.defineClass()`:

```java
// Proposed approach (no --add-opens needed for java.lang)
MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
    TargetClass.class, MethodHandles.lookup()
);
byte[] classBytes = javassistProxy.toBytecode();
Class<?> proxyClass = lookup.defineClass(classBytes);
```

A fallback path must be preserved for JDK 8 where `Lookup.defineClass()` does not exist:

```java
try {
    // JDK 9+: Use supported Lookup.defineClass() API
    MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
    return lookup.defineClass(classBytes);
} catch (NoSuchMethodError | IllegalAccessException e) {
    // JDK 8 fallback: reflection-based ClassLoader.defineClass()
    return defineClassViaReflection(classLoader, className, classBytes);
}
```

### Benefits

- **Eliminates `--add-opens java.base/java.lang=ALL-UNNAMED`**: Removes the most security-sensitive JVM flag (access to `java.lang` internals)
- **Uses a supported, standard API**: `Lookup.defineClass()` is public and documented — far less likely to be removed or restricted in future JDK releases
- **Future-proof for JDK 21+**: As the JDK continues encapsulating internal APIs, the reflection-based approach becomes riskier with each release
- **Improved security posture**: Reduces `--add-opens` surface from 4 flags to 3, aligning with the principle of least privilege

### Challenges

1. **`Lookup` access requirements**: `MethodHandles.Lookup.defineClass()` requires a `Lookup` with `PRIVATE` access on a target class in the **same package** as the generated proxy. May require restructuring proxy package placement.

2. **Javassist compatibility**: Javassist's `CtClass.toBytecode()` returns `byte[]` — this should be directly compatible, but the class must not have been previously loaded by a different `ClassLoader`.

3. **JDK 8 backward compatibility**: Must preserve the reflection-based fallback path for JDK 8 (`Lookup.defineClass()` does not exist in JDK 8).

4. **Package-private access**: Generated proxy classes need access to internal Seasar2 packages. The `Lookup`-based approach must ensure correct package-level visibility.

5. **HotdeployBehavior complexity**: The hot-deploy mechanism's dynamic class reloading may interact differently with `Lookup.defineClass()`. Requires thorough testing under HotdeployBehavior.

### Acceptance Criteria

- [ ] All existing proxy-generation tests pass **without** `--add-opens java.base/java.lang=ALL-UNNAMED` on JDK 11+
- [ ] Reflection-based fallback preserved and tested on JDK 8 — no regressions
- [ ] No performance regression in proxy class creation
- [ ] HotdeployBehavior class reloading works correctly with the new approach
- [ ] AOP proxy generation works for both interface-based and class-based proxies
- [ ] [`MIGRATION_GUIDE.md`](MIGRATION_GUIDE.md) updated to remove the `java.lang` add-opens requirement
- [ ] [`CHANGELOG.md`](CHANGELOG.md) updated with the change

### Estimated Effort

| Task | Estimate |
|---|---|
| Research & prototype `Lookup.defineClass()` integration | 2–3 days |
| Implement core change with JDK 8 fallback | 2–3 days |
| Adapt HotdeployBehavior | 1–2 days |
| Full regression testing (JDK 8, 11, 17) | 2–3 days |
| Documentation updates | 0.5 day |
| **Total** | **~8–12 days** |

---

## Issue 2: Migrate `javax.*` to `jakarta.*` Namespace for Servlet Container Compatibility

**Labels:** `enhancement` `jakarta` `servlet` `breaking-change` `java11+`

### Summary

Replace all `javax.servlet.*`, `javax.transaction.*`, `javax.annotation.*`, and `javax.persistence.*` imports with their `jakarta.*` equivalents. This is a **breaking change** required for compatibility with modern Servlet containers (Tomcat 10+, Jetty 11+, WildFly 27+).

### Background

Seasar2 currently uses `javax.servlet.*` and other `javax.*` packages from Java EE 8 and earlier. With Jakarta EE 9 (2020), all APIs moved from `javax.*` to `jakarta.*` — a deliberate breaking change by the Eclipse Foundation.

**Affected container compatibility:**

| Container | `javax.*` Support | `jakarta.*` Support |
|---|---|---|
| Tomcat 9 | ✅ Yes | ❌ No |
| Tomcat 10+ | ❌ No | ✅ Yes |
| Jetty 10 | ✅ Yes | ❌ No |
| Jetty 11+ | ❌ No | ✅ Yes |
| WildFly 26 and earlier | ✅ Yes | ❌ No |
| WildFly 27+ | ❌ No | ✅ Yes |
| GlassFish 6 and earlier | ✅ Yes | ❌ No |
| GlassFish 7+ | ❌ No | ✅ Yes |
| Payara 6+ | ❌ No | ✅ Yes |
| Open Liberty 23+ | ❌ No | ✅ Yes |

**Impact**: Applications on Seasar2 **cannot deploy** to Tomcat 10+, Jetty 11+, or any modern Jakarta EE 9+ container. As older containers reach end-of-life, this becomes a critical blocker.

### Affected Modules

| Module | `javax.*` Dependency | Replacement |
|---|---|---|
| `s2-extension` | `javax.servlet.*` (filters, request dump) | `jakarta.servlet.*` |
| `s2-extension` | `javax.transaction.*` (JTA) | `jakarta.transaction.*` |
| `s2-tiger` | `javax.servlet.*` (web test utilities) | `jakarta.servlet.*` |
| `s2-tiger` | `javax.persistence.*` (JPA) | `jakarta.persistence.*` |
| `s2-tiger` | `javax.annotation.*` | `jakarta.annotation.*` |
| All modules | `javax.ejb.*` | `jakarta.ejb.*` |

### Proposed Solution

#### Phase 1: Audit

```bash
# Find all javax.* imports in Java source
grep -r "import javax\." --include="*.java" .

# Find javax.* in dicon files
grep -r "javax\." --include="*.dicon" .

# Find javax.* in Maven POM dependencies
grep -r "javax\." --include="pom.xml" .
```

#### Phase 2: Update Maven Dependencies

| Current Dependency | Jakarta Replacement |
|---|---|
| `org.apache.geronimo.specs:geronimo-j2ee_1.4_spec` | `jakarta.platform:jakarta.jakartaee-api:9.1.0` |
| `org.apache.geronimo.specs:geronimo-servlet_2.4_spec` | `jakarta.servlet:jakarta.servlet-api:5.0.0` |
| `org.apache.geronimo.specs:geronimo-ejb_2.1_spec` | `jakarta.ejb:jakarta.ejb-api:4.0.0` |
| `org.apache.geronimo.specs:geronimo-jta_1.1_spec` | `jakarta.transaction:jakarta.transaction-api:2.0.1` |
| `org.apache.geronimo.specs:geronimo-annotation_1.0_spec` | `jakarta.annotation:jakarta.annotation-api:2.1.1` |
| `org.apache.geronimo.specs:geronimo-jpa_3.0_spec` | `jakarta.persistence:jakarta.persistence-api:3.1.0` |

#### Phase 3: Create Jakarta Migration Branch

```bash
git checkout -b branch-jakarta
```

This branch should:
- Target **JDK 11+ only** (Jakarta EE 9+ requires JDK 11)
- Replace all `javax.*` imports with `jakarta.*` equivalents
- Update all Maven POM dependencies
- Update `.dicon` files referencing `javax.*` classes
- Run full regression test suite

#### Phase 4: Migration Tooling for Downstream Consumers

Provide an automated migration script:

```bash
find . -name "*.java" -exec sed -i \
  -e 's/import javax\.servlet\./import jakarta.servlet./g' \
  -e 's/import javax\.transaction\./import jakarta.transaction./g' \
  -e 's/import javax\.annotation\./import jakarta.annotation./g' \
  -e 's/import javax\.ejb\./import jakarta.ejb./g' \
  -e 's/import javax\.persistence\./import jakarta.persistence./g' \
  {} +
```

> **Note:** `javax.sql.*`, `javax.naming.*`, and `javax.xml.*` remain in the JDK and must **NOT** be migrated.

### Challenges

1. **Breaking change**: Hard break — all downstream code extending Seasar2 web classes must also migrate. No binary compatibility path.
2. **Major version bump**: Warrants **Seasar2 3.0.0** for the Jakarta branch; javax branch stays at 2.x.
3. **JDK 8 incompatibility**: Jakarta EE 9+ requires JDK 11+. Jakarta branch must drop JDK 8 support.
4. **Dual maintenance burden**: Bug fixes may need backporting between `branch-javax` (2.x) and `branch-jakarta` (3.x).
5. **Downstream ecosystem impact**: All dependent projects must migrate their own `javax.*` imports and upgrade their Servlet container.

### Interim Workaround for Downstream Consumers

The [Apache Tomcat Migration Tool for Jakarta EE](https://github.com/apache/tomcat-jakartaee-migration) can automatically transform `javax.*` to `jakarta.*` at the bytecode level in existing WAR/EAR files. This works without source changes but is a stopgap — Seasar2 should eventually move to `jakarta.*` natively.

### Acceptance Criteria

- [ ] Zero `javax.servlet.*`, `javax.transaction.*`, `javax.annotation.*`, `javax.ejb.*`, `javax.persistence.*` imports remaining
- [ ] All Maven POM files updated with Jakarta EE 9+ dependencies
- [ ] All `.dicon` files reviewed and updated for `jakarta.*` references
- [ ] Full regression test suite passes on Tomcat 10+ with JDK 11+
- [ ] Migration script/documentation provided for downstream consumers
- [ ] New Maven coordinates or classifier to distinguish javax (2.x) vs jakarta (3.x) builds
- [ ] [`MIGRATION_GUIDE.md`](MIGRATION_GUIDE.md) updated with `javax → jakarta` migration steps
- [ ] [`CHANGELOG.md`](CHANGELOG.md) updated with breaking change notice
- [ ] [`README.md`](README.md) updated with JDK version requirements per branch
- [ ] `CONTRIBUTING.md` updated with branch strategy

### Estimated Effort

| Task | Estimate |
|---|---|
| Audit all `javax.*` references | 1 day |
| Implement import migration (automated + manual review) | 3–5 days |
| Update Maven dependencies | 1 day |
| Update `.dicon` files | 0.5 day |
| Full regression testing (JDK 11, 17) on Tomcat 10+ | 3–5 days |
| Migration tooling for downstream | 2–3 days |
| Documentation updates | 1–2 days |
| **Total** | **~12–18 days** |

---

## Priority & Timeline

| Issue | Priority | Recommended Timeline |
|---|---|---|
| Issue 2: `javax → jakarta` | **High** | Within 6 months — Tomcat 9 end-of-life approaching |
| Issue 1: `Lookup.defineClass()` | Medium | Within 6–12 months — before JDK 21 becomes dominant LTS |

---

## Related Documents

- [Changelog — Seasar2 Modernized Fork](CHANGELOG.md)
- [Migration Guide — Seasar2 Modernized Fork](MIGRATION_GUIDE.md)
- [Development Guide](DEVELOPMENT.md)
- [Contributing Guidelines](CONTRIBUTING.md)

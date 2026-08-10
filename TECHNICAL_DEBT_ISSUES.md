# Technical Debt & Future Work — Seasar2

This document tracks known technical debt and planned future improvements for the Seasar2 modernization effort. Each issue is presented in a GitHub-style format with background, proposed solution, benefits, challenges, and acceptance criteria.

---

## Issue 1: Replace `ClassLoader.defineClass()` Reflection with `MethodHandles.Lookup.defineClass()`

**Priority:** Medium  
**Labels:** `refactoring`, `java9+`, `jvm-compatibility`  
**Affected Areas:** `s2-framework` (DI container, AOP proxy generation, HotdeployBehavior)

### Background

Seasar2 currently uses reflection to access `ClassLoader.defineClass()` for dynamic proxy class generation. The core DI container and AOP framework generate bytecode at runtime (via [Javassist](https://www.javassist.org/)) and must define new classes in the JVM. The legacy approach calls the `protected` method `ClassLoader.defineClass()` through `setAccessible(true)`:

```java
// Current approach (simplified)
Method defineClass = ClassLoader.class.getDeclaredMethod(
    "defineClass", String.class, byte[].class, int.class, int.class
);
defineClass.setAccessible(true);
Class<?> proxyClass = (Class<?>) defineClass.invoke(
    classLoader, className, bytecode, 0, bytecode.length
);
```

JDK 9+ encapsulates this method behind module boundaries, requiring `--add-opens java.base/java.lang=ALL-UNNAMED` at JVM startup. This workaround functions correctly on JDK 9 through JDK 21, but it is fragile — future JDK versions may remove the method entirely or further tighten module encapsulation.

**Key files involved:**

| File | Role |
|---|---|
| `s2-framework/.../container/factory/S2ContainerFactory.java` | DI container initialization, uses reflection for class loading |
| `s2-framework/.../aop/` (various) | AOP proxy bytecode generation |
| `s2-framework/.../hotdeploy/` (various) | HotdeployBehavior dynamic class reloading |
| `s2-framework/.../util/ClassUtil.java` | Utility methods wrapping `ClassLoader.defineClass()` |

### Proposed Solution

Replace reflective `ClassLoader.defineClass()` calls with [`java.lang.invoke.MethodHandles.Lookup.defineClass()`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#defineClass(byte%5B%5D)), available since Java 9:

```java
// Proposed approach
MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
    TargetClass.class, MethodHandles.lookup()
);
byte[] classBytes = javassistProxy.toBytecode(); // generated proxy bytecode
Class<?> proxyClass = lookup.defineClass(classBytes);
```

### Benefits

- **Eliminates `--add-opens java.base/java.lang=ALL-UNNAMED`**: Reduces the required JVM flags from 4 to 3, and removes the most security-sensitive one (access to `java.lang` internals).
- **Uses a supported, standard API**: `Lookup.defineClass()` is a public, documented method that is less likely to be removed or restricted in future JDK releases.
- **Future-proof for JDK 21+**: As the JDK continues to encapsulate internal APIs, the reflection-based approach becomes riskier with each release.
- **Improved security posture**: Reducing `--add-opens` surface area aligns with the principle of least privilege.

### Challenges

1. **`Lookup` access requirements**: `MethodHandles.Lookup.defineClass()` requires a `Lookup` with `PRIVATE` access on a target class in the **same package** as the generated proxy. This may require restructuring how proxy classes are generated and which package they live in. [`MethodHandles.privateLookupIn()`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.html#privateLookupIn(java.lang.Class,java.lang.invoke.MethodHandles.Lookup)) can be used to obtain the necessary access, but the target class must be carefully chosen.

2. **Javassist compatibility**: The existing codebase uses Javassist for bytecode manipulation. Need to verify that Javassist can output raw `byte[]` compatible with `Lookup.defineClass()`. Javassist's `CtClass.toBytecode()` returns `byte[]`, which should be directly compatible, but the class must not have already been loaded by a different `ClassLoader`.

3. **Backward compatibility with JDK 8**: `MethodHandles.Lookup.defineClass()` does not exist in JDK 8. The old reflection path must be preserved for JDK 8 environments. Use a `try-catch` fallback pattern:

    ```java
    try {
        // Try JDK 9+ Lookup.defineClass() first
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
        return lookup.defineClass(classBytes);
    } catch (NoSuchMethodError | IllegalAccessException e) {
        // Fallback to reflection-based ClassLoader.defineClass() on JDK 8
        return defineClassViaReflection(classLoader, className, classBytes);
    }
    ```

4. **Package-private access**: Generated proxy classes need access to internal Seasar2 packages. The `Lookup` approach may require careful placement of the anchor class to ensure the generated class has the right package-level access.

5. **HotdeployBehavior complexity**: The hot-deploy mechanism reloads classes dynamically, which may interact differently with `Lookup.defineClass()` compared to the reflection path. Thorough testing under HotdeployBehavior is essential.

### Acceptance Criteria

- [ ] All existing proxy-generation tests pass **without** `--add-opens java.base/java.lang=ALL-UNNAMED` on JDK 11+
- [ ] Old reflection path preserved and tested on JDK 8 — no regressions
- [ ] No performance regression in proxy class creation (benchmark comparison)
- [ ] HotdeployBehavior class reloading works correctly with the new approach
- [ ] AOP proxy generation works correctly (both interface-based and class-based proxies)
- [ ] [`MIGRATION_GUIDE.md`](MIGRATION_GUIDE.md) updated to remove the `java.lang` add-opens requirement
- [ ] [`CHANGELOG.md`](CHANGELOG.md) updated with the change

### Estimated Effort

| Task | Estimate |
|---|---|
| Research & prototype `Lookup.defineClass()` integration | 2-3 days |
| Implement core change with fallback | 2-3 days |
| Adapt HotdeployBehavior | 1-2 days |
| Full regression testing (JDK 8, 11, 17) | 2-3 days |
| Documentation updates | 0.5 day |
| **Total** | **~8-12 days** |

---

## Issue 2: Migrate from `javax.*` to `jakarta.*` Namespace

**Priority:** High  
**Labels:** `jakarta`, `servlet`, `breaking-change`, `tomcat10+`  
**Affected Areas:** `s2-extension` (Servlet filter, request dump), `s2-tiger` (web test utilities), `s2-framework` (any `javax.*` references)

### Background

Seasar2 currently uses `javax.servlet.*`, `javax.transaction.*`, and other `javax.*` packages from Java EE / Jakarta EE 8 and earlier. With the release of Jakarta EE 9 (2020), all APIs moved from the `javax.*` namespace to the `jakarta.*` namespace. This was a deliberate, breaking change by the Eclipse Foundation to signal the transition from Oracle-led Java EE to community-led Jakarta EE.

Modern Servlet containers have dropped `javax.*` support:

| Container | `javax.*` Support | `jakarta.*` Support |
|---|---|---|
| Tomcat 9 | ✅ Yes | ❌ No |
| Tomcat 10+ | ❌ No | ✅ Yes |
| Jetty 10 | ✅ Yes | ❌ No |
| Jetty 11+ | ❌ No | ✅ Yes |
| Jetty 12+ | ❌ No | ✅ Yes |
| WildFly 26 and earlier | ✅ Yes | ❌ No |
| WildFly 27+ | ❌ No | ✅ Yes |
| GlassFish 6 and earlier | ✅ Yes | ❌ No |
| GlassFish 7+ | ❌ No | ✅ Yes |
| Payara 6+ | ❌ No | ✅ Yes |
| Open Liberty 23+ | ❌ No | ✅ Yes |

**Impact**: Applications built on Seasar2 **cannot deploy** to Tomcat 10+, Jetty 11+, WildFly 27+, or any modern Jakarta EE 9+ container. As older containers reach end-of-life, this becomes a critical blocker.

### Proposed Solution

#### Phase 1: Identify All `javax.*` References

Run a comprehensive audit across the entire codebase:

```bash
# Find all javax.* imports in Java source
grep -r "import javax\." --include="*.java" .

# Find javax.* in dicon files
grep -r "javax\." --include="*.dicon" .

# Find javax.* in Maven POM dependencies
grep -r "javax\." --include="pom.xml" .
```

Expected categories of affected code:

| Current Import | Target Import |
|---|---|
| `javax.servlet.*` | `jakarta.servlet.*` |
| `javax.servlet.http.*` | `jakarta.servlet.http.*` |
| `javax.servlet.annotation.*` | `jakarta.servlet.annotation.*` |
| `javax.transaction.*` | `jakarta.transaction.*` |
| `javax.annotation.*` | `jakarta.annotation.*` |
| `javax.ejb.*` (if used) | `jakarta.ejb.*` |
| `javax.persistence.*` (s2-tiger) | `jakarta.persistence.*` |

#### Phase 2: Update Maven Dependencies

| Current Dependency (GroupId : ArtifactId) | Jakarta Replacement | Notes |
|---|---|---|
| `org.apache.geronimo.specs:geronimo-j2ee_1.4_spec` | `jakarta.platform:jakarta.jakartaee-api:9.1.0` | Umbrella API JAR; may bring in more than needed |
| `org.apache.geronimo.specs:geronimo-servlet_2.4_spec` | `jakarta.servlet:jakarta.servlet-api:5.0.0` | Servlet 5.0 = Jakarta EE 9 |
| `org.apache.geronimo.specs:geronimo-ejb_2.1_spec` | `jakarta.ejb:jakarta.ejb-api:4.0.0` | EJB 4.0 = Jakarta EE 9 |
| `org.apache.geronimo.specs:geronimo-jta_1.1_spec` | `jakarta.transaction:jakarta.transaction-api:2.0.1` | JTA 2.0 = Jakarta EE 9 |
| `org.apache.geronimo.specs:geronimo-annotation_1.0_spec` | `jakarta.annotation:jakarta.annotation-api:2.1.1` | Common Annotations 2.1 = Jakarta EE 9 |
| `org.apache.geronimo.specs:geronimo-jpa_3.0_spec` | `jakarta.persistence:jakarta.persistence-api:3.1.0` | JPA 3.1 = Jakarta EE 10 |

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

#### Phase 4: Provide Migration Tooling

Create a migration script or document step-by-step instructions for downstream consumers:

```bash
# Example: Automated import migration using a script
find . -name "*.java" -exec sed -i \
  -e 's/import javax\.servlet\./import jakarta.servlet./g' \
  -e 's/import javax\.transaction\./import jakarta.transaction./g' \
  -e 's/import javax\.annotation\./import jakarta.annotation./g' \
  -e 's/import javax\.ejb\./import jakarta.ejb./g' \
  -e 's/import javax\.persistence\./import jakarta.persistence./g' \
  {} +
```

> **Note**: The above `sed` command is a starting point only. Manual review is necessary — some `javax.*` packages (like `javax.sql.*`, `javax.naming.*`, `javax.xml.*`) remain in the JDK and should NOT be migrated.

### Challenges

1. **Breaking change**: This is a hard breaking change. All existing code that extends Seasar2 web classes (e.g., custom servlets, filters, listeners) must also migrate their imports. There is no binary compatibility path.

2. **Major version bump required**: This warrants a new major version — e.g., **Seasar2 3.0.0** for the Jakarta branch, while the javax branch remains at 2.x. Clear versioning communication is critical.

3. **JDK 8 incompatibility**: Jakarta EE 9+ requires JDK 11+. The Jakarta branch must drop JDK 8 support. This must be clearly documented.

4. **Downstream ecosystem impact**: Projects that depend on Seasar2 will need to:
   - Migrate their own `javax.*` imports
   - Update their Servlet container (or keep running on Tomcat 9 / Jetty 10)
   - Potentially re-test their entire application

5. **Dual maintenance burden**: During the transition period, bug fixes may need to be backported between `branch-javax` (2.x) and `branch-jakarta` (3.x).

6. **Testing infrastructure**: CI must be updated to test against both Tomcat 9 (for javax builds) and Tomcat 10 (for jakarta builds), OR the javax branch can be placed in maintenance mode with only critical fixes.

### Acceptance Criteria

- [ ] Zero `javax.servlet.*`, `javax.transaction.*`, `javax.annotation.*`, `javax.ejb.*`, `javax.persistence.*` imports remaining in source code
- [ ] All Maven POM files updated with Jakarta EE 9+ dependencies
- [ ] All `.dicon` files reviewed and updated for `jakarta.*` references
- [ ] Full regression test suite passes on Tomcat 10 (or embedded Jetty 11 for CI) with JDK 11+
- [ ] Migration script or tool provided for downstream consumers
- [ ] New Maven coordinates or classifier to distinguish `javax` (2.x) vs `jakarta` (3.x) builds
- [ ] [`MIGRATION_GUIDE.md`](MIGRATION_GUIDE.md) updated with `javax → jakarta` migration steps
- [ ] [`CHANGELOG.md`](CHANGELOG.md) updated with the breaking change notice
- [ ] [`README.md`](README.md) updated to document JDK version requirements per branch
- [ ] `CONTRIBUTING.md` updated with branch strategy for javax vs jakarta

### Estimated Effort

| Task | Estimate |
|---|---|
| Audit all `javax.*` references | 1 day |
| Implement import migration (automated + manual review) | 3-5 days |
| Update Maven dependencies | 1 day |
| Update `.dicon` files | 0.5 day |
| Full regression testing (JDK 11, 17) on Tomcat 10+ | 3-5 days |
| Migration tooling for downstream | 2-3 days |
| Documentation updates | 1-2 days |
| **Total** | **~12-18 days** |

### Alternative: Tomcat Migration Tool for Jakarta

As an interim solution for downstream consumers, the [Apache Tomcat Migration Tool for Jakarta EE](https://github.com/apache/tomcat-jakartaee-migration) can automatically transform `javax.*` to `jakarta.*` at the bytecode level in existing WAR/EAR files. This tool works without source code changes, but it is a stopgap — Seasar2 itself should eventually move to the `jakarta.*` namespace natively.

---

## Additional Notes

### When to Address Each Issue

| Issue | Urgency | Recommended Timeline |
|---|---|---|
| Issue 1: `Lookup.defineClass()` | Medium | Within 6-12 months, before JDK 21 becomes the dominant LTS |
| Issue 2: `javax → jakarta` | High | Within 6 months, as Tomcat 9 end-of-life approaches |

### Related Documentation

- [Changelog — Seasar2 Modernization Release](CHANGELOG.md)
- [Migration Guide — Seasar2 Modernization](MIGRATION_GUIDE.md)
- [Seasar2 Development Guide](DEVELOPMENT.md)
- [Contributing Guidelines](CONTRIBUTING.md)

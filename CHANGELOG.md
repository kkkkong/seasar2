# Changelog — Seasar2 Modernized Fork

This is a **modernized fork** of the original [seasarorg/seasar2](https://github.com/seasarorg/seasar2) DI/AOP framework. This release upgrades the framework to build and run on JDK 8, 11, and 17, patches critical security vulnerabilities (XXE and OGNL injection), and resolves JDK 17 module-system compatibility issues. All changes are backward-compatible with existing Seasar2 applications, subject to the OGNL sandbox restrictions documented below.

All notable changes are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Security

- **XXE Protection**: Hardened XML parsers in [`SAXParserFactoryUtil.newInstance()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/SAXParserFactoryUtil.java:47) and [`DocumentBuilderFactoryUtil.newInstance()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/DocumentBuilderFactoryUtil.java:43) by disabling DOCTYPE declarations, external general entities, external parameter entities, and XInclude processing. [`XmlS2ContainerBuilder.createSaxHandlerParser()`](seasar2/s2-framework/src/main/java/org/seasar/framework/container/factory/XmlS2ContainerBuilder.java:185) re-enables DTD for `.dicon` schema validation while keeping external entity resolution blocked.

- **OGNL Sandbox**: New [`OgnlSecurityMemberAccess`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/OgnlSecurityMemberAccess.java:27) implementing `ognl.MemberAccess` to sandbox OGNL expressions and prevent arbitrary code execution. Blocks `java.lang.Runtime.exec()`, `ProcessBuilder`, `System.exit()`, `Class.forName()`, `ClassLoader` access, `AccessibleObject.setAccessible()`, and all reflective `invoke()`/set operations. Integrated via [`OgnlUtil.createContext()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/OgnlUtil.java:149). Comprehensive test coverage in [`OgnlSecurityMemberAccessTest`](seasar2/s2-framework/src/test/java/org/seasar/framework/util/OgnlSecurityMemberAccessTest.java).

### Added

- **JDK 17 / JDK 11 / JDK 8 build compatibility**: `--add-opens` JVM flags in root [`pom.xml`](seasar2/pom.xml:337) — `java.base/java.lang`, `java.base/java.util`, `java.base/java.math`, `java.base/java.net` opened to `ALL-UNNAMED` for Seasar2's reflection-based DI container, AOP proxy generation, and HotdeployBehavior.

- **Graceful Javadoc degradation**: [`EntityMetaReaderImpl`](s2jdbc-gen/src/main/java/org/seasar/extension/jdbc/gen/internal/meta/EntityMetaReaderImpl.java) detects JDK 9+ environments where `com.sun.javadoc` is unavailable and continues entity metadata generation without Javadoc comment extraction (instead of throwing an error).

- **Mirror interfaces for `com.sun.javadoc`**: [`CommentDoclet`](s2jdbc-gen/src/main/java/org/seasar/extension/jdbc/gen/internal/meta/CommentDoclet.java:129) now uses local mirror interfaces (`RootDocMirror`, `ClassDocMirror`, etc.) replacing direct `com.sun.javadoc.*` references, enabling `s2jdbc-gen` to compile without `tools.jar`.

### Changed

- [`S2ContainerFactory`](seasar2/s2-framework/src/main/java/org/seasar/framework/container/factory/S2ContainerFactory.java:133): Defensive null-safety re-initialization guard in all `create()` and `include()` methods prevents NPE when the static initializer fails silently on JDK 17+ due to module-system or classloader isolation. Now throws `IllegalStateException` with a clear diagnostic message.

- [`SAXParserFactoryUtil.setXIncludeAware()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/SAXParserFactoryUtil.java:121): Uses the JDK 9+ public JAXP API (`spf.setXIncludeAware()`) first, falling back to reflection-based access for older parsers on JDK 8.

### Fixed

- **NPE in `S2ContainerFactory.create()`** on JDK 17+: Fixed silent failure when static initialization encounters module-system restrictions. The container now throws `IllegalStateException` with a clear message instead of a cryptic `NullPointerException`.

- **Surefire fork-JVM crash on Java 8**: The root [`pom.xml`](seasar2/pom.xml) previously injected `--add-opens` flags unconditionally into the surefire `argLine`. These JVM options are only supported on JDK 9+, so on Java 8 the forked test JVM failed to start (`Unrecognized option: --add-opens`). The flags were moved into a new auto-activated `jdk9plus` Maven profile (`<activation><jdk>[9,)</jdk></activation>`) so Java 8 builds can run the test suite without them.

- **Java 8 CLDR test parity**: The 5 date-format tests (`DateConversionUtilTest` / `TimestampConversionUtilTest`) failed on Java 8 because the JRE locale provider emits different `SimpleDateFormat` patterns than the CLDR provider used on JDK 9+. The base surefire `argLine` in the root [`pom.xml`](seasar2/pom.xml) now includes `-Djava.locale.providers=CLDR,JRE` so Java 8 uses the same CLDR data as JDK 11+ when available.

- **`maven-antrun-plugin` 3.x incompatibility**: [`s2jdbc-it`](s2jdbc-it/pom.xml) and [`s2jdbc-gen-it`](s2jdbc-gen-it/pom.xml) configure the plugin via the `<tasks>` element, which was removed in antrun 3.0.0. An unversioned plugin declaration resolved to 3.x and failed the build. The plugin is now pinned to version `1.8` in the parent [`pom.xml`](seasar2/pom.xml) plugin management.

- **Missing `database` property in test profiles**: The DB profiles of [`s2jdbc-it`](s2jdbc-it/pom.xml) and [`s2jdbc-gen-it`](s2jdbc-gen-it/pom.xml) (`standard`, `hsqldb`, `h2`, `postgre`, `mysql`, `oracle`, `db2`, `mssql2005`) never declared the `database` Maven property. The antrun `env_ut.txt` setup therefore wrote the literal string `${database}`, and `Env.adjustPath()` could not resolve `xadatasource.dicon` → `xadatasource_<env>.dicon`, causing every integration test to fail with `ResourceNotFoundRuntime`. Each profile now sets its matching `<properties><database>…</database></properties>`.

### Known Issues

- **5 date-format tests** in [`DateConversionUtilTest`](seasar2/s2-framework/src/test/java/org/seasar/framework/util/DateConversionUtilTest.java) and [`TimestampConversionUtilTest`](seasar2/s2-framework/src/test/java/org/seasar/framework/util/TimestampConversionUtilTest.java) may **fail on native JDK 8** due to JRE vs CLDR locale provider differences. These same tests **pass on JDK 11+**. This is a test-only difference — production date-formatting behavior is correct on all supported JDK versions. The surefire `argLine` in the root [`pom.xml`](seasar2/pom.xml) now includes `-Djava.locale.providers=CLDR,JRE`, which restores parity on JDK 8 builds that ship CLDR data (note: some minimal OpenJDK 8 distributions do not bundle CLDR and will still fail — use JDK 11+ in that case). See [`MIGRATION_GUIDE.md`](MIGRATION_GUIDE.md) section 4 for details and workarounds.

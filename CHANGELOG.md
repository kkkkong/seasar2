# Changelog — Seasar2 Modernization Release

All notable changes to the Seasar2 modernization release are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Security

- **XXE Protection**: Hardened XML parsers in [`SAXParserFactoryUtil.newInstance()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/SAXParserFactoryUtil.java:47) and [`DocumentBuilderFactoryUtil.newInstance()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/DocumentBuilderFactoryUtil.java:43) by disabling DOCTYPE declarations, external general entities, external parameter entities, and XInclude processing. [`XmlS2ContainerBuilder.createSaxHandlerParser()`](seasar2/s2-framework/src/main/java/org/seasar/framework/container/factory/XmlS2ContainerBuilder.java:185) re-enables DTD for dicon validation while keeping external entities blocked.

- **OGNL Sandbox**: New [`OgnlSecurityMemberAccess`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/OgnlSecurityMemberAccess.java:27) implementing `ognl.MemberAccess` to sandbox OGNL expressions. Blocks `java.lang.Runtime.exec()`, `ProcessBuilder`, `System.exit()`, `Class.forName()`, `ClassLoader`, `AccessibleObject.setAccessible()`, and all reflective invoke/set operations. Integrated via [`OgnlUtil.createContext()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/OgnlUtil.java:149). Test coverage in [`OgnlSecurityMemberAccessTest`](seasar2/s2-framework/src/test/java/org/seasar/framework/util/OgnlSecurityMemberAccessTest.java).

### Added

- JDK 17 / JDK 11 / JDK 8 build compatibility via `--add-opens` JVM flags in root [`pom.xml`](seasar2/pom.xml:337): `java.base/java.lang`, `java.base/java.util`, `java.base/java.math`, `java.base/java.net` opened to `ALL-UNNAMED` for reflection-based DI/AOP.

- Graceful degradation in [`EntityMetaReaderImpl`](s2jdbc-gen/src/main/java/org/seasar/extension/jdbc/gen/internal/meta/EntityMetaReaderImpl.java) for JDK 9+ environments where `com.sun.javadoc` is unavailable — entity metadata generation continues without Javadoc comment extraction.

- Mirror interfaces in [`CommentDoclet`](s2jdbc-gen/src/main/java/org/seasar/extension/jdbc/gen/internal/meta/CommentDoclet.java:129) replacing direct `com.sun.javadoc.*` references to enable compilation without `tools.jar`.

### Changed

- [`S2ContainerFactory`](seasar2/s2-framework/src/main/java/org/seasar/framework/container/factory/S2ContainerFactory.java:133): Defensive null-safety re-initialization guard in all `create()` and `include()` methods to prevent NPE when static initializer fails on JDK 17+ due to module system or classloader isolation.

- [`SAXParserFactoryUtil.setXIncludeAware()`](seasar2/s2-framework/src/main/java/org/seasar/framework/util/SAXParserFactoryUtil.java:121): Uses JDK 9+ public JAXP API (`spf.setXIncludeAware()`) first, falls back to reflection for older parsers.

### Fixed

- NPE in `S2ContainerFactory.create()` methods when static initialization fails silently on JDK 17+ (now throws `IllegalStateException` with clear message).

### Known Issues

- 5 date-format tests in `DateConversionUtilTest` and `TimestampConversionUtilTest` fail on native JDK 8 due to JRE vs CLDR locale provider differences. These pass on JDK 11+. Not a functional regression; production behavior is consistent on JDK 11+.

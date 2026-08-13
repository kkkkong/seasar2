# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Build & Test Commands

- Build: `mvn clean install` (run from module directory, NOT root; root pom is aggregator-only with `<packaging>pom</packaging>`)
- Single test: `mvn test -Dtest=ClassName` (e.g., `-Dtest=UserDaoTest`)
- Test with database: `mvn test -P<profile>` (profiles: h2, postgre, mysql, oracle, db2, mssql2005)
- Skip tests: `mvn clean install -DskipTests`
- Javadoc: `mvn javadoc:javadoc`
- Build order: `seasar2/` first → `s2-tiger/` → `s2jdbc-gen/` (due to inter-module dependencies)

## Critical Architecture

- **Three-tier module hierarchy**: `s2-framework` (core DI/AOP, Java 1.8) → `s2-extension` (JDBC/Tx/DBCP) → `s2-tiger` (annotations, Java 5). `s2-tiger/pom.xml` compiles with source=1.5 but depends on s2-extension.
- **Container behavior customization**: `S2ContainerBehavior.Provider` (set via `s2container.dicon`) controls `getComponent`, `getComponentDef`, `hasComponentDef`, `injectDependency` lookups. Three deploy modes: HotdeployBehavior, WarmdeployBehavior, CooldeployBehavior — checked via `SmartDeployUtil`.
- **`.dicon` files are XML-based IoC configs**: Auto-discovered by convention. Root dicon resolves via `S2FrameworkTestCase.resolveRootDicon()` which looks for `getRootDicon<TestName>` method.
- **Test jars are packaged as dependencies**: `s2-framework` and `s2-extension` both produce `test-jar` via maven-jar-plugin used by downstream modules.

## Testing Conventions

- `S2FrameworkTestCase` (JUnit 3 style — no `@Test`, method must be `public void testXxx()`): for core DI testing. Sets up `S2Container` automatically. Calls `include("path/to.dicon")` in `setUp()` to add child containers.
- `S2TestCase` (extends S2FrameworkTestCase): for DB/tx tests. Adds datasource at `j2ee.dataSource`.
- Test dicon naming: By convention, if test class is `com.example.FooTest`, put dicon at `com/example/FooTest.dicon` and call `include(getClass().getName().replace('.','/') + ".dicon")` in setUp.
- No `@Test` annotation — this is JUnit 3.8.2 style (s2-framework/s2-extension). Only s2-tiger uses JUnit 4.4.
- Maven Surefire uses `-noverify -Duser.timezone=Asia/Tokyo -Duser.language=ja -Duser.country=JP -Dfile.encoding=UTF-8` — locale-sensitive tests expect Japanese locale.

## Code Style

- 4-space indentation (no tabs), UTF-8 encoding everywhere
- No checkstyle/editorconfig files exist — conventions are documented only in CONTRIBUTING.md
- Java 1.4 compatibility required for s2-framework/s2-extension (no generics, no annotations, no for-each, no autoboxing)
- NamingConvention interface (`org.seasar.framework.convention.NamingConvention`) controls component name↔class mapping, impl suffix detection, package layout
- Logging: Use `org.seasar.framework.log.Logger` (custom wrapper), NOT commons-logging directly. Pattern: `private static final Logger logger = Logger.getLogger(ClassName.class);`
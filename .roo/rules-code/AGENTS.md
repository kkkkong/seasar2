# Coding Rules (Non-Obvious)

- The core modules (s2-framework, s2-extension) target Java 1.4. NO generics, no `@Override` on interface methods, no annotations, no for-each loops, no autoboxing. Only s2-tiger can use Java 5+ features.
- Use `org.seasar.framework.log.Logger` (not commons-logging, not log4j directly): `Logger.getLogger(ClassName.class)`. Supports message codes like `"DSSR0108"` for i18n.
- Resource loading: Use `org.seasar.framework.util.ResourceUtil` for classpath resources. Use `org.seasar.framework.util.StringUtil` instead of Apache Commons or Guava equivalents.
- System property `org.seasar.framework.container.factory.config` overrides the default config dicon path (`s2container.dicon`).
- Container hierarchy: Use `include(S2Container)` to add child containers. Circular includes throw `CircularIncludeRuntimeException`. Use `registerDescendant()` for descendant tracking.
- `S2ContainerBehavior.Provider` is a global singleton — changes affect ALL containers. Set via `S2ContainerBehavior.setProvider()`.
- HOT deploy uses `HotdeployClassLoader` which replaces the thread context ClassLoader. Call `HotdeployUtil.start()` / `HotdeployUtil.stop()` around test code that uses it.
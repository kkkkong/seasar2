# Documentation Rules (Non-Obvious)

- The "docs" folder does not exist. Primary documentation: README.md, CONTRIBUTING.md, DEVELOPMENT.md, and Javadoc in source.
- `s2-tiger/` is NOT a submodule of `seasar2/` — it has its own `pom.xml` with `<parent>` pointing to `../seasar2`. Same for `s2jdbc-gen/` and `s2jdbc-gen-it/`.
- `s2-tiger/` compiles at Java 1.5 while parent `seasar2/` compiles at Java 1.8. This is intentional for backward compatibility.
- `ci-and-contrib.patch` at repository root is a git patch file — contains pre-applied CI and contribution setup changes.
- Site documentation in `seasar2/src/site/resources/` is HTML-based (not markdown), with separate `en/`, `ja/`, `zh/` locale directories.
- `s2-tiger/src/main/resources/` contains `.dicon` files that are framework configuration (not user-facing docs). Critical ones: `jpa-support.dicon`, `s2jdbc-internal.dicon`, `ejb3tx.dicon`.
- `CODE_OF_CONDUCT.md` exists but is a placeholder (empty/standard template).
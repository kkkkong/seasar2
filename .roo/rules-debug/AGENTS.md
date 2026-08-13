# Debug Rules (Non-Obvious)

- `SmartDeployUtil.getDeployMode(container)` returns deploy mode string ("Hot Deploy"/"Cool Deploy"/"Warm Deploy"/"Normal Mode") — use to check why components aren't being found.
- When duplicate component keys exist, `S2ContainerImpl.createTooManyRegistration()` silently wraps them in `TooManyRegistrationComponentDefImpl`, which throws at access time (not registration time).
- `S2FrameworkTestCase` sets `env_ut.txt` with value `ut` before each test. Environment-dependent dicon includes (e.g., `j2ee_ut.dicon`) depend on this.
- JUnit 3 style test methods: `public void testXxx()` — methods without `test` prefix are NOT executed by JUnit 3 runners. This is critical since there's no `@Test`.
- Maven Surefire argLine includes `-noverify` and Japanese locale. Tests that depend on `MessageResourceBundle` or `ResourceBundle` will fail if locale differs.
- `HotdeployBehavior` caches ClassLoader via `keep` flag. If tests trigger HOT deploy, call `HotdeployUtil.clearHotdeploy()` between tests to avoid stale state.
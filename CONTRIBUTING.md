# Contributing to Seasar2

Thank you for your interest in contributing to Seasar2! This document provides guidelines and instructions for contributing to the project.

---

## Code of Conduct

We are committed to providing a welcoming and inclusive environment for all contributors. Please be respectful and constructive in all interactions with other community members.

---

## Getting Started

### Prerequisites

Before you start contributing, ensure you have:

- **Java Development Kit (JDK)** - Version 1.4+ (JDK 6+ recommended)
- **Maven 3.0+** - Build and dependency management
- **Git** - Version control
- **IDE** - Eclipse, IntelliJ IDEA, or your preferred Java IDE

### Fork and Clone the Repository

```bash
# 1. Fork the repository on GitHub
# Visit: https://github.com/kkkkong/seasar2

# 2. Clone your fork locally
git clone https://github.com/kkkkong/seasar2.git
cd seasar2

# 3. Add upstream remote to track original repository
git remote add upstream https://github.com/kkkkong/seasar2.git

# 4. Verify remotes
git remote -v
# origin    https://github.com/<your-username>/seasar2.git (fetch)
# origin    https://github.com/<your-username>/seasar2.git (push)
# upstream  https://github.com/kkkkong/seasar2.git (fetch)
# upstream  https://github.com/kkkkong/seasar2.git (push)
```

### Build the Project Locally

```bash
# Build core framework
cd seasar2
mvn clean install

# Build extensions
cd ../s2-tiger
mvn clean install

# Build JDBC generator
cd ../s2jdbc-gen
mvn clean install

# Verify tests pass
mvn test
```

---

## Types of Contributions

### 1. Bug Reports

Found a bug? We'd love to hear about it!

**Before Submitting:**
- Check existing [https://github.com/kkkkong/seasar2/issues](https://github.com/kkkkong/seasar2/issues) to avoid duplicates
- Test with the latest version
- Provide a minimal reproducible example

**When Submitting:**
```markdown
**Title:** Brief description of the bug

**Environment:**
- Seasar2 Version: 2.4.49-SNAPSHOT
- Java Version: 1.8.0_271
- Database: PostgreSQL 12.4
- OS: Ubuntu 20.04

**Steps to Reproduce:**
1. Create a component with @Tx annotation
2. Call method that should trigger transaction
3. Observe unexpected behavior

**Expected Behavior:**
Transaction should be committed successfully

**Actual Behavior:**
Transaction is rolled back with error

**Code Sample:**
```java
@Component
public class UserService {
  @Tx
  public void saveUser(User user) {
    // Implementation
  }
}
```

**Additional Context:**
Include stack traces, logs, or screenshots if applicable
```

### 2. Feature Requests

Have an idea for a new feature?

**Before Submitting:**
- Check if the feature is already discussed in issues
- Explain the use case and why it's needed
- Consider alternative approaches

**When Submitting:**
```markdown
**Title:** Brief description of requested feature

**Use Case:**
Describe the problem this solves

**Proposed Solution:**
Explain how it should work

**Alternative Approaches:**
Any alternative solutions you've considered

**Additional Context:**
Links to related issues, documentation, or external resources
```

### 3. Documentation Improvements

Documentation can always be improved!

- Fix typos and grammatical errors
- Improve clarity and examples
- Add missing documentation
- Update outdated information

Submit documentation changes directly as pull requests.

### 4. Code Contributions

Contributing code is the most direct way to improve Seasar2.

---

## Development Workflow

### 1. Create a Feature Branch

```bash
# Update your local master from upstream
git fetch upstream
git checkout master
git merge upstream/master

# Create a descriptive branch name
git checkout -b feature/your-feature-name
# or for bug fixes:
git checkout -b bugfix/issue-description
```

**Branch Naming Conventions:**
- `feature/description` - New features
- `bugfix/description` - Bug fixes
- `docs/description` - Documentation updates
- `refactor/description` - Code refactoring
- `test/description` - Test improvements

### 2. Make Your Changes

Follow the coding standards and best practices outlined below.

### 3. Commit Your Work

```bash
# Stage your changes
git add <files>

# Create meaningful commits
git commit -m "feat: add feature description

Detailed explanation of what was changed and why.
This is a longer description that explains the context
and rationale for the change.

Closes #123 (if fixing an issue)"
```

**Commit Message Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat` - A new feature
- `fix` - A bug fix
- `docs` - Documentation changes
- `style` - Code style changes (formatting, semicolons, etc.)
- `refactor` - Code refactoring without feature changes
- `test` - Adding or updating tests
- `chore` - Build, dependency, or configuration changes

**Example:**
```
feat(s2jdbc): support batch update operations

- Add BatchStatementFactory for efficient batch processing
- Implement batch parameter binding
- Add unit tests with Derby and H2 databases

Closes #456
```

### 4. Write or Update Tests

**Test Requirements:**
- Write tests for new features
- Update tests for modified code
- Ensure all existing tests pass
- Aim for >80% code coverage

**Test Location:**
```
seasar2/s2-framework/src/test/java/org/seasar/framework/...
seasar2/s2-extension/src/test/java/org/seasar/extension/...
```

**Test Example:**
```java
public class UserDaoTest extends S2TestCase {
  
  private UserDao userDao;
  private Sql sql;
  
  protected void setUp() throws Exception {
    super.setUp();
    // Container automatically injects
  }
  
  public void testFindById() throws Exception {
    // Arrange
    User expected = new User(1, "John Doe");
    
    // Act
    User actual = userDao.findById(1);
    
    // Assert
    assertNotNull(actual);
    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getName(), actual.getName());
  }
  
  public void testFindByIdNotFound() throws Exception {
    User user = userDao.findById(999);
    assertNull(user);
  }
}
```

### 5. Run Tests Locally

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserDaoTest

# Run with specific database
mvn test -Pmysql

# Skip tests during build
mvn clean install -DskipTests

# Build and run tests
mvn clean verify
```

### 6. Push to Your Fork

```bash
git push origin feature/your-feature-name
```

### 7. Create a Pull Request

**On GitHub:**

1. Go to your fork repository
2. Click "New Pull Request" or the "Compare & pull request" button
3. Select base repository: `seasarorg/seasar2`, base branch: `master`
4. Select head repository: your fork, compare branch: your feature branch
5. Fill in the pull request template:

```markdown
## Description
Brief description of what this PR does.

## Type of Change
- [ ] Bug fix (non-breaking change fixing an issue)
- [ ] New feature (non-breaking change adding functionality)
- [ ] Breaking change (fix or feature causing existing functionality to change)
- [ ] Documentation update

## Related Issues
Closes #123

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed
- [ ] All tests passing

## Database Testing
- [ ] HSQLDB (default)
- [ ] H2
- [ ] MySQL
- [ ] PostgreSQL
- [ ] Other (specify): ___

## Checklist
- [ ] Code follows style guidelines
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests demonstrate feature works
- [ ] Tests pass locally with my changes

## Screenshots/Examples (if applicable)
Include screenshots, logs, or code examples

## Reviewers
@reviewer1 @reviewer2
```

---

## Coding Standards

### Java Code Style

**Naming Conventions:**
```java
// Classes - PascalCase
public class UserService { }

// Methods - camelCase
public void findUserById(int id) { }

// Constants - UPPER_SNAKE_CASE
private static final int DEFAULT_TIMEOUT = 3000;

// Variables - camelCase
private String userName;
private List<User> userList;
```

**Formatting:**
```java
// Indentation: 4 spaces (not tabs)
public class Example {
    private String field;
    
    public Example(String field) {
        this.field = field;
    }
    
    // Method formatting
    public void processData(String input, int count) {
        if (input != null) {
            for (int i = 0; i < count; i++) {
                System.out.println(input);
            }
        }
    }
}
```

**Documentation:**
```java
/**
 * Finds a user by their unique identifier.
 *
 * @param id the user ID to search for
 * @return the User object if found, null otherwise
 * @throws IllegalArgumentException if id is negative
 * @throws DataAccessException if database error occurs
 */
public User findById(int id) throws DataAccessException {
    if (id < 0) {
        throw new IllegalArgumentException("ID must be positive");
    }
    // Implementation
}
```

### Project Structure

Respect the existing module structure:

```
seasar2/
├── s2-framework/           # Core functionality - fundamental
├── s2-extension/           # Extensions - builds on framework
├── s2-tiger/              # Advanced features - builds on framework & extension
└── s2jdbc-gen/            # Code generation tools
```

**Placement Guidelines:**
- Core DI/AOP logic → `s2-framework`
- Database, transaction, testing utilities → `s2-extension`
- Annotations, advanced features → `s2-tiger`
- New modules → discuss in issue first

### Important Practices

**1. Backward Compatibility**
- Avoid breaking changes to public APIs
- If necessary, mark old methods as `@Deprecated`
- Provide migration path in documentation

```java
/**
 * @deprecated Use {@link #findUserById(int)} instead
 */
@Deprecated
public User getUserById(int id) {
    return findUserById(id);
}
```

**2. Error Handling**
```java
try {
    connection = dataSource.getConnection();
    // Use connection
} catch (SQLException e) {
    throw new DataAccessException("Failed to get connection", e);
} finally {
    if (connection != null) {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.warn("Error closing connection", e);
        }
    }
}
```

**3. Logging**
```java
private static final Logger logger = LoggerFactory.getLogger(UserService.class);

public void saveUser(User user) {
    logger.debug("Saving user: {}", user.getId());
    try {
        userDao.insert(user);
        logger.info("User saved successfully: {}", user.getId());
    } catch (Exception e) {
        logger.error("Failed to save user: " + user.getId(), e);
        throw e;
    }
}
```

**4. Resource Management**
```java
// Use try-with-resources when possible
try (Connection conn = dataSource.getConnection();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery(sql)) {
    // Process results
}
```

---

## Testing Guidelines

### Unit Tests

```java
public class CalculatorTest extends TestCase {
    
    private Calculator calculator;
    
    protected void setUp() throws Exception {
        super.setUp();
        calculator = new Calculator();
    }
    
    public void testAdd() {
        assertEquals(5, calculator.add(2, 3));
    }
    
    public void testAddNegatives() {
        assertEquals(-1, calculator.add(-2, 1));
    }
    
    public void testDivideByZero() {
        try {
            calculator.divide(10, 0);
            fail("Should throw ArithmeticException");
        } catch (ArithmeticException expected) {
            // Expected
        }
    }
}
```

### Integration Tests

```java
public class UserServiceIntegrationTest extends S2TestCase {
    
    private UserService userService;
    
    public void testSaveAndFindUser() throws Exception {
        // Arrange
        User user = new User();
        user.setName("John Doe");
        
        // Act
        userService.saveUser(user);
        User found = userService.findById(user.getId());
        
        // Assert
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
    }
}
```

### Test Coverage

- Aim for >80% code coverage for new code
- Use `mvn jacoco:report` to generate coverage reports
- Focus on critical paths and edge cases

---

## Database Testing

### Testing with Multiple Databases

```bash
# HSQLDB (in-memory, default)
mvn test

# PostgreSQL
mvn test -Ppostgre

# MySQL
mvn test -Pmysql

# H2
mvn test -Ph2

# Oracle
mvn test -Poracle
```

**Database Configuration:**
- Test configurations in `src/test/resources/`
- Database setup scripts in `src/test/sql/`
- Dataset files in `src/test/data/`

---

## Documentation

### README Changes

If your contribution affects how users interact with Seasar2:
1. Update the main [README.md](README.md)
2. Provide code examples
3. Explain configuration changes

### Javadoc

All public classes and methods must have Javadoc:

```java
/**
 * Manages user persistence operations.
 * 
 * <p>This service handles all CRUD operations for User entities
 * and manages transaction boundaries automatically.</p>
 * 
 * @author Your Name
 * @version 2.4.49
 * @see User
 * @see UserDao
 */
public class UserService {
    
    /**
     * Saves a user to the database.
     *
     * @param user the user to save, must not be null
     * @return the saved user with generated ID
     * @throws IllegalArgumentException if user is null
     * @throws DataAccessException if save operation fails
     */
    @Tx
    public User save(User user) {
        // Implementation
    }
}
```

### Generate Javadoc

```bash
mvn javadoc:javadoc
# Output in: target/site/apidocs/
```

---

## Pull Request Review Process

### What Reviewers Look For

1. **Code Quality**
   - Follows coding standards
   - Is it maintainable?
   - Are there potential bugs?

2. **Tests**
   - Are tests comprehensive?
   - Do they pass locally?
   - Is coverage adequate?

3. **Documentation**
   - Is code well-documented?
   - Are comments helpful?
   - Is user documentation updated?

4. **Backward Compatibility**
   - Are existing APIs preserved?
   - Are deprecations handled properly?

5. **Performance**
   - Are there any performance regressions?
   - Has the solution been optimized?

### Responding to Feedback

- Be open to constructive criticism
- Ask for clarification if feedback is unclear
- Make requested changes and push updates
- Acknowledge and thank reviewers

```bash
# Make changes based on feedback
git add <files>
git commit -m "refactor: address code review feedback

- Rename method for clarity
- Add additional null checks
- Remove unused imports"

git push origin feature/your-feature-name
```

---

## Common Issues and Solutions

### Build Fails with Java Compilation Error

```bash
# Check Java version
java -version

# Should be 1.4+ (1.6+ recommended)
# Update JAVA_HOME if needed

export JAVA_HOME=/path/to/jdk
mvn clean install
```

### Test Fails with Database Connection Error

```bash
# For PostgreSQL tests
# Ensure PostgreSQL is running on localhost:5432
# Create test database: createdb seasar2_test

# For MySQL tests  
# Ensure MySQL is running on localhost:3306
# Create test database: create database seasar2_test;

# Run tests with specific database
mvn test -Ppostgre
```

### "Cannot find symbol" compilation error

```bash
# Clear Maven cache and rebuild
mvn clean install

# Or force update of dependencies
mvn dependency:purge-local-repository
mvn clean install
```

### Test hangs or times out

```bash
# Increase timeout
mvn test -DtimeoutInMinutes=10

# Run single test class
mvn test -Dtest=SpecificTest
```

---

## Resources and Communication

### Getting Help

- **GitHub Issues** - Ask questions, report bugs, request features
- **Mailing Lists**
  - [Seasar-user](https://www.seasar.org/mailman/listinfo/seasar-user) - User discussions
  - [Seasar-dev](https://www.seasar.org/mailman/listinfo/seasar-dev) - Development discussions
  - [Seasar-user-en](https://www.seasar.org/mailman/listinfo/seasar-user-en) - English-language support

### Useful Links

- [Official Website](http://s2container.seasar.org/)
- [Seasar Foundation](http://www.seasarfoundation.org/)
- [Upstream Repository](https://github.com/seasarorg/seasar2)
- [Issue Tracker](https://github.com/seasarorg/seasar2/issues)

---

## License

By contributing to Seasar2, you agree that your contributions will be licensed under the Apache License 2.0. See [LICENSE.txt](seasar2/LICENSE.txt) for details.

---

## Recognition

Contributors are recognized in:
- Project [README.md](README.md)
- Git commit history
- Release notes (for significant contributions)

Thank you for contributing to Seasar2! Your efforts help make the project better for everyone. 🙏

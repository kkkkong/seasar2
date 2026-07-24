/*
 * Copyright 2004-2015 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.extension.jdbc.gen.mock.sql;

import java.sql.*;

/**
 * @author taedium
 * 
 */
public class GenMockDatabaseMetaData implements DatabaseMetaData {

    public boolean allProceduresAreCallable() throws SQLException {

        return false;
    }

    public boolean allTablesAreSelectable() throws SQLException {

        return false;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {

        return false;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {

        return false;
    }

    public boolean deletesAreDetected(int type) throws SQLException {

        return false;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {

        return false;
    }

    public ResultSet getAttributes(String catalog, String schemaPattern,
            String typeNamePattern, String attributeNamePattern)
            throws SQLException {

        return null;
    }

    public ResultSet getBestRowIdentifier(String catalog, String schema,
            String table, int scope, boolean nullable) throws SQLException {

        return null;
    }

    public ResultSet getCatalogs() throws SQLException {

        return null;
    }

    public String getCatalogSeparator() throws SQLException {

        return null;
    }

    public String getCatalogTerm() throws SQLException {

        return null;
    }

    public ResultSet getColumnPrivileges(String catalog, String schema,
            String table, String columnNamePattern) throws SQLException {

        return null;
    }

    public ResultSet getColumns(String catalog, String schemaPattern,
            String tableNamePattern, String columnNamePattern)
            throws SQLException {

        return null;
    }

    public Connection getConnection() throws SQLException {

        return null;
    }

    public ResultSet getCrossReference(String primaryCatalog,
            String primarySchema, String primaryTable, String foreignCatalog,
            String foreignSchema, String foreignTable) throws SQLException {

        return null;
    }

    public int getDatabaseMajorVersion() throws SQLException {

        return 0;
    }

    public int getDatabaseMinorVersion() throws SQLException {

        return 0;
    }

    public String getDatabaseProductName() throws SQLException {

        return "Mock Database";
    }

    public String getDatabaseProductVersion() throws SQLException {

        return null;
    }

    public int getDefaultTransactionIsolation() throws SQLException {

        return 0;
    }

    public int getDriverMajorVersion() {

        return 0;
    }

    public int getDriverMinorVersion() {

        return 0;
    }

    public String getDriverName() throws SQLException {

        return null;
    }

    public String getDriverVersion() throws SQLException {

        return null;
    }

    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {

        return null;
    }

    public String getExtraNameCharacters() throws SQLException {

        return null;
    }

    public String getIdentifierQuoteString() throws SQLException {

        return null;
    }

    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {

        return null;
    }

    public ResultSet getIndexInfo(String catalog, String schema, String table,
            boolean unique, boolean approximate) throws SQLException {

        return null;
    }

    public int getJDBCMajorVersion() throws SQLException {

        return 0;
    }

    public int getJDBCMinorVersion() throws SQLException {

        return 0;
    }

    public int getMaxBinaryLiteralLength() throws SQLException {

        return 0;
    }

    public int getMaxCatalogNameLength() throws SQLException {

        return 0;
    }

    public int getMaxCharLiteralLength() throws SQLException {

        return 0;
    }

    public int getMaxColumnNameLength() throws SQLException {

        return 0;
    }

    public int getMaxColumnsInGroupBy() throws SQLException {

        return 0;
    }

    public int getMaxColumnsInIndex() throws SQLException {

        return 0;
    }

    public int getMaxColumnsInOrderBy() throws SQLException {

        return 0;
    }

    public int getMaxColumnsInSelect() throws SQLException {

        return 0;
    }

    public int getMaxColumnsInTable() throws SQLException {

        return 0;
    }

    public int getMaxConnections() throws SQLException {

        return 0;
    }

    public int getMaxCursorNameLength() throws SQLException {

        return 0;
    }

    public int getMaxIndexLength() throws SQLException {

        return 0;
    }

    public int getMaxProcedureNameLength() throws SQLException {

        return 0;
    }

    public int getMaxRowSize() throws SQLException {

        return 0;
    }

    public int getMaxSchemaNameLength() throws SQLException {

        return 0;
    }

    public int getMaxStatementLength() throws SQLException {

        return 0;
    }

    public int getMaxStatements() throws SQLException {

        return 0;
    }

    public int getMaxTableNameLength() throws SQLException {

        return 0;
    }

    public int getMaxTablesInSelect() throws SQLException {

        return 0;
    }

    public int getMaxUserNameLength() throws SQLException {

        return 0;
    }

    public String getNumericFunctions() throws SQLException {

        return null;
    }

    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {

        return null;
    }

    public ResultSet getProcedureColumns(String catalog, String schemaPattern,
            String procedureNamePattern, String columnNamePattern)
            throws SQLException {

        return null;
    }

    public ResultSet getProcedures(String catalog, String schemaPattern,
            String procedureNamePattern) throws SQLException {

        return null;
    }

    public String getProcedureTerm() throws SQLException {

        return null;
    }

    public int getResultSetHoldability() throws SQLException {

        return 0;
    }

    public ResultSet getSchemas() throws SQLException {

        return null;
    }

    public String getSchemaTerm() throws SQLException {

        return null;
    }

    public String getSearchStringEscape() throws SQLException {

        return null;
    }

    public String getSQLKeywords() throws SQLException {

        return null;
    }

    public int getSQLStateType() throws SQLException {

        return 0;
    }

    public String getStringFunctions() throws SQLException {

        return null;
    }

    public ResultSet getSuperTables(String catalog, String schemaPattern,
            String tableNamePattern) throws SQLException {

        return null;
    }

    public ResultSet getSuperTypes(String catalog, String schemaPattern,
            String typeNamePattern) throws SQLException {

        return null;
    }

    public String getSystemFunctions() throws SQLException {

        return null;
    }

    public ResultSet getTablePrivileges(String catalog, String schemaPattern,
            String tableNamePattern) throws SQLException {

        return null;
    }

    public ResultSet getTables(String catalog, String schemaPattern,
            String tableNamePattern, String[] types) throws SQLException {

        return null;
    }

    public ResultSet getTableTypes() throws SQLException {

        return null;
    }

    public String getTimeDateFunctions() throws SQLException {

        return null;
    }

    public ResultSet getTypeInfo() throws SQLException {

        return null;
    }

    public ResultSet getUDTs(String catalog, String schemaPattern,
            String typeNamePattern, int[] types) throws SQLException {

        return null;
    }

    public String getURL() throws SQLException {

        return null;
    }

    public String getUserName() throws SQLException {

        return null;
    }

    public ResultSet getVersionColumns(String catalog, String schema,
            String table) throws SQLException {

        return null;
    }

    public boolean insertsAreDetected(int type) throws SQLException {

        return false;
    }

    public boolean isCatalogAtStart() throws SQLException {

        return false;
    }

    public boolean isReadOnly() throws SQLException {

        return false;
    }

    public boolean locatorsUpdateCopy() throws SQLException {

        return false;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {

        return false;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {

        return false;
    }

    public boolean nullsAreSortedAtStart() throws SQLException {

        return false;
    }

    public boolean nullsAreSortedHigh() throws SQLException {

        return false;
    }

    public boolean nullsAreSortedLow() throws SQLException {

        return false;
    }

    public boolean othersDeletesAreVisible(int type) throws SQLException {

        return false;
    }

    public boolean othersInsertsAreVisible(int type) throws SQLException {

        return false;
    }

    public boolean othersUpdatesAreVisible(int type) throws SQLException {

        return false;
    }

    public boolean ownDeletesAreVisible(int type) throws SQLException {

        return false;
    }

    public boolean ownInsertsAreVisible(int type) throws SQLException {

        return false;
    }

    public boolean ownUpdatesAreVisible(int type) throws SQLException {

        return false;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {

        return false;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {

        return false;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {

        return false;
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {

        return false;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {

        return false;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {

        return false;
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {

        return false;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {

        return false;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {

        return false;
    }

    public boolean supportsANSI92FullSQL() throws SQLException {

        return false;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {

        return false;
    }

    public boolean supportsBatchUpdates() throws SQLException {

        return false;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {

        return false;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {

        return false;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {

        return false;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {

        return false;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {

        return false;
    }

    public boolean supportsColumnAliasing() throws SQLException {

        return false;
    }

    public boolean supportsConvert() throws SQLException {

        return false;
    }

    public boolean supportsConvert(int fromType, int toType)
            throws SQLException {

        return false;
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {

        return false;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {

        return false;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions()
            throws SQLException {

        return false;
    }

    public boolean supportsDataManipulationTransactionsOnly()
            throws SQLException {

        return false;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {

        return false;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {

        return false;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {

        return false;
    }

    public boolean supportsFullOuterJoins() throws SQLException {

        return false;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {

        return false;
    }

    public boolean supportsGroupBy() throws SQLException {

        return false;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {

        return false;
    }

    public boolean supportsGroupByUnrelated() throws SQLException {

        return false;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {

        return false;
    }

    public boolean supportsLikeEscapeClause() throws SQLException {

        return false;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {

        return false;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {

        return false;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {

        return false;
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {

        return false;
    }

    public boolean supportsMultipleOpenResults() throws SQLException {

        return false;
    }

    public boolean supportsMultipleResultSets() throws SQLException {

        return false;
    }

    public boolean supportsMultipleTransactions() throws SQLException {

        return false;
    }

    public boolean supportsNamedParameters() throws SQLException {

        return false;
    }

    public boolean supportsNonNullableColumns() throws SQLException {

        return false;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {

        return false;
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {

        return false;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {

        return false;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {

        return false;
    }

    public boolean supportsOrderByUnrelated() throws SQLException {

        return false;
    }

    public boolean supportsOuterJoins() throws SQLException {

        return false;
    }

    public boolean supportsPositionedDelete() throws SQLException {

        return false;
    }

    public boolean supportsPositionedUpdate() throws SQLException {

        return false;
    }

    public boolean supportsResultSetConcurrency(int type, int concurrency)
            throws SQLException {

        return false;
    }

    public boolean supportsResultSetHoldability(int holdability)
            throws SQLException {

        return false;
    }

    public boolean supportsResultSetType(int type) throws SQLException {

        return false;
    }

    public boolean supportsSavepoints() throws SQLException {

        return false;
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {

        return false;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {

        return false;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {

        return false;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {

        return false;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {

        return false;
    }

    public boolean supportsSelectForUpdate() throws SQLException {

        return false;
    }

    public boolean supportsStatementPooling() throws SQLException {

        return false;
    }

    /**
     * Indicates whether or not this data source supports the SQL <code>ROWID</code> type,
     * and if so  the lifetime for which a <code>RowId</code> object remains valid.
     * <p>
     * The returned int values have the following relationship:
     * <pre>{@code
     *     ROWID_UNSUPPORTED < ROWID_VALID_OTHER < ROWID_VALID_TRANSACTION
     *         < ROWID_VALID_SESSION < ROWID_VALID_FOREVER
     * }</pre>
     * so conditional logic such as
     * <pre>{@code
     *     if (metadata.getRowIdLifetime() > DatabaseMetaData.ROWID_VALID_TRANSACTION)
     * }</pre>
     * can be used. Valid Forever means valid across all Sessions, and valid for
     * a Session means valid across all its contained Transactions.
     *
     * @return the status indicating the lifetime of a <code>RowId</code>
     * @throws SQLException if a database access error occurs
     * @since 1.6
     */
    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return null;
    }

    /**
     * Retrieves the schema names available in this database.  The results
     * are ordered by <code>TABLE_CATALOG</code> and
     * <code>TABLE_SCHEM</code>.
     *
     * <P>The schema columns are:
     * <OL>
     * <LI><B>TABLE_SCHEM</B> String {@code =>} schema name
     * <LI><B>TABLE_CATALOG</B> String {@code =>} catalog name (may be <code>null</code>)
     * </OL>
     *
     * @param catalog       a catalog name; must match the catalog name as it is stored
     *                      in the database;"" retrieves those without a catalog; null means catalog
     *                      name should not be used to narrow down the search.
     * @param schemaPattern a schema name; must match the schema name as it is
     *                      stored in the database; null means
     *                      schema name should not be used to narrow down the search.
     * @return a <code>ResultSet</code> object in which each row is a
     * schema description
     * @throws SQLException if a database access error occurs
     * @see #getSearchStringEscape
     * @since 1.6
     */
    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        return null;
    }

    /**
     * Retrieves whether this database supports invoking user-defined or vendor functions
     * using the stored procedure escape syntax.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     * @throws SQLException if a database access error occurs
     * @since 1.6
     */
    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return false;
    }

    /**
     * Retrieves whether a <code>SQLException</code> while autoCommit is <code>true</code> indicates
     * that all open ResultSets are closed, even ones that are holdable.  When a <code>SQLException</code> occurs while
     * autocommit is <code>true</code>, it is vendor specific whether the JDBC driver responds with a commit operation, a
     * rollback operation, or by doing neither a commit nor a rollback.  A potential result of this difference
     * is in whether or not holdable ResultSets are closed.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     * @throws SQLException if a database access error occurs
     * @since 1.6
     */
    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false;
    }

    /**
     * Retrieves a list of the client info properties
     * that the driver supports.  The result set contains the following columns
     *
     * <ol>
     * <li><b>NAME</b> String{@code =>} The name of the client info property<br>
     * <li><b>MAX_LEN</b> int{@code =>} The maximum length of the value for the property<br>
     * <li><b>DEFAULT_VALUE</b> String{@code =>} The default value of the property<br>
     * <li><b>DESCRIPTION</b> String{@code =>} A description of the property.  This will typically
     *                                              contain information as to where this property is
     *                                              stored in the database.
     * </ol>
     * <p>
     * The <code>ResultSet</code> is sorted by the NAME column
     * <p>
     *
     * @return A <code>ResultSet</code> object; each row is a supported client info
     * property
     * <p>
     * @throws SQLException if a database access error occurs
     *                      <p>
     * @since 1.6
     */
    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        return null;
    }

    /**
     * Retrieves a description of the  system and user functions available
     * in the given catalog.
     * <p>
     * Only system and user function descriptions matching the schema and
     * function name criteria are returned.  They are ordered by
     * <code>FUNCTION_CAT</code>, <code>FUNCTION_SCHEM</code>,
     * <code>FUNCTION_NAME</code> and
     * <code>SPECIFIC_ NAME</code>.
     *
     * <P>Each function description has the the following columns:
     * <OL>
     * <LI><B>FUNCTION_CAT</B> String {@code =>} function catalog (may be <code>null</code>)
     * <LI><B>FUNCTION_SCHEM</B> String {@code =>} function schema (may be <code>null</code>)
     * <LI><B>FUNCTION_NAME</B> String {@code =>} function name.  This is the name
     * used to invoke the function
     * <LI><B>REMARKS</B> String {@code =>} explanatory comment on the function
     * <LI><B>FUNCTION_TYPE</B> short {@code =>} kind of function:
     * <UL>
     * <LI>functionResultUnknown - Cannot determine if a return value
     * or table will be returned
     * <LI> functionNoTable- Does not return a table
     * <LI> functionReturnsTable - Returns a table
     * </UL>
     * <LI><B>SPECIFIC_NAME</B> String  {@code =>} the name which uniquely identifies
     * this function within its schema.  This is a user specified, or DBMS
     * generated, name that may be different then the <code>FUNCTION_NAME</code>
     * for example with overload functions
     * </OL>
     * <p>
     * A user may not have permission to execute any of the functions that are
     * returned by <code>getFunctions</code>
     *
     * @param catalog             a catalog name; must match the catalog name as it
     *                            is stored in the database; "" retrieves those without a catalog;
     *                            <code>null</code> means that the catalog name should not be used to narrow
     *                            the search
     * @param schemaPattern       a schema name pattern; must match the schema name
     *                            as it is stored in the database; "" retrieves those without a schema;
     *                            <code>null</code> means that the schema name should not be used to narrow
     *                            the search
     * @param functionNamePattern a function name pattern; must match the
     *                            function name as it is stored in the database
     * @return <code>ResultSet</code> - each row is a function description
     * @throws SQLException if a database access error occurs
     * @see #getSearchStringEscape
     * @since 1.6
     */
    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        return null;
    }

    /**
     * Retrieves a description of the given catalog's system or user
     * function parameters and return type.
     *
     * <P>Only descriptions matching the schema,  function and
     * parameter name criteria are returned. They are ordered by
     * <code>FUNCTION_CAT</code>, <code>FUNCTION_SCHEM</code>,
     * <code>FUNCTION_NAME</code> and
     * <code>SPECIFIC_ NAME</code>. Within this, the return value,
     * if any, is first. Next are the parameter descriptions in call
     * order. The column descriptions follow in column number order.
     *
     * <P>Each row in the <code>ResultSet</code>
     * is a parameter description, column description or
     * return type description with the following fields:
     * <OL>
     * <LI><B>FUNCTION_CAT</B> String {@code =>} function catalog (may be <code>null</code>)
     * <LI><B>FUNCTION_SCHEM</B> String {@code =>} function schema (may be <code>null</code>)
     * <LI><B>FUNCTION_NAME</B> String {@code =>} function name.  This is the name
     * used to invoke the function
     * <LI><B>COLUMN_NAME</B> String {@code =>} column/parameter name
     * <LI><B>COLUMN_TYPE</B> Short {@code =>} kind of column/parameter:
     * <UL>
     * <LI> functionColumnUnknown - nobody knows
     * <LI> functionColumnIn - IN parameter
     * <LI> functionColumnInOut - INOUT parameter
     * <LI> functionColumnOut - OUT parameter
     * <LI> functionColumnReturn - function return value
     * <LI> functionColumnResult - Indicates that the parameter or column
     * is a column in the <code>ResultSet</code>
     * </UL>
     * <LI><B>DATA_TYPE</B> int {@code =>} SQL type from java.sql.Types
     * <LI><B>TYPE_NAME</B> String {@code =>} SQL type name, for a UDT type the
     * type name is fully qualified
     * <LI><B>PRECISION</B> int {@code =>} precision
     * <LI><B>LENGTH</B> int {@code =>} length in bytes of data
     * <LI><B>SCALE</B> short {@code =>} scale -  null is returned for data types where
     * SCALE is not applicable.
     * <LI><B>RADIX</B> short {@code =>} radix
     * <LI><B>NULLABLE</B> short {@code =>} can it contain NULL.
     * <UL>
     * <LI> functionNoNulls - does not allow NULL values
     * <LI> functionNullable - allows NULL values
     * <LI> functionNullableUnknown - nullability unknown
     * </UL>
     * <LI><B>REMARKS</B> String {@code =>} comment describing column/parameter
     * <LI><B>CHAR_OCTET_LENGTH</B> int  {@code =>} the maximum length of binary
     * and character based parameters or columns.  For any other datatype the returned value
     * is a NULL
     * <LI><B>ORDINAL_POSITION</B> int  {@code =>} the ordinal position, starting
     * from 1, for the input and output parameters. A value of 0
     * is returned if this row describes the function's return value.
     * For result set columns, it is the
     * ordinal position of the column in the result set starting from 1.
     * <LI><B>IS_NULLABLE</B> String  {@code =>} ISO rules are used to determine
     * the nullability for a parameter or column.
     * <UL>
     * <LI> YES           --- if the parameter or column can include NULLs
     * <LI> NO            --- if the parameter or column  cannot include NULLs
     * <LI> empty string  --- if the nullability for the
     * parameter  or column is unknown
     * </UL>
     * <LI><B>SPECIFIC_NAME</B> String  {@code =>} the name which uniquely identifies
     * this function within its schema.  This is a user specified, or DBMS
     * generated, name that may be different then the <code>FUNCTION_NAME</code>
     * for example with overload functions
     * </OL>
     *
     * <p>The PRECISION column represents the specified column size for the given
     * parameter or column.
     * For numeric data, this is the maximum precision.  For character data, this is the length in characters.
     * For datetime datatypes, this is the length in characters of the String representation (assuming the
     * maximum allowed precision of the fractional seconds component). For binary data, this is the length in bytes.  For the ROWID datatype,
     * this is the length in bytes. Null is returned for data types where the
     * column size is not applicable.
     *
     * @param catalog             a catalog name; must match the catalog name as it
     *                            is stored in the database; "" retrieves those without a catalog;
     *                            <code>null</code> means that the catalog name should not be used to narrow
     *                            the search
     * @param schemaPattern       a schema name pattern; must match the schema name
     *                            as it is stored in the database; "" retrieves those without a schema;
     *                            <code>null</code> means that the schema name should not be used to narrow
     *                            the search
     * @param functionNamePattern a procedure name pattern; must match the
     *                            function name as it is stored in the database
     * @param columnNamePattern   a parameter name pattern; must match the
     *                            parameter or column name as it is stored in the database
     * @return <code>ResultSet</code> - each row describes a
     * user function parameter, column  or return type
     * @throws SQLException if a database access error occurs
     * @see #getSearchStringEscape
     * @since 1.6
     */
    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        return null;
    }

    /**
     * Retrieves a description of the pseudo or hidden columns available
     * in a given table within the specified catalog and schema.
     * Pseudo or hidden columns may not always be stored within
     * a table and are not visible in a ResultSet unless they are
     * specified in the query's outermost SELECT list. Pseudo or hidden
     * columns may not necessarily be able to be modified. If there are
     * no pseudo or hidden columns, an empty ResultSet is returned.
     *
     * <P>Only column descriptions matching the catalog, schema, table
     * and column name criteria are returned.  They are ordered by
     * <code>TABLE_CAT</code>,<code>TABLE_SCHEM</code>, <code>TABLE_NAME</code>
     * and <code>COLUMN_NAME</code>.
     *
     * <P>Each column description has the following columns:
     * <OL>
     * <LI><B>TABLE_CAT</B> String {@code =>} table catalog (may be <code>null</code>)
     * <LI><B>TABLE_SCHEM</B> String {@code =>} table schema (may be <code>null</code>)
     * <LI><B>TABLE_NAME</B> String {@code =>} table name
     * <LI><B>COLUMN_NAME</B> String {@code =>} column name
     * <LI><B>DATA_TYPE</B> int {@code =>} SQL type from java.sql.Types
     * <LI><B>COLUMN_SIZE</B> int {@code =>} column size.
     * <LI><B>DECIMAL_DIGITS</B> int {@code =>} the number of fractional digits. Null is returned for data types where
     * DECIMAL_DIGITS is not applicable.
     * <LI><B>NUM_PREC_RADIX</B> int {@code =>} Radix (typically either 10 or 2)
     * <LI><B>COLUMN_USAGE</B> String {@code =>} The allowed usage for the column.  The
     * value returned will correspond to the enum name returned by {@link PseudoColumnUsage#name PseudoColumnUsage.name()}
     * <LI><B>REMARKS</B> String {@code =>} comment describing column (may be <code>null</code>)
     * <LI><B>CHAR_OCTET_LENGTH</B> int {@code =>} for char types the
     * maximum number of bytes in the column
     * <LI><B>IS_NULLABLE</B> String  {@code =>} ISO rules are used to determine the nullability for a column.
     * <UL>
     * <LI> YES           --- if the column can include NULLs
     * <LI> NO            --- if the column cannot include NULLs
     * <LI> empty string  --- if the nullability for the column is unknown
     * </UL>
     * </OL>
     *
     * <p>The COLUMN_SIZE column specifies the column size for the given column.
     * For numeric data, this is the maximum precision.  For character data, this is the length in characters.
     * For datetime datatypes, this is the length in characters of the String representation (assuming the
     * maximum allowed precision of the fractional seconds component). For binary data, this is the length in bytes.  For the ROWID datatype,
     * this is the length in bytes. Null is returned for data types where the
     * column size is not applicable.
     *
     * @param catalog           a catalog name; must match the catalog name as it
     *                          is stored in the database; "" retrieves those without a catalog;
     *                          <code>null</code> means that the catalog name should not be used to narrow
     *                          the search
     * @param schemaPattern     a schema name pattern; must match the schema name
     *                          as it is stored in the database; "" retrieves those without a schema;
     *                          <code>null</code> means that the schema name should not be used to narrow
     *                          the search
     * @param tableNamePattern  a table name pattern; must match the
     *                          table name as it is stored in the database
     * @param columnNamePattern a column name pattern; must match the column
     *                          name as it is stored in the database
     * @return <code>ResultSet</code> - each row is a column description
     * @throws SQLException if a database access error occurs
     * @see PseudoColumnUsage
     * @since 1.7
     */
    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        return null;
    }

    /**
     * Retrieves whether a generated key will always be returned if the column
     * name(s) or index(es) specified for the auto generated key column(s)
     * are valid and the statement succeeds.  The key that is returned may or
     * may not be based on the column(s) for the auto generated key.
     * Consult your JDBC driver documentation for additional details.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     * @throws SQLException if a database access error occurs
     * @since 1.7
     */
    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return false;
    }

    public boolean supportsStoredProcedures() throws SQLException {

        return false;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {

        return false;
    }

    public boolean supportsSubqueriesInExists() throws SQLException {

        return false;
    }

    public boolean supportsSubqueriesInIns() throws SQLException {

        return false;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {

        return false;
    }

    public boolean supportsTableCorrelationNames() throws SQLException {

        return false;
    }

    public boolean supportsTransactionIsolationLevel(int level)
            throws SQLException {

        return false;
    }

    public boolean supportsTransactions() throws SQLException {

        return false;
    }

    public boolean supportsUnion() throws SQLException {

        return false;
    }

    public boolean supportsUnionAll() throws SQLException {

        return false;
    }

    public boolean updatesAreDetected(int type) throws SQLException {

        return false;
    }

    public boolean usesLocalFilePerTable() throws SQLException {

        return false;
    }

    public boolean usesLocalFiles() throws SQLException {

        return false;
    }

    /**
     * Returns an object that implements the given interface to allow access to
     * non-standard methods, or standard methods not exposed by the proxy.
     * <p>
     * If the receiver implements the interface then the result is the receiver
     * or a proxy for the receiver. If the receiver is a wrapper
     * and the wrapped object implements the interface then the result is the
     * wrapped object or a proxy for the wrapped object. Otherwise return the
     * the result of calling <code>unwrap</code> recursively on the wrapped object
     * or a proxy for that result. If the receiver is not a
     * wrapper and does not implement the interface, then an <code>SQLException</code> is thrown.
     *
     * @param iface A Class defining an interface that the result must implement.
     * @return an object that implements the interface. May be a proxy for the actual implementing object.
     * @throws SQLException If no object found that implements the interface
     * @since 1.6
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    /**
     * Returns true if this either implements the interface argument or is directly or indirectly a wrapper
     * for an object that does. Returns false otherwise. If this implements the interface then return true,
     * else if this is a wrapper then return the result of recursively calling <code>isWrapperFor</code> on the wrapped
     * object. If this does not implement the interface and is not a wrapper, return false.
     * This method should be implemented as a low-cost operation compared to <code>unwrap</code> so that
     * callers can use this method to avoid expensive <code>unwrap</code> calls that may fail. If this method
     * returns true then calling <code>unwrap</code> with the same argument should succeed.
     *
     * @param iface a Class defining an interface.
     * @return true if this implements the interface or directly or indirectly wraps an object that does.
     * @throws SQLException if an error occurs while determining whether this is a wrapper
     *                      for an object with the given interface.
     * @since 1.6
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}

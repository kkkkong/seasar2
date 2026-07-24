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
package org.seasar.extension.dbcp.impl;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.XAConnection;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import org.seasar.extension.dbcp.ConnectionPool;
import org.seasar.extension.dbcp.ConnectionWrapper;
import org.seasar.extension.jdbc.impl.PreparedStatementWrapper;
import org.seasar.framework.exception.SSQLException;
import org.seasar.framework.log.Logger;

/**
 * {@link ConnectionWrapper}の実装クラスです。
 * 
 * @author higa
 * 
 */
public class ConnectionWrapperImpl implements ConnectionWrapper,
        ConnectionEventListener {

    private static final Logger logger_ = Logger
            .getLogger(ConnectionWrapperImpl.class);

    private XAConnection xaConnection_;

    private Connection physicalConnection_;

    private XAResource xaResource_;

    private ConnectionPool connectionPool_;

    private boolean closed_ = false;

    private Transaction tx_;

    /**
     * {@link ConnectionWrapperImpl}を作成します。
     * 
     * @param xaConnection
     *            XAコネクション
     * @param physicalConnection
     *            物理コネクション
     * @param connectionPool
     *            コネクションプール
     * @param tx
     *            トランザクション
     * @throws SQLException
     *             SQL例外が発生した場合
     */
    public ConnectionWrapperImpl(final XAConnection xaConnection,
            final Connection physicalConnection,
            final ConnectionPool connectionPool, final Transaction tx)
            throws SQLException {
        xaConnection_ = xaConnection;
        physicalConnection_ = physicalConnection;
        xaResource_ = new XAResourceWrapperImpl(xaConnection.getXAResource(),
                this);
        connectionPool_ = connectionPool;
        tx_ = tx;
        xaConnection_.addConnectionEventListener(this);
    }

    public Connection getPhysicalConnection() {
        return physicalConnection_;
    }

    public XAResource getXAResource() {
        return xaResource_;
    }

    public XAConnection getXAConnection() {
        return xaConnection_;
    }

    public void init(final Transaction tx) {
        closed_ = false;
        tx_ = tx;
    }

    public void cleanup() {
        xaConnection_.removeConnectionEventListener(this);
        closed_ = true;
        xaConnection_ = null;
        physicalConnection_ = null;
        tx_ = null;
    }

    public void closeReally() {
        if (xaConnection_ == null) {
            return;
        }
        closed_ = true;
        try {
            if (!physicalConnection_.isClosed()) {
                if (!physicalConnection_.getAutoCommit()) {
                    try {
                        physicalConnection_.rollback();
                        physicalConnection_.setAutoCommit(true);
                    } catch (final SQLException ex) {
                        logger_.log(ex);
                    }
                }
                physicalConnection_.close();
            }
        } catch (final SQLException ex) {
            logger_.log(ex);
        } finally {
            physicalConnection_ = null;
        }

        try {
            xaConnection_.close();
            logger_.log("DSSR0001", null);
        } catch (final SQLException ex) {
            logger_.log(ex);
        } finally {
            xaConnection_ = null;
        }
    }

    private void assertOpened() throws SQLException {
        if (closed_) {
            throw new SSQLException("ESSR0062", null);
        }
    }

    private void assertLocalTx() throws SQLException {
        if (tx_ != null) {
            throw new SSQLException("ESSR0366", null);
        }
    }

    public void release() throws SQLException {
        if (!closed_) {
            connectionPool_.release(this);
        }
    }

    public void connectionClosed(final ConnectionEvent event) {
    }

    public void connectionErrorOccurred(final ConnectionEvent event) {
        try {
            release();
        } catch (final SQLException ignore) {
        }
    }

    public Statement createStatement() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.createStatement();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public PreparedStatement prepareStatement(final String sql)
            throws SQLException {
        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_
                    .prepareStatement(sql), sql);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public CallableStatement prepareCall(final String sql) throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.prepareCall(sql);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public String nativeSQL(final String sql) throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.nativeSQL(sql);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public boolean isClosed() throws SQLException {
        return closed_;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getMetaData();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setReadOnly(final boolean readOnly) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setReadOnly(readOnly);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public boolean isReadOnly() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.isReadOnly();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setCatalog(final String catalog) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setCatalog(catalog);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public String getCatalog() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getCatalog();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void close() throws SQLException {
        if (closed_) {
            return;
        }
        if (logger_.isDebugEnabled()) {
            logger_.log("DSSR0002", new Object[] { tx_ });
        }
        if (tx_ == null) {
            connectionPool_.checkIn(this);
        } else {
            connectionPool_.checkInTx(tx_);
        }
    }

    public void setTransactionIsolation(final int level) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setTransactionIsolation(level);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public int getTransactionIsolation() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getTransactionIsolation();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getWarnings();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void clearWarnings() throws SQLException {
        assertOpened();
        try {
            physicalConnection_.clearWarnings();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void commit() throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection_.commit();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void rollback() throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection_.rollback();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        assertOpened();
        if (autoCommit) {
            assertLocalTx();
        }
        try {
            physicalConnection_.setAutoCommit(autoCommit);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public boolean getAutoCommit() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getAutoCommit();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public Statement createStatement(final int resultSetType,
            final int resultSetConcurrency) throws SQLException {

        assertOpened();
        try {
            return physicalConnection_.createStatement(resultSetType,
                    resultSetConcurrency);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public Map getTypeMap() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getTypeMap();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void setTypeMap(final Map map) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setTypeMap(map);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public PreparedStatement prepareStatement(final String sql,
            final int resultSetType, final int resultSetConcurrency)
            throws SQLException {

        assertOpened();
        try {
            return new PreparedStatementWrapper(
                    physicalConnection_.prepareStatement(sql, resultSetType,
                            resultSetConcurrency), sql);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public CallableStatement prepareCall(final String sql,
            final int resultSetType, final int resultSetConcurrency)
            throws SQLException {

        assertOpened();
        try {
            return physicalConnection_.prepareCall(sql, resultSetType,
                    resultSetConcurrency);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public void setHoldability(final int holdability) throws SQLException {
        assertOpened();
        try {
            physicalConnection_.setHoldability(holdability);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public int getHoldability() throws SQLException {
        assertOpened();
        try {
            return physicalConnection_.getHoldability();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            return physicalConnection_.setSavepoint();
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public Savepoint setSavepoint(final String name) throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            return physicalConnection_.setSavepoint(name);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void rollback(final Savepoint savepoint) throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection_.rollback(savepoint);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        assertOpened();
        assertLocalTx();
        try {
            physicalConnection_.releaseSavepoint(savepoint);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public Statement createStatement(final int resultSetType,
            final int resultSetConcurrency, final int resultSetHoldability)
            throws SQLException {

        assertOpened();
        try {
            return physicalConnection_.createStatement(resultSetType,
                    resultSetConcurrency, resultSetHoldability);
        } catch (final SQLException ex) {
            release();
            throw ex;
        }
    }

    public PreparedStatement prepareStatement(final String sql,
            final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {

        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_
                    .prepareStatement(sql, resultSetType, resultSetConcurrency,
                            resultSetHoldability), sql);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public CallableStatement prepareCall(final String sql,
            final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {

        assertOpened();
        try {
            return physicalConnection_.prepareCall(sql, resultSetType,
                    resultSetConcurrency, resultSetHoldability);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public PreparedStatement prepareStatement(final String sql,
            final int autoGeneratedKeys) throws SQLException {

        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_
                    .prepareStatement(sql, autoGeneratedKeys), sql);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public PreparedStatement prepareStatement(final String sql,
            final int[] columnIndexes) throws SQLException {

        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_
                    .prepareStatement(sql, columnIndexes), sql);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    public PreparedStatement prepareStatement(final String sql,
            final String[] columnNames) throws SQLException {

        assertOpened();
        try {
            return new PreparedStatementWrapper(physicalConnection_
                    .prepareStatement(sql, columnNames), sql);
        } catch (final SQLException ex) {
            release();
            throw wrapException(ex, sql);
        }
    }

    /**
     * Constructs an object that implements the <code>Clob</code> interface. The object
     * returned initially contains no data.  The <code>setAsciiStream</code>,
     * <code>setCharacterStream</code> and <code>setString</code> methods of
     * the <code>Clob</code> interface may be used to add data to the <code>Clob</code>.
     *
     * @return An object that implements the <code>Clob</code> interface
     * @throws SQLException                    if an object that implements the
     *                                         <code>Clob</code> interface can not be constructed, this method is
     *                                         called on a closed connection or a database access error occurs.
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
     *                                         this data type
     * @since 1.6
     */
    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    /**
     * Constructs an object that implements the <code>Blob</code> interface. The object
     * returned initially contains no data.  The <code>setBinaryStream</code> and
     * <code>setBytes</code> methods of the <code>Blob</code> interface may be used to add data to
     * the <code>Blob</code>.
     *
     * @return An object that implements the <code>Blob</code> interface
     * @throws SQLException                    if an object that implements the
     *                                         <code>Blob</code> interface can not be constructed, this method is
     *                                         called on a closed connection or a database access error occurs.
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
     *                                         this data type
     * @since 1.6
     */
    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    /**
     * Constructs an object that implements the <code>NClob</code> interface. The object
     * returned initially contains no data.  The <code>setAsciiStream</code>,
     * <code>setCharacterStream</code> and <code>setString</code> methods of the <code>NClob</code> interface may
     * be used to add data to the <code>NClob</code>.
     *
     * @return An object that implements the <code>NClob</code> interface
     * @throws SQLException                    if an object that implements the
     *                                         <code>NClob</code> interface can not be constructed, this method is
     *                                         called on a closed connection or a database access error occurs.
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
     *                                         this data type
     * @since 1.6
     */
    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    /**
     * Constructs an object that implements the <code>SQLXML</code> interface. The object
     * returned initially contains no data. The <code>createXmlStreamWriter</code> object and
     * <code>setString</code> method of the <code>SQLXML</code> interface may be used to add data to the <code>SQLXML</code>
     * object.
     *
     * @return An object that implements the <code>SQLXML</code> interface
     * @throws SQLException                    if an object that implements the <code>SQLXML</code> interface can not
     *                                         be constructed, this method is
     *                                         called on a closed connection or a database access error occurs.
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
     *                                         this data type
     * @since 1.6
     */
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    /**
     * Returns true if the connection has not been closed and is still valid.
     * The driver shall submit a query on the connection or use some other
     * mechanism that positively verifies the connection is still valid when
     * this method is called.
     * <p>
     * The query submitted by the driver to validate the connection shall be
     * executed in the context of the current transaction.
     *
     * @param timeout -             The time in seconds to wait for the database operation
     *                used to validate the connection to complete.  If
     *                the timeout period expires before the operation
     *                completes, this method returns false.  A value of
     *                0 indicates a timeout is not applied to the
     *                database operation.
     *                <p>
     * @return true if the connection is valid, false otherwise
     * @throws SQLException if the value supplied for <code>timeout</code>
     *                      is less then 0
     * @see DatabaseMetaData#getClientInfoProperties
     * @since 1.6
     */
    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    /**
     * Sets the value of the client info property specified by name to the
     * value specified by value.
     * <p>
     * Applications may use the <code>DatabaseMetaData.getClientInfoProperties</code>
     * method to determine the client info properties supported by the driver
     * and the maximum length that may be specified for each property.
     * <p>
     * The driver stores the value specified in a suitable location in the
     * database.  For example in a special register, session parameter, or
     * system table column.  For efficiency the driver may defer setting the
     * value in the database until the next time a statement is executed or
     * prepared.  Other than storing the client information in the appropriate
     * place in the database, these methods shall not alter the behavior of
     * the connection in anyway.  The values supplied to these methods are
     * used for accounting, diagnostics and debugging purposes only.
     * <p>
     * The driver shall generate a warning if the client info name specified
     * is not recognized by the driver.
     * <p>
     * If the value specified to this method is greater than the maximum
     * length for the property the driver may either truncate the value and
     * generate a warning or generate a <code>SQLClientInfoException</code>.  If the driver
     * generates a <code>SQLClientInfoException</code>, the value specified was not set on the
     * connection.
     * <p>
     * The following are standard client info properties.  Drivers are not
     * required to support these properties however if the driver supports a
     * client info property that can be described by one of the standard
     * properties, the standard property name should be used.
     *
     * <ul>
     * <li>ApplicationName  -       The name of the application currently utilizing
     *                                                      the connection</li>
     * <li>ClientUser               -       The name of the user that the application using
     *                                                      the connection is performing work for.  This may
     *                                                      not be the same as the user name that was used
     *                                                      in establishing the connection.</li>
     * <li>ClientHostname   -       The hostname of the computer the application
     *                                                      using the connection is running on.</li>
     * </ul>
     * <p>
     *
     * @param name  The name of the client info property to set
     * @param value The value to set the client info property to.  If the
     *              value is null, the current value of the specified
     *              property is cleared.
     *              <p>
     * @throws SQLClientInfoException if the database server returns an error while
     *                                setting the client info value on the database server or this method
     *                                is called on a closed connection
     *                                <p>
     * @since 1.6
     */
    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    /**
     * Sets the value of the connection's client info properties.  The
     * <code>Properties</code> object contains the names and values of the client info
     * properties to be set.  The set of client info properties contained in
     * the properties list replaces the current set of client info properties
     * on the connection.  If a property that is currently set on the
     * connection is not present in the properties list, that property is
     * cleared.  Specifying an empty properties list will clear all of the
     * properties on the connection.  See <code>setClientInfo (String, String)</code> for
     * more information.
     * <p>
     * If an error occurs in setting any of the client info properties, a
     * <code>SQLClientInfoException</code> is thrown. The <code>SQLClientInfoException</code>
     * contains information indicating which client info properties were not set.
     * The state of the client information is unknown because
     * some databases do not allow multiple client info properties to be set
     * atomically.  For those databases, one or more properties may have been
     * set before the error occurred.
     * <p>
     *
     * @param properties the list of client info properties to set
     *                   <p>
     * @throws SQLClientInfoException if the database server returns an error while
     *                                setting the clientInfo values on the database server or this method
     *                                is called on a closed connection
     * @see Connection#setClientInfo(String, String) setClientInfo(String, String)
     * @since 1.6
     * <p>
     *
     */
    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    /**
     * Returns the value of the client info property specified by name.  This
     * method may return null if the specified client info property has not
     * been set and does not have a default value.  This method will also
     * return null if the specified client info property name is not supported
     * by the driver.
     * <p>
     * Applications may use the <code>DatabaseMetaData.getClientInfoProperties</code>
     * method to determine the client info properties supported by the driver.
     * <p>
     *
     * @param name The name of the client info property to retrieve
     *             <p>
     * @return The value of the client info property specified
     * <p>
     * @throws SQLException if the database server returns an error when
     *                      fetching the client info value from the database
     *                      or this method is called on a closed connection
     *                      <p>
     * @see DatabaseMetaData#getClientInfoProperties
     * @since 1.6
     */
    @Override
    public String getClientInfo(String name) throws SQLException {
        return "";
    }

    /**
     * Returns a list containing the name and current value of each client info
     * property supported by the driver.  The value of a client info property
     * may be null if the property has not been set and does not have a
     * default value.
     * <p>
     *
     * @return A <code>Properties</code> object that contains the name and current value of
     * each of the client info properties supported by the driver.
     * <p>
     * @throws SQLException if the database server returns an error when
     *                      fetching the client info values from the database
     *                      or this method is called on a closed connection
     *                      <p>
     * @since 1.6
     */
    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    /**
     * Factory method for creating Array objects.
     * <p>
     * <b>Note: </b>When <code>createArrayOf</code> is used to create an array object
     * that maps to a primitive data type, then it is implementation-defined
     * whether the <code>Array</code> object is an array of that primitive
     * data type or an array of <code>Object</code>.
     * <p>
     * <b>Note: </b>The JDBC driver is responsible for mapping the elements
     * <code>Object</code> array to the default JDBC SQL type defined in
     * java.sql.Types for the given class of <code>Object</code>. The default
     * mapping is specified in Appendix B of the JDBC specification.  If the
     * resulting JDBC type is not the appropriate type for the given typeName then
     * it is implementation defined whether an <code>SQLException</code> is
     * thrown or the driver supports the resulting conversion.
     *
     * @param typeName the SQL name of the type the elements of the array map to. The typeName is a
     *                 database-specific name which may be the name of a built-in type, a user-defined type or a standard  SQL type supported by this database. This
     *                 is the value returned by <code>Array.getBaseTypeName</code>
     * @param elements the elements that populate the returned object
     * @return an Array object whose elements map to the specified SQL type
     * @throws SQLException                    if a database error occurs, the JDBC type is not
     *                                         appropriate for the typeName and the conversion is not supported, the typeName is null or this method is called on a closed connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this data type
     * @since 1.6
     */
    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    /**
     * Factory method for creating Struct objects.
     *
     * @param typeName   the SQL type name of the SQL structured type that this <code>Struct</code>
     *                   object maps to. The typeName is the name of  a user-defined type that
     *                   has been defined for this database. It is the value returned by
     *                   <code>Struct.getSQLTypeName</code>.
     * @param attributes the attributes that populate the returned object
     * @return a Struct object that maps to the given SQL type and is populated with the given attributes
     * @throws SQLException                    if a database error occurs, the typeName is null or this method is called on a closed connection
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this data type
     * @since 1.6
     */
    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    /**
     * Sets the given schema name to access.
     * <p>
     * If the driver does not support schemas, it will
     * silently ignore this request.
     * <p>
     * Calling {@code setSchema} has no effect on previously created or prepared
     * {@code Statement} objects. It is implementation defined whether a DBMS
     * prepare operation takes place immediately when the {@code Connection}
     * method {@code prepareStatement} or {@code prepareCall} is invoked.
     * For maximum portability, {@code setSchema} should be called before a
     * {@code Statement} is created or prepared.
     *
     * @param schema the name of a schema  in which to work
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     * @see #getSchema
     * @since 1.7
     */
    @Override
    public void setSchema(String schema) throws SQLException {

    }

    /**
     * Retrieves this <code>Connection</code> object's current schema name.
     *
     * @return the current schema name or <code>null</code> if there is none
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     * @see #setSchema
     * @since 1.7
     */
    @Override
    public String getSchema() throws SQLException {
        return "";
    }

    /**
     * Terminates an open connection.  Calling <code>abort</code> results in:
     * <ul>
     * <li>The connection marked as closed
     * <li>Closes any physical connection to the database
     * <li>Releases resources used by the connection
     * <li>Insures that any thread that is currently accessing the connection
     * will either progress to completion or throw an <code>SQLException</code>.
     * </ul>
     * <p>
     * Calling <code>abort</code> marks the connection closed and releases any
     * resources. Calling <code>abort</code> on a closed connection is a
     * no-op.
     * <p>
     * It is possible that the aborting and releasing of the resources that are
     * held by the connection can take an extended period of time.  When the
     * <code>abort</code> method returns, the connection will have been marked as
     * closed and the <code>Executor</code> that was passed as a parameter to abort
     * may still be executing tasks to release resources.
     * <p>
     * This method checks to see that there is an <code>SQLPermission</code>
     * object before allowing the method to proceed.  If a
     * <code>SecurityManager</code> exists and its
     * <code>checkPermission</code> method denies calling <code>abort</code>,
     * this method throws a
     * <code>java.lang.SecurityException</code>.
     *
     * @param executor The <code>Executor</code>  implementation which will
     *                 be used by <code>abort</code>.
     * @throws SQLException      if a database access error occurs or
     *                           the {@code executor} is {@code null},
     * @throws SecurityException if a security manager exists and its
     *                           <code>checkPermission</code> method denies calling <code>abort</code>
     * @see SecurityManager#checkPermission
     * @see Executor
     * @since 1.7
     */
    @Override
    public void abort(Executor executor) throws SQLException {

    }

    /**
     *
     * Sets the maximum period a <code>Connection</code> or
     * objects created from the <code>Connection</code>
     * will wait for the database to reply to any one request. If any
     * request remains unanswered, the waiting method will
     * return with a <code>SQLException</code>, and the <code>Connection</code>
     * or objects created from the <code>Connection</code>  will be marked as
     * closed. Any subsequent use of
     * the objects, with the exception of the <code>close</code>,
     * <code>isClosed</code> or <code>Connection.isValid</code>
     * methods, will result in  a <code>SQLException</code>.
     * <p>
     * <b>Note</b>: This method is intended to address a rare but serious
     * condition where network partitions can cause threads issuing JDBC calls
     * to hang uninterruptedly in socket reads, until the OS TCP-TIMEOUT
     * (typically 10 minutes). This method is related to the
     * {@link #abort abort() } method which provides an administrator
     * thread a means to free any such threads in cases where the
     * JDBC connection is accessible to the administrator thread.
     * The <code>setNetworkTimeout</code> method will cover cases where
     * there is no administrator thread, or it has no access to the
     * connection. This method is severe in it's effects, and should be
     * given a high enough value so it is never triggered before any more
     * normal timeouts, such as transaction timeouts.
     * <p>
     * JDBC driver implementations  may also choose to support the
     * {@code setNetworkTimeout} method to impose a limit on database
     * response time, in environments where no network is present.
     * <p>
     * Drivers may internally implement some or all of their API calls with
     * multiple internal driver-database transmissions, and it is left to the
     * driver implementation to determine whether the limit will be
     * applied always to the response to the API call, or to any
     * single  request made during the API call.
     * <p>
     * <p>
     * This method can be invoked more than once, such as to set a limit for an
     * area of JDBC code, and to reset to the default on exit from this area.
     * Invocation of this method has no impact on already outstanding
     * requests.
     * <p>
     * The {@code Statement.setQueryTimeout()} timeout value is independent of the
     * timeout value specified in {@code setNetworkTimeout}. If the query timeout
     * expires  before the network timeout then the
     * statement execution will be canceled. If the network is still
     * active the result will be that both the statement and connection
     * are still usable. However if the network timeout expires before
     * the query timeout or if the statement timeout fails due to network
     * problems, the connection will be marked as closed, any resources held by
     * the connection will be released and both the connection and
     * statement will be unusable.
     * <p>
     * When the driver determines that the {@code setNetworkTimeout} timeout
     * value has expired, the JDBC driver marks the connection
     * closed and releases any resources held by the connection.
     * <p>
     * <p>
     * This method checks to see that there is an <code>SQLPermission</code>
     * object before allowing the method to proceed.  If a
     * <code>SecurityManager</code> exists and its
     * <code>checkPermission</code> method denies calling
     * <code>setNetworkTimeout</code>, this method throws a
     * <code>java.lang.SecurityException</code>.
     *
     * @param executor     The <code>Executor</code>  implementation which will
     *                     be used by <code>setNetworkTimeout</code>.
     * @param milliseconds The time in milliseconds to wait for the database
     *                     operation
     *                     to complete.  If the JDBC driver does not support milliseconds, the
     *                     JDBC driver will round the value up to the nearest second.  If the
     *                     timeout period expires before the operation
     *                     completes, a SQLException will be thrown.
     *                     A value of 0 indicates that there is not timeout for database operations.
     * @throws SQLException                    if a database access error occurs, this
     *                                         method is called on a closed connection,
     *                                         the {@code executor} is {@code null},
     *                                         or the value specified for <code>seconds</code> is less than 0.
     * @throws SecurityException               if a security manager exists and its
     *                                         <code>checkPermission</code> method denies calling
     *                                         <code>setNetworkTimeout</code>.
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
     *                                         this method
     * @see SecurityManager#checkPermission
     * @see Statement#setQueryTimeout
     * @see #getNetworkTimeout
     * @see #abort
     * @see Executor
     * @since 1.7
     */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    /**
     * Retrieves the number of milliseconds the driver will
     * wait for a database request to complete.
     * If the limit is exceeded, a
     * <code>SQLException</code> is thrown.
     *
     * @return the current timeout limit in milliseconds; zero means there is
     * no limit
     * @throws SQLException                    if a database access error occurs or
     *                                         this method is called on a closed <code>Connection</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
     *                                         this method
     * @see #setNetworkTimeout
     * @since 1.7
     */
    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    private SQLException wrapException(final SQLException e, final String sql) {
        return new SSQLException("ESSR0072",
                new Object[] { sql, e.getMessage(),
                        new Integer(e.getErrorCode()), e.getSQLState() }, e
                        .getSQLState(), e.getErrorCode(), e, sql);
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

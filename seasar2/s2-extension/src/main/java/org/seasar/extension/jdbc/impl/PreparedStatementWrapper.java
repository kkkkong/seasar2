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
package org.seasar.extension.jdbc.impl;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

import org.seasar.framework.exception.SSQLException;

/**
 * {@link PreparedStatement}のラッパです。
 * 
 * @author higa
 * 
 */
public class PreparedStatementWrapper implements PreparedStatement {

    private final PreparedStatement original;

    private final String sql;

    /**
     * {@link PreparedStatementWrapper}を作成します。
     * 
     * @param original
     *            オリジナル
     * @param sql
     *            SQL
     */
    public PreparedStatementWrapper(final PreparedStatement original,
            final String sql) {
        this.original = original;
        this.sql = sql;
    }

    private SQLException wrapException(final SQLException e) {
        return wrapException(e, sql);
    }

    private SQLException wrapException(final SQLException e, final String sql) {
        if (sql != null) {
            return new SSQLException("ESSR0072", new Object[] { sql,
                    String.valueOf(e.getErrorCode()), e.getSQLState() }, e
                    .getSQLState(), e.getErrorCode(), e, sql);
        }
        return e;
    }

    public ResultSet executeQuery() throws SQLException {
        try {
            return original.executeQuery();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int executeUpdate() throws SQLException {
        try {
            return original.executeUpdate();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return original.getMetaData();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            return original.getParameterMetaData();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    /**
     * Sets the designated parameter to the given <code>java.sql.RowId</code> object. The
     * driver converts this to a SQL <code>ROWID</code> value when it sends it
     * to the database
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x              the parameter value
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if a database access error occurs or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {

    }

    /**
     * Sets the designated parameter to the given <code>String</code> object.
     * The driver converts this to a SQL <code>NCHAR</code> or
     * <code>NVARCHAR</code> or <code>LONGNVARCHAR</code> value
     * (depending on the argument's
     * size relative to the driver's limits on <code>NVARCHAR</code> values)
     * when it sends it to the database.
     *
     * @param parameterIndex of the first parameter is 1, the second is 2, ...
     * @param value          the parameter value
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if the driver does not support national
     *                                         character sets;  if the driver can detect that a data conversion
     *                                         error could occur; if a database access error occurs; or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {

    }

    /**
     * Sets the designated parameter to a <code>Reader</code> object. The
     * <code>Reader</code> reads the data till end-of-file is reached. The
     * driver does the necessary conversion from Java character format to
     * the national character set in the database.
     *
     * @param parameterIndex of the first parameter is 1, the second is 2, ...
     * @param value          the parameter value
     * @param length         the number of characters in the parameter data.
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if the driver does not support national
     *                                         character sets;  if the driver can detect that a data conversion
     *                                         error could occur; if a database access error occurs; or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

    }

    /**
     * Sets the designated parameter to a <code>java.sql.NClob</code> object. The driver converts this to a
     * SQL <code>NCLOB</code> value when it sends it to the database.
     *
     * @param parameterIndex of the first parameter is 1, the second is 2, ...
     * @param value          the parameter value
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if the driver does not support national
     *                                         character sets;  if the driver can detect that a data conversion
     *                                         error could occur; if a database access error occurs; or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {

    }

    /**
     * Sets the designated parameter to a <code>Reader</code> object.  The reader must contain  the number
     * of characters specified by length otherwise a <code>SQLException</code> will be
     * generated when the <code>PreparedStatement</code> is executed.
     * This method differs from the <code>setCharacterStream (int, Reader, int)</code> method
     * because it informs the driver that the parameter value should be sent to
     * the server as a <code>CLOB</code>.  When the <code>setCharacterStream</code> method is used, the
     * driver may have to do extra work to determine whether the parameter
     * data should be sent to the server as a <code>LONGVARCHAR</code> or a <code>CLOB</code>
     *
     * @param parameterIndex index of the first parameter is 1, the second is 2, ...
     * @param reader         An object that contains the data to set the parameter value to.
     * @param length         the number of characters in the parameter data.
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if a database access error occurs; this method is called on
     *                                         a closed <code>PreparedStatement</code> or if the length specified is less than zero.
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    /**
     * Sets the designated parameter to a <code>InputStream</code> object.  The inputstream must contain  the number
     * of characters specified by length otherwise a <code>SQLException</code> will be
     * generated when the <code>PreparedStatement</code> is executed.
     * This method differs from the <code>setBinaryStream (int, InputStream, int)</code>
     * method because it informs the driver that the parameter value should be
     * sent to the server as a <code>BLOB</code>.  When the <code>setBinaryStream</code> method is used,
     * the driver may have to do extra work to determine whether the parameter
     * data should be sent to the server as a <code>LONGVARBINARY</code> or a <code>BLOB</code>
     *
     * @param parameterIndex index of the first parameter is 1,
     *                       the second is 2, ...
     * @param inputStream    An object that contains the data to set the parameter
     *                       value to.
     * @param length         the number of bytes in the parameter data.
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if a database access error occurs;
     *                                         this method is called on a closed <code>PreparedStatement</code>;
     *                                         if the length specified
     *                                         is less than zero or if the number of bytes in the inputstream does not match
     *                                         the specified length.
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

    }

    /**
     * Sets the designated parameter to a <code>Reader</code> object.  The reader must contain  the number
     * of characters specified by length otherwise a <code>SQLException</code> will be
     * generated when the <code>PreparedStatement</code> is executed.
     * This method differs from the <code>setCharacterStream (int, Reader, int)</code> method
     * because it informs the driver that the parameter value should be sent to
     * the server as a <code>NCLOB</code>.  When the <code>setCharacterStream</code> method is used, the
     * driver may have to do extra work to determine whether the parameter
     * data should be sent to the server as a <code>LONGNVARCHAR</code> or a <code>NCLOB</code>
     *
     * @param parameterIndex index of the first parameter is 1, the second is 2, ...
     * @param reader         An object that contains the data to set the parameter value to.
     * @param length         the number of characters in the parameter data.
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if the length specified is less than zero;
     *                                         if the driver does not support national character sets;
     *                                         if the driver can detect that a data conversion
     *                                         error could occur;  if a database access error occurs or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    /**
     * Sets the designated parameter to the given <code>java.sql.SQLXML</code> object.
     * The driver converts this to an
     * SQL <code>XML</code> value when it sends it to the database.
     * <p>
     *
     * @param parameterIndex index of the first parameter is 1, the second is 2, ...
     * @param xmlObject      a <code>SQLXML</code> object that maps an SQL <code>XML</code> value
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if a database access error occurs;
     *                                         this method is called on a closed <code>PreparedStatement</code>
     *                                         or the <code>java.xml.transform.Result</code>,
     *                                         <code>Writer</code> or <code>OutputStream</code> has not been closed for
     *                                         the <code>SQLXML</code> object
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

    }

    public int getMaxRows() throws SQLException {
        try {
            return original.getMaxRows();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getQueryTimeout() throws SQLException {
        return original.getQueryTimeout();
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            return original.getWarnings();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public ResultSet getResultSet() throws SQLException {
        try {
            return original.getResultSet();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getUpdateCount() throws SQLException {
        try {
            return original.getUpdateCount();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public boolean getMoreResults() throws SQLException {
        try {
            return original.getMoreResults();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getFetchDirection() throws SQLException {
        try {
            return original.getFetchDirection();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getFetchSize() throws SQLException {
        try {
            return original.getFetchSize();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        try {
            return original.getResultSetConcurrency();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int getResultSetType() throws SQLException {
        try {
            return original.getResultSetType();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int[] executeBatch() throws SQLException {
        try {
            return original.executeBatch();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return original.getConnection();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public boolean getMoreResults(final int current) throws SQLException {
        try {
            return original.getMoreResults();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            return original.getGeneratedKeys();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public int executeUpdate(final String sql, final int autoGeneratedKeys)
            throws SQLException {
        try {
            return original.executeUpdate();
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int getResultSetHoldability() throws SQLException {
        try {
            return original.getResultSetHoldability();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    /**
     * Retrieves whether this <code>Statement</code> object has been closed. A <code>Statement</code> is closed if the
     * method close has been called on it, or if it is automatically closed.
     *
     * @return true if this <code>Statement</code> object is closed; false if it is still open
     * @throws SQLException if a database access error occurs
     * @since 1.6
     */
    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    /**
     * Requests that a <code>Statement</code> be pooled or not pooled.  The value
     * specified is a hint to the statement pool implementation indicating
     * whether the application wants the statement to be pooled.  It is up to
     * the statement pool manager as to whether the hint is used.
     * <p>
     * The poolable value of a statement is applicable to both internal
     * statement caches implemented by the driver and external statement caches
     * implemented by application servers and other applications.
     * <p>
     * By default, a <code>Statement</code> is not poolable when created, and
     * a <code>PreparedStatement</code> and <code>CallableStatement</code>
     * are poolable when created.
     * <p>
     *
     * @param poolable requests that the statement be pooled if true and
     *                 that the statement not be pooled if false
     *                 <p>
     * @throws SQLException if this method is called on a closed
     *                      <code>Statement</code>
     *                      <p>
     * @since 1.6
     */
    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    /**
     * Returns a  value indicating whether the <code>Statement</code>
     * is poolable or not.
     * <p>
     *
     * @return <code>true</code> if the <code>Statement</code>
     * is poolable; <code>false</code> otherwise
     * <p>
     * @throws SQLException if this method is called on a closed
     *                      <code>Statement</code>
     *                      <p>
     * @see Statement#setPoolable(boolean) setPoolable(boolean)
     * @since 1.6
     * <p>
     */
    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    /**
     * Specifies that this {@code Statement} will be closed when all its
     * dependent result sets are closed. If execution of the {@code Statement}
     * does not produce any result sets, this method has no effect.
     * <p>
     * <strong>Note:</strong> Multiple calls to {@code closeOnCompletion} do
     * not toggle the effect on this {@code Statement}. However, a call to
     * {@code closeOnCompletion} does effect both the subsequent execution of
     * statements, and statements that currently have open, dependent,
     * result sets.
     *
     * @throws SQLException if this method is called on a closed
     *                      {@code Statement}
     * @since 1.7
     */
    @Override
    public void closeOnCompletion() throws SQLException {

    }

    /**
     * Returns a value indicating whether this {@code Statement} will be
     * closed when all its dependent result sets are closed.
     *
     * @return {@code true} if the {@code Statement} will be closed when all
     * of its dependent result sets are closed; {@code false} otherwise
     * @throws SQLException if this method is called on a closed
     *                      {@code Statement}
     * @since 1.7
     */
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    public void addBatch() throws SQLException {
        try {
            original.addBatch();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void addBatch(final String sql) throws SQLException {
        try {
            original.addBatch(sql);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public void cancel() throws SQLException {
        try {
            original.cancel();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void clearBatch() throws SQLException {
        try {
            original.clearBatch();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void clearParameters() throws SQLException {
        try {
            original.clearParameters();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            original.clearWarnings();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void close() throws SQLException {
        try {
            original.close();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public boolean execute() throws SQLException {
        try {
            return original.execute();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public boolean execute(final String sql, final int autoGeneratedKeys)
            throws SQLException {
        try {
            return original.execute(sql, autoGeneratedKeys);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public boolean execute(final String sql, final int[] columnIndexes)
            throws SQLException {
        try {
            return original.execute(sql, columnIndexes);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public boolean execute(final String sql, final String[] columnNames)
            throws SQLException {
        try {
            return original.execute(sql, columnNames);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public boolean execute(final String sql) throws SQLException {
        try {
            return original.execute(sql);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public ResultSet executeQuery(final String sql) throws SQLException {
        try {
            return original.executeQuery(sql);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int executeUpdate(final String sql, final int[] columnIndexes)
            throws SQLException {
        try {
            return original.executeUpdate(sql, columnIndexes);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int executeUpdate(final String sql, final String[] columnNames)
            throws SQLException {
        try {
            return original.executeUpdate(sql, columnNames);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int executeUpdate(final String sql) throws SQLException {
        try {
            return original.executeUpdate(sql);
        } catch (final SQLException e) {
            throw wrapException(e, sql);
        }
    }

    public int getMaxFieldSize() throws SQLException {
        try {
            return original.getMaxFieldSize();
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setArray(final int i, final Array x) throws SQLException {
        try {
            original.setArray(i, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setAsciiStream(final int parameterIndex, final InputStream x,
            final int length) throws SQLException {
        try {
            original.setAsciiStream(parameterIndex, x, length);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBigDecimal(final int parameterIndex, final BigDecimal x)
            throws SQLException {
        try {
            original.setBigDecimal(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBinaryStream(final int parameterIndex, final InputStream x,
            final int length) throws SQLException {
        try {
            original.setBinaryStream(parameterIndex, x, length);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBlob(final int i, final Blob x) throws SQLException {
        try {
            original.setBlob(i, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBoolean(final int parameterIndex, final boolean x)
            throws SQLException {
        try {
            original.setBoolean(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setByte(final int parameterIndex, final byte x)
            throws SQLException {
        try {
            original.setByte(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setBytes(final int parameterIndex, final byte[] x)
            throws SQLException {
        try {
            original.setBytes(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setCharacterStream(final int parameterIndex,
            final Reader reader, final int length) throws SQLException {
        try {
            original.setCharacterStream(parameterIndex, reader, length);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setClob(final int i, final Clob x) throws SQLException {
        try {
            original.setClob(i, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setCursorName(final String name) throws SQLException {
        try {
            original.setCursorName(name);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setDate(final int parameterIndex, final Date x,
            final Calendar cal) throws SQLException {
        try {
            original.setDate(parameterIndex, x, cal);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setDate(final int parameterIndex, final Date x)
            throws SQLException {
        try {
            original.setDate(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setDouble(final int parameterIndex, final double x)
            throws SQLException {
        try {
            original.setDouble(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setEscapeProcessing(final boolean enable) throws SQLException {
        try {
            original.setEscapeProcessing(enable);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setFetchDirection(final int direction) throws SQLException {
        try {
            original.setFetchDirection(direction);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setFetchSize(final int rows) throws SQLException {
        try {
            original.setFetchSize(rows);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setFloat(final int parameterIndex, final float x)
            throws SQLException {
        try {
            original.setFloat(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setInt(final int parameterIndex, final int x)
            throws SQLException {
        try {
            original.setInt(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setLong(final int parameterIndex, final long x)
            throws SQLException {
        try {
            original.setLong(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setMaxFieldSize(final int max) throws SQLException {
        try {
            original.setMaxFieldSize(max);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setMaxRows(final int max) throws SQLException {
        try {
            original.setMaxRows(max);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setNull(final int paramIndex, final int sqlType,
            final String typeName) throws SQLException {
        try {
            original.setNull(paramIndex, sqlType, typeName);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setNull(final int parameterIndex, final int sqlType)
            throws SQLException {
        try {
            original.setNull(parameterIndex, sqlType);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setObject(final int parameterIndex, final Object x,
            final int targetSqlType, final int scale) throws SQLException {
        try {
            original.setObject(parameterIndex, x, targetSqlType, scale);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    /**
     * Sets the designated parameter to the given input stream, which will have
     * the specified number of bytes.
     * When a very large ASCII value is input to a <code>LONGVARCHAR</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.InputStream</code>. Data will be read from the stream
     * as needed until end-of-file is reached.  The JDBC driver will
     * do any necessary conversion from ASCII to the database char format.
     *
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x              the Java input stream that contains the ASCII parameter value
     * @param length         the number of bytes in the stream
     * @throws SQLException if parameterIndex does not correspond to a parameter
     *                      marker in the SQL statement; if a database access error occurs or
     *                      this method is called on a closed <code>PreparedStatement</code>
     * @since 1.6
     */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    /**
     * Sets the designated parameter to the given input stream, which will have
     * the specified number of bytes.
     * When a very large binary value is input to a <code>LONGVARBINARY</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.InputStream</code> object. The data will be read from the
     * stream as needed until end-of-file is reached.
     *
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x              the java input stream which contains the binary parameter value
     * @param length         the number of bytes in the stream
     * @throws SQLException if parameterIndex does not correspond to a parameter
     *                      marker in the SQL statement; if a database access error occurs or
     *                      this method is called on a closed <code>PreparedStatement</code>
     * @since 1.6
     */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    /**
     * Sets the designated parameter to the given <code>Reader</code>
     * object, which is the given number of characters long.
     * When a very large UNICODE value is input to a <code>LONGVARCHAR</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.Reader</code> object. The data will be read from the stream
     * as needed until end-of-file is reached.  The JDBC driver will
     * do any necessary conversion from UNICODE to the database char format.
     *
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param reader         the <code>java.io.Reader</code> object that contains the
     *                       Unicode data
     * @param length         the number of characters in the stream
     * @throws SQLException if parameterIndex does not correspond to a parameter
     *                      marker in the SQL statement; if a database access error occurs or
     *                      this method is called on a closed <code>PreparedStatement</code>
     * @since 1.6
     */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    /**
     * Sets the designated parameter to the given input stream.
     * When a very large ASCII value is input to a <code>LONGVARCHAR</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.InputStream</code>. Data will be read from the stream
     * as needed until end-of-file is reached.  The JDBC driver will
     * do any necessary conversion from ASCII to the database char format.
     *
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
     * it might be more efficient to use a version of
     * <code>setAsciiStream</code> which takes a length parameter.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x              the Java input stream that contains the ASCII parameter value
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if a database access error occurs or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

    }

    /**
     * Sets the designated parameter to the given input stream.
     * When a very large binary value is input to a <code>LONGVARBINARY</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.InputStream</code> object. The data will be read from the
     * stream as needed until end-of-file is reached.
     *
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
     * it might be more efficient to use a version of
     * <code>setBinaryStream</code> which takes a length parameter.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x              the java input stream which contains the binary parameter value
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if a database access error occurs or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

    }

    /**
     * Sets the designated parameter to the given <code>Reader</code>
     * object.
     * When a very large UNICODE value is input to a <code>LONGVARCHAR</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.Reader</code> object. The data will be read from the stream
     * as needed until end-of-file is reached.  The JDBC driver will
     * do any necessary conversion from UNICODE to the database char format.
     *
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
     * it might be more efficient to use a version of
     * <code>setCharacterStream</code> which takes a length parameter.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param reader         the <code>java.io.Reader</code> object that contains the
     *                       Unicode data
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if a database access error occurs or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

    }

    /**
     * Sets the designated parameter to a <code>Reader</code> object. The
     * <code>Reader</code> reads the data till end-of-file is reached. The
     * driver does the necessary conversion from Java character format to
     * the national character set in the database.
     *
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
     * it might be more efficient to use a version of
     * <code>setNCharacterStream</code> which takes a length parameter.
     *
     * @param parameterIndex of the first parameter is 1, the second is 2, ...
     * @param value          the parameter value
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if the driver does not support national
     *                                         character sets;  if the driver can detect that a data conversion
     *                                         error could occur; if a database access error occurs; or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

    }

    /**
     * Sets the designated parameter to a <code>Reader</code> object.
     * This method differs from the <code>setCharacterStream (int, Reader)</code> method
     * because it informs the driver that the parameter value should be sent to
     * the server as a <code>CLOB</code>.  When the <code>setCharacterStream</code> method is used, the
     * driver may have to do extra work to determine whether the parameter
     * data should be sent to the server as a <code>LONGVARCHAR</code> or a <code>CLOB</code>
     *
     * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
     * it might be more efficient to use a version of
     * <code>setClob</code> which takes a length parameter.
     *
     * @param parameterIndex index of the first parameter is 1, the second is 2, ...
     * @param reader         An object that contains the data to set the parameter value to.
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if a database access error occurs; this method is called on
     *                                         a closed <code>PreparedStatement</code>or if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {

    }

    /**
     * Sets the designated parameter to a <code>InputStream</code> object.
     * This method differs from the <code>setBinaryStream (int, InputStream)</code>
     * method because it informs the driver that the parameter value should be
     * sent to the server as a <code>BLOB</code>.  When the <code>setBinaryStream</code> method is used,
     * the driver may have to do extra work to determine whether the parameter
     * data should be sent to the server as a <code>LONGVARBINARY</code> or a <code>BLOB</code>
     *
     * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
     * it might be more efficient to use a version of
     * <code>setBlob</code> which takes a length parameter.
     *
     * @param parameterIndex index of the first parameter is 1,
     *                       the second is 2, ...
     * @param inputStream    An object that contains the data to set the parameter
     *                       value to.
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement; if a database access error occurs;
     *                                         this method is called on a closed <code>PreparedStatement</code> or
     *                                         if parameterIndex does not correspond
     *                                         to a parameter marker in the SQL statement,
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

    }

    /**
     * Sets the designated parameter to a <code>Reader</code> object.
     * This method differs from the <code>setCharacterStream (int, Reader)</code> method
     * because it informs the driver that the parameter value should be sent to
     * the server as a <code>NCLOB</code>.  When the <code>setCharacterStream</code> method is used, the
     * driver may have to do extra work to determine whether the parameter
     * data should be sent to the server as a <code>LONGNVARCHAR</code> or a <code>NCLOB</code>
     * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
     * it might be more efficient to use a version of
     * <code>setNClob</code> which takes a length parameter.
     *
     * @param parameterIndex index of the first parameter is 1, the second is 2, ...
     * @param reader         An object that contains the data to set the parameter value to.
     * @throws SQLException                    if parameterIndex does not correspond to a parameter
     *                                         marker in the SQL statement;
     *                                         if the driver does not support national character sets;
     *                                         if the driver can detect that a data conversion
     *                                         error could occur;  if a database access error occurs or
     *                                         this method is called on a closed <code>PreparedStatement</code>
     * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
     * @since 1.6
     */
    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {

    }

    public void setObject(final int parameterIndex, final Object x,
            final int targetSqlType) throws SQLException {
        try {
            original.setObject(parameterIndex, x, targetSqlType);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setObject(final int parameterIndex, final Object x)
            throws SQLException {
        try {
            original.setObject(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setQueryTimeout(final int seconds) throws SQLException {
        try {
            original.setQueryTimeout(seconds);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setRef(final int i, final Ref x) throws SQLException {
        try {
            original.setRef(i, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setShort(final int parameterIndex, final short x)
            throws SQLException {
        try {
            original.setShort(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setString(final int parameterIndex, final String x)
            throws SQLException {
        try {
            original.setString(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setTime(final int parameterIndex, final Time x,
            final Calendar cal) throws SQLException {
        try {
            original.setTime(parameterIndex, x, cal);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setTime(final int parameterIndex, final Time x)
            throws SQLException {
        try {
            original.setTime(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setTimestamp(final int parameterIndex, final Timestamp x,
            final Calendar cal) throws SQLException {
        try {
            original.setTimestamp(parameterIndex, x, cal);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setTimestamp(final int parameterIndex, final Timestamp x)
            throws SQLException {
        try {
            original.setTimestamp(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    /**
     * @deprecated
     */
    public void setUnicodeStream(final int parameterIndex, final InputStream x,
            final int length) throws SQLException {
        try {
            original.setUnicodeStream(parameterIndex, x, length);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public void setURL(final int parameterIndex, final URL x)
            throws SQLException {
        try {
            original.setURL(parameterIndex, x);
        } catch (final SQLException e) {
            throw wrapException(e);
        }
    }

    public String toString() {
        return original.toString();
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

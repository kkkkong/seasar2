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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.seasar.extension.jdbc.StatementFactory;

/**
 * @author manhole
 */
public class ConfigurableStatementFactoryTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testCunstructorWithNull() throws Exception {
        try {
            new ConfigurableStatementFactory(null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    /**
     * @throws Exception
     */
    public void testCreatePreparedStatement() throws Exception {
        // ## Arrange ##
        final Connection mockConnection = new MockConnection();
        final PreparedStatement mockPreparedStatement = new MockPreparedStatement();

        ConfigurableStatementFactory statementFactory = new ConfigurableStatementFactory(
                new MockStatementFactory() {

                    public PreparedStatement createPreparedStatement(
                            Connection con, String sql) {
                        assertSame(mockConnection, con);
                        assertEquals("some sql", sql);
                        return mockPreparedStatement;
                    }
                });

        // ## Act ##
        PreparedStatement preparedStatement = statementFactory
                .createPreparedStatement(mockConnection, "some sql");

        // ## Assert ##
        assertSame(mockPreparedStatement, preparedStatement);
    }

    /**
     * @throws Exception
     */
    public void testCreateCallableStatement() throws Exception {
        // ## Arrange ##
        final Connection mockConnection = new MockConnection();
        final CallableStatement mockCallableStatement = new MockCallableStatement();

        ConfigurableStatementFactory statementFactory = new ConfigurableStatementFactory(
                new MockStatementFactory() {

                    public CallableStatement createCallableStatement(
                            Connection con, String sql) {
                        assertSame(mockConnection, con);
                        assertEquals("some sql", sql);
                        return mockCallableStatement;
                    }

                });

        // ## Act ##
        CallableStatement preparedStatement = statementFactory
                .createCallableStatement(mockConnection, "some sql");

        // ## Assert ##
        assertSame(mockCallableStatement, preparedStatement);
    }

    /**
     * @throws Exception
     */
    public void testConfigurePreparedStatement() throws Exception {
        // ## Arrange ##
        final int[] fetchSize = new int[1];
        final int[] maxRows = new int[1];
        final int[] queryTimeout = new int[1];
        final PreparedStatement mockPreparedStatement = new MockPreparedStatement() {
            public void setFetchSize(int arg0) throws SQLException {
                fetchSize[0] = arg0;
            }

            public void setMaxRows(int arg0) throws SQLException {
                maxRows[0] = arg0;
            }

            public void setQueryTimeout(int arg0) throws SQLException {
                queryTimeout[0] = arg0;
            }
        };

        ConfigurableStatementFactory statementFactory = new ConfigurableStatementFactory(
                new MockStatementFactory() {

                    public PreparedStatement createPreparedStatement(
                            Connection con, String sql) {
                        return mockPreparedStatement;
                    }

                });

        statementFactory.setFetchSize(new Integer(123));
        statementFactory.setMaxRows(new Integer(221));
        statementFactory.setQueryTimeout(new Integer(321));

        // ## Act ##
        statementFactory.createPreparedStatement(new MockConnection(),
                "select ...");

        // ## Assert ##
        assertEquals(123, fetchSize[0]);
        assertEquals(221, maxRows[0]);
        assertEquals(321, queryTimeout[0]);
    }

    /**
     * @throws Exception
     */
    public void testConfigureCallableStatement() throws Exception {
        // ## Arrange ##
        final int[] fetchSize = new int[1];
        final int[] maxRows = new int[1];
        final CallableStatement mockCallableStatement = new MockCallableStatement() {
            public void setFetchSize(int arg0) throws SQLException {
                fetchSize[0] = arg0;
            }

            public void setMaxRows(int arg0) throws SQLException {
                maxRows[0] = arg0;
            }
        };

        ConfigurableStatementFactory statementFactory = new ConfigurableStatementFactory(
                new MockStatementFactory() {

                    public CallableStatement createCallableStatement(
                            Connection con, String sql) {
                        return mockCallableStatement;
                    }

                });

        statementFactory.setFetchSize(new Integer(1123));
        statementFactory.setMaxRows(new Integer(5535));

        // ## Act ##
        statementFactory.createCallableStatement(new MockConnection(),
                "select ...");

        // ## Assert ##
        assertEquals(1123, fetchSize[0]);
        assertEquals(5535, maxRows[0]);
    }

    private static class MockStatementFactory implements StatementFactory {

        public PreparedStatement createPreparedStatement(Connection con,
                String sql) {
            throw new AssertionFailedError();
        }

        public CallableStatement createCallableStatement(Connection con,
                String sql) {
            throw new AssertionFailedError();
        }
    }

    private static class MockPreparedStatement implements PreparedStatement {

        public ResultSet executeQuery() throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setNull(int parameterIndex, int sqlType)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBoolean(int parameterIndex, boolean x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setByte(int parameterIndex, byte x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setShort(int parameterIndex, short x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setInt(int parameterIndex, int x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setLong(int parameterIndex, long x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setFloat(int parameterIndex, float x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setDouble(int parameterIndex, double x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBigDecimal(int parameterIndex, BigDecimal x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setString(int parameterIndex, String x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBytes(int parameterIndex, byte[] x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setDate(int parameterIndex, Date x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTime(int parameterIndex, Time x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTimestamp(int parameterIndex, Timestamp x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setAsciiStream(int parameterIndex, InputStream x, int length)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setUnicodeStream(int parameterIndex, InputStream x,
                int length) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBinaryStream(int parameterIndex, InputStream x,
                int length) throws SQLException {
            throw new AssertionFailedError();
        }

        public void clearParameters() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setObject(int parameterIndex, Object x, int targetSqlType,
                int scale) throws SQLException {
            throw new AssertionFailedError();
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

        public void setObject(int parameterIndex, Object x, int targetSqlType)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setObject(int parameterIndex, Object x) throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute() throws SQLException {
            throw new AssertionFailedError();
        }

        public void addBatch() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setCharacterStream(int parameterIndex, Reader reader,
                int length) throws SQLException {
            throw new AssertionFailedError();

        }

        public void setRef(int i, Ref x) throws SQLException {
            throw new AssertionFailedError();

        }

        public void setBlob(int i, Blob x) throws SQLException {
            throw new AssertionFailedError();

        }

        public void setClob(int i, Clob x) throws SQLException {
            throw new AssertionFailedError();

        }

        public void setArray(int i, Array x) throws SQLException {
            throw new AssertionFailedError();

        }

        public ResultSetMetaData getMetaData() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setDate(int parameterIndex, Date x, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();

        }

        public void setTime(int parameterIndex, Time x, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();

        }

        public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();

        }

        public void setNull(int paramIndex, int sqlType, String typeName)
                throws SQLException {
            throw new AssertionFailedError();

        }

        public void setURL(int parameterIndex, URL x) throws SQLException {
            throw new AssertionFailedError();

        }

        public ParameterMetaData getParameterMetaData() throws SQLException {
            throw new AssertionFailedError();
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

        public ResultSet executeQuery(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public void close() throws SQLException {
            throw new AssertionFailedError();
        }

        public int getMaxFieldSize() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setMaxFieldSize(int max) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getMaxRows() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setMaxRows(int max) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setEscapeProcessing(boolean enable) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getQueryTimeout() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setQueryTimeout(int seconds) throws SQLException {
            throw new AssertionFailedError();
        }

        public void cancel() throws SQLException {
            throw new AssertionFailedError();
        }

        public SQLWarning getWarnings() throws SQLException {
            throw new AssertionFailedError();
        }

        public void clearWarnings() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setCursorName(String name) throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public ResultSet getResultSet() throws SQLException {
            throw new AssertionFailedError();
        }

        public int getUpdateCount() throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean getMoreResults() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setFetchDirection(int direction) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getFetchDirection() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setFetchSize(int rows) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getFetchSize() throws SQLException {
            throw new AssertionFailedError();
        }

        public int getResultSetConcurrency() throws SQLException {
            throw new AssertionFailedError();
        }

        public int getResultSetType() throws SQLException {
            throw new AssertionFailedError();
        }

        public void addBatch(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public void clearBatch() throws SQLException {
            throw new AssertionFailedError();
        }

        public int[] executeBatch() throws SQLException {
            throw new AssertionFailedError();
        }

        public Connection getConnection() throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean getMoreResults(int current) throws SQLException {
            throw new AssertionFailedError();
        }

        public ResultSet getGeneratedKeys() throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate(String sql, int autoGeneratedKeys)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate(String sql, int[] columnIndexes)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate(String sql, String[] columnNames)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute(String sql, int autoGeneratedKeys)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute(String sql, int[] columnIndexes)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute(String sql, String[] columnNames)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public int getResultSetHoldability() throws SQLException {
            throw new AssertionFailedError();
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

    private static class MockConnection implements Connection {

        public Statement createStatement() throws SQLException {
            throw new AssertionFailedError();
        }

        public PreparedStatement prepareStatement(String sql)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public CallableStatement prepareCall(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public String nativeSQL(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setAutoCommit(boolean autoCommit) throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean getAutoCommit() throws SQLException {
            throw new AssertionFailedError();
        }

        public void commit() throws SQLException {
            throw new AssertionFailedError();
        }

        public void rollback() throws SQLException {
            throw new AssertionFailedError();
        }

        public void close() throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean isClosed() throws SQLException {
            throw new AssertionFailedError();
        }

        public DatabaseMetaData getMetaData() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setReadOnly(boolean readOnly) throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean isReadOnly() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setCatalog(String catalog) throws SQLException {
            throw new AssertionFailedError();
        }

        public String getCatalog() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTransactionIsolation(int level) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getTransactionIsolation() throws SQLException {
            throw new AssertionFailedError();
        }

        public SQLWarning getWarnings() throws SQLException {
            throw new AssertionFailedError();
        }

        public void clearWarnings() throws SQLException {
            throw new AssertionFailedError();
        }

        public Statement createStatement(int resultSetType,
                int resultSetConcurrency) throws SQLException {
            throw new AssertionFailedError();
        }

        public PreparedStatement prepareStatement(String sql,
                int resultSetType, int resultSetConcurrency)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public CallableStatement prepareCall(String sql, int resultSetType,
                int resultSetConcurrency) throws SQLException {
            throw new AssertionFailedError();
        }

        public Map getTypeMap() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTypeMap(Map arg0) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setHoldability(int holdability) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getHoldability() throws SQLException {
            throw new AssertionFailedError();
        }

        public Savepoint setSavepoint() throws SQLException {
            throw new AssertionFailedError();
        }

        public Savepoint setSavepoint(String name) throws SQLException {
            throw new AssertionFailedError();
        }

        public void rollback(Savepoint savepoint) throws SQLException {
            throw new AssertionFailedError();
        }

        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            throw new AssertionFailedError();
        }

        public Statement createStatement(int resultSetType,
                int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public PreparedStatement prepareStatement(String sql,
                int resultSetType, int resultSetConcurrency,
                int resultSetHoldability) throws SQLException {
            throw new AssertionFailedError();
        }

        public CallableStatement prepareCall(String sql, int resultSetType,
                int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public PreparedStatement prepareStatement(String sql,
                int autoGeneratedKeys) throws SQLException {
            throw new AssertionFailedError();
        }

        public PreparedStatement prepareStatement(String sql,
                int[] columnIndexes) throws SQLException {
            throw new AssertionFailedError();
        }

        public PreparedStatement prepareStatement(String sql,
                String[] columnNames) throws SQLException {
            throw new AssertionFailedError();
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

    private static class MockCallableStatement implements CallableStatement {

        public void registerOutParameter(int parameterIndex, int sqlType)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void registerOutParameter(int parameterIndex, int sqlType,
                int scale) throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean wasNull() throws SQLException {
            throw new AssertionFailedError();
        }

        public String getString(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean getBoolean(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public byte getByte(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public short getShort(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getInt(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public long getLong(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public float getFloat(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public double getDouble(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public BigDecimal getBigDecimal(int parameterIndex, int scale)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public byte[] getBytes(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public Date getDate(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public Time getTime(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public Timestamp getTimestamp(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public Object getObject(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public Object getObject(int arg0, Map arg1) throws SQLException {
            throw new AssertionFailedError();
        }

        public Ref getRef(int i) throws SQLException {
            throw new AssertionFailedError();
        }

        public Blob getBlob(int i) throws SQLException {
            throw new AssertionFailedError();
        }

        public Clob getClob(int i) throws SQLException {
            throw new AssertionFailedError();
        }

        public Array getArray(int i) throws SQLException {
            throw new AssertionFailedError();
        }

        public Date getDate(int parameterIndex, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public Time getTime(int parameterIndex, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public Timestamp getTimestamp(int parameterIndex, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void registerOutParameter(int paramIndex, int sqlType,
                String typeName) throws SQLException {
            throw new AssertionFailedError();
        }

        public void registerOutParameter(String parameterName, int sqlType)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void registerOutParameter(String parameterName, int sqlType,
                int scale) throws SQLException {
            throw new AssertionFailedError();
        }

        public void registerOutParameter(String parameterName, int sqlType,
                String typeName) throws SQLException {
            throw new AssertionFailedError();
        }

        public URL getURL(int parameterIndex) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setURL(String parameterName, URL val) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setNull(String parameterName, int sqlType)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBoolean(String parameterName, boolean x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setByte(String parameterName, byte x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setShort(String parameterName, short x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setInt(String parameterName, int x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setLong(String parameterName, long x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setFloat(String parameterName, float x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setDouble(String parameterName, double x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBigDecimal(String parameterName, BigDecimal x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setString(String parameterName, String x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBytes(String parameterName, byte[] x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setDate(String parameterName, Date x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTime(String parameterName, Time x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTimestamp(String parameterName, Timestamp x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setAsciiStream(String parameterName, InputStream x,
                int length) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBinaryStream(String parameterName, InputStream x,
                int length) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setObject(String parameterName, Object x,
                int targetSqlType, int scale) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setObject(String parameterName, Object x, int targetSqlType)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setObject(String parameterName, Object x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setCharacterStream(String parameterName, Reader reader,
                int length) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setDate(String parameterName, Date x, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTime(String parameterName, Time x, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setNull(String parameterName, int sqlType, String typeName)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public String getString(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean getBoolean(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public byte getByte(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public short getShort(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getInt(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public long getLong(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public float getFloat(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public double getDouble(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public byte[] getBytes(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public Date getDate(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public Time getTime(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public Timestamp getTimestamp(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public Object getObject(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public BigDecimal getBigDecimal(String parameterName)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public Object getObject(String arg0, Map arg1) throws SQLException {
            throw new AssertionFailedError();
        }

        public Ref getRef(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public Blob getBlob(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public Clob getClob(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public Array getArray(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        public Date getDate(String parameterName, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public Time getTime(String parameterName, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public Timestamp getTimestamp(String parameterName, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public URL getURL(String parameterName) throws SQLException {
            throw new AssertionFailedError();
        }

        /**
         * Retrieves the value of the designated JDBC <code>ROWID</code> parameter as a
         * <code>java.sql.RowId</code> object.
         *
         * @param parameterIndex the first parameter is 1, the second is 2,...
         * @return a <code>RowId</code> object that represents the JDBC <code>ROWID</code>
         * value is used as the designated parameter. If the parameter contains
         * a SQL <code>NULL</code>, then a <code>null</code> value is returned.
         * @throws SQLException                    if the parameterIndex is not valid;
         *                                         if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public RowId getRowId(int parameterIndex) throws SQLException {
            return null;
        }

        /**
         * Retrieves the value of the designated JDBC <code>ROWID</code> parameter as a
         * <code>java.sql.RowId</code> object.
         *
         * @param parameterName the name of the parameter
         * @return a <code>RowId</code> object that represents the JDBC <code>ROWID</code>
         * value is used as the designated parameter. If the parameter contains
         * a SQL <code>NULL</code>, then a <code>null</code> value is returned.
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public RowId getRowId(String parameterName) throws SQLException {
            return null;
        }

        /**
         * Sets the designated parameter to the given <code>java.sql.RowId</code> object. The
         * driver converts this to a SQL <code>ROWID</code> when it sends it to the
         * database.
         *
         * @param parameterName the name of the parameter
         * @param x             the parameter value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setRowId(String parameterName, RowId x) throws SQLException {

        }

        /**
         * Sets the designated parameter to the given <code>String</code> object.
         * The driver converts this to a SQL <code>NCHAR</code> or
         * <code>NVARCHAR</code> or <code>LONGNVARCHAR</code>
         *
         * @param parameterName the name of the parameter to be set
         * @param value         the parameter value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if the driver does not support national
         *                                         character sets;  if the driver can detect that a data conversion
         *                                         error could occur; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setNString(String parameterName, String value) throws SQLException {

        }

        /**
         * Sets the designated parameter to a <code>Reader</code> object. The
         * <code>Reader</code> reads the data till end-of-file is reached. The
         * driver does the necessary conversion from Java character format to
         * the national character set in the database.
         *
         * @param parameterName the name of the parameter to be set
         * @param value         the parameter value
         * @param length        the number of characters in the parameter data.
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if the driver does not support national
         *                                         character sets;  if the driver can detect that a data conversion
         *                                         error could occur; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {

        }

        /**
         * Sets the designated parameter to a <code>java.sql.NClob</code> object. The object
         * implements the <code>java.sql.NClob</code> interface. This <code>NClob</code>
         * object maps to a SQL <code>NCLOB</code>.
         *
         * @param parameterName the name of the parameter to be set
         * @param value         the parameter value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if the driver does not support national
         *                                         character sets;  if the driver can detect that a data conversion
         *                                         error could occur; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setNClob(String parameterName, NClob value) throws SQLException {

        }

        /**
         * Sets the designated parameter to a <code>Reader</code> object.  The <code>reader</code> must contain  the number
         * of characters specified by length otherwise a <code>SQLException</code> will be
         * generated when the <code>CallableStatement</code> is executed.
         * This method differs from the <code>setCharacterStream (int, Reader, int)</code> method
         * because it informs the driver that the parameter value should be sent to
         * the server as a <code>CLOB</code>.  When the <code>setCharacterStream</code> method is used, the
         * driver may have to do extra work to determine whether the parameter
         * data should be send to the server as a <code>LONGVARCHAR</code> or a <code>CLOB</code>
         *
         * @param parameterName the name of the parameter to be set
         * @param reader        An object that contains the data to set the parameter value to.
         * @param length        the number of characters in the parameter data.
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if the length specified is less than zero;
         *                                         a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setClob(String parameterName, Reader reader, long length) throws SQLException {

        }

        /**
         * Sets the designated parameter to a <code>InputStream</code> object.  The <code>inputstream</code> must contain  the number
         * of characters specified by length, otherwise a <code>SQLException</code> will be
         * generated when the <code>CallableStatement</code> is executed.
         * This method differs from the <code>setBinaryStream (int, InputStream, int)</code>
         * method because it informs the driver that the parameter value should be
         * sent to the server as a <code>BLOB</code>.  When the <code>setBinaryStream</code> method is used,
         * the driver may have to do extra work to determine whether the parameter
         * data should be sent to the server as a <code>LONGVARBINARY</code> or a <code>BLOB</code>
         *
         * @param parameterName the name of the parameter to be set
         *                      the second is 2, ...
         * @param inputStream   An object that contains the data to set the parameter
         *                      value to.
         * @param length        the number of bytes in the parameter data.
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if the length specified
         *                                         is less than zero; if the number of bytes in the inputstream does not match
         *                                         the specified length; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {

        }

        /**
         * Sets the designated parameter to a <code>Reader</code> object.  The <code>reader</code> must contain  the number
         * of characters specified by length otherwise a <code>SQLException</code> will be
         * generated when the <code>CallableStatement</code> is executed.
         * This method differs from the <code>setCharacterStream (int, Reader, int)</code> method
         * because it informs the driver that the parameter value should be sent to
         * the server as a <code>NCLOB</code>.  When the <code>setCharacterStream</code> method is used, the
         * driver may have to do extra work to determine whether the parameter
         * data should be send to the server as a <code>LONGNVARCHAR</code> or a <code>NCLOB</code>
         *
         * @param parameterName the name of the parameter to be set
         * @param reader        An object that contains the data to set the parameter value to.
         * @param length        the number of characters in the parameter data.
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if the length specified is less than zero;
         *                                         if the driver does not support national
         *                                         character sets;  if the driver can detect that a data conversion
         *                                         error could occur; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setNClob(String parameterName, Reader reader, long length) throws SQLException {

        }

        /**
         * Retrieves the value of the designated JDBC <code>NCLOB</code> parameter as a
         * <code>java.sql.NClob</code> object in the Java programming language.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, and
         *                       so on
         * @return the parameter value as a <code>NClob</code> object in the
         * Java programming language.  If the value was SQL <code>NULL</code>, the
         * value <code>null</code> is returned.
         * @throws SQLException                    if the parameterIndex is not valid;
         *                                         if the driver does not support national
         *                                         character sets;  if the driver can detect that a data conversion
         *                                         error could occur; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public NClob getNClob(int parameterIndex) throws SQLException {
            return null;
        }

        /**
         * Retrieves the value of a JDBC <code>NCLOB</code> parameter as a
         * <code>java.sql.NClob</code> object in the Java programming language.
         *
         * @param parameterName the name of the parameter
         * @return the parameter value as a <code>NClob</code> object in the
         * Java programming language.  If the value was SQL <code>NULL</code>,
         * the value <code>null</code> is returned.
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if the driver does not support national
         *                                         character sets;  if the driver can detect that a data conversion
         *                                         error could occur; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public NClob getNClob(String parameterName) throws SQLException {
            return null;
        }

        /**
         * Sets the designated parameter to the given <code>java.sql.SQLXML</code> object. The driver converts this to an
         * <code>SQL XML</code> value when it sends it to the database.
         *
         * @param parameterName the name of the parameter
         * @param xmlObject     a <code>SQLXML</code> object that maps an <code>SQL XML</code> value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs;
         *                                         this method is called on a closed <code>CallableStatement</code> or
         *                                         the <code>java.xml.transform.Result</code>,
         *                                         <code>Writer</code> or <code>OutputStream</code> has not been closed for the <code>SQLXML</code> object
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {

        }

        /**
         * Retrieves the value of the designated <code>SQL XML</code> parameter as a
         * <code>java.sql.SQLXML</code> object in the Java programming language.
         *
         * @param parameterIndex index of the first parameter is 1, the second is 2, ...
         * @return a <code>SQLXML</code> object that maps an <code>SQL XML</code> value
         * @throws SQLException                    if the parameterIndex is not valid;
         *                                         if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public SQLXML getSQLXML(int parameterIndex) throws SQLException {
            return null;
        }

        /**
         * Retrieves the value of the designated <code>SQL XML</code> parameter as a
         * <code>java.sql.SQLXML</code> object in the Java programming language.
         *
         * @param parameterName the name of the parameter
         * @return a <code>SQLXML</code> object that maps an <code>SQL XML</code> value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public SQLXML getSQLXML(String parameterName) throws SQLException {
            return null;
        }

        /**
         * Retrieves the value of the designated <code>NCHAR</code>,
         * <code>NVARCHAR</code>
         * or <code>LONGNVARCHAR</code> parameter as
         * a <code>String</code> in the Java programming language.
         * <p>
         * For the fixed-length type JDBC <code>NCHAR</code>,
         * the <code>String</code> object
         * returned has exactly the same value the SQL
         * <code>NCHAR</code> value had in the
         * database, including any padding added by the database.
         *
         * @param parameterIndex index of the first parameter is 1, the second is 2, ...
         * @return a <code>String</code> object that maps an
         * <code>NCHAR</code>, <code>NVARCHAR</code> or <code>LONGNVARCHAR</code> value
         * @throws SQLException                    if the parameterIndex is not valid;
         *                                         if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @see #setNString
         * @since 1.6
         */
        @Override
        public String getNString(int parameterIndex) throws SQLException {
            return "";
        }

        /**
         * Retrieves the value of the designated <code>NCHAR</code>,
         * <code>NVARCHAR</code>
         * or <code>LONGNVARCHAR</code> parameter as
         * a <code>String</code> in the Java programming language.
         * <p>
         * For the fixed-length type JDBC <code>NCHAR</code>,
         * the <code>String</code> object
         * returned has exactly the same value the SQL
         * <code>NCHAR</code> value had in the
         * database, including any padding added by the database.
         *
         * @param parameterName the name of the parameter
         * @return a <code>String</code> object that maps an
         * <code>NCHAR</code>, <code>NVARCHAR</code> or <code>LONGNVARCHAR</code> value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter;
         *                                         if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @see #setNString
         * @since 1.6
         */
        @Override
        public String getNString(String parameterName) throws SQLException {
            return "";
        }

        /**
         * Retrieves the value of the designated parameter as a
         * <code>java.io.Reader</code> object in the Java programming language.
         * It is intended for use when
         * accessing  <code>NCHAR</code>,<code>NVARCHAR</code>
         * and <code>LONGNVARCHAR</code> parameters.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @return a <code>java.io.Reader</code> object that contains the parameter
         * value; if the value is SQL <code>NULL</code>, the value returned is
         * <code>null</code> in the Java programming language.
         * @throws SQLException                    if the parameterIndex is not valid;
         *                                         if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public Reader getNCharacterStream(int parameterIndex) throws SQLException {
            return null;
        }

        /**
         * Retrieves the value of the designated parameter as a
         * <code>java.io.Reader</code> object in the Java programming language.
         * It is intended for use when
         * accessing  <code>NCHAR</code>,<code>NVARCHAR</code>
         * and <code>LONGNVARCHAR</code> parameters.
         *
         * @param parameterName the name of the parameter
         * @return a <code>java.io.Reader</code> object that contains the parameter
         * value; if the value is SQL <code>NULL</code>, the value returned is
         * <code>null</code> in the Java programming language
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public Reader getNCharacterStream(String parameterName) throws SQLException {
            return null;
        }

        /**
         * Retrieves the value of the designated parameter as a
         * <code>java.io.Reader</code> object in the Java programming language.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, ...
         * @return a <code>java.io.Reader</code> object that contains the parameter
         * value; if the value is SQL <code>NULL</code>, the value returned is
         * <code>null</code> in the Java programming language.
         * @throws SQLException if the parameterIndex is not valid; if a database access error occurs or
         *                      this method is called on a closed <code>CallableStatement</code>
         * @since 1.6
         */
        @Override
        public Reader getCharacterStream(int parameterIndex) throws SQLException {
            return null;
        }

        /**
         * Retrieves the value of the designated parameter as a
         * <code>java.io.Reader</code> object in the Java programming language.
         *
         * @param parameterName the name of the parameter
         * @return a <code>java.io.Reader</code> object that contains the parameter
         * value; if the value is SQL <code>NULL</code>, the value returned is
         * <code>null</code> in the Java programming language
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public Reader getCharacterStream(String parameterName) throws SQLException {
            return null;
        }

        /**
         * Sets the designated parameter to the given <code>java.sql.Blob</code> object.
         * The driver converts this to an SQL <code>BLOB</code> value when it
         * sends it to the database.
         *
         * @param parameterName the name of the parameter
         * @param x             a <code>Blob</code> object that maps an SQL <code>BLOB</code> value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setBlob(String parameterName, Blob x) throws SQLException {

        }

        /**
         * Sets the designated parameter to the given <code>java.sql.Clob</code> object.
         * The driver converts this to an SQL <code>CLOB</code> value when it
         * sends it to the database.
         *
         * @param parameterName the name of the parameter
         * @param x             a <code>Clob</code> object that maps an SQL <code>CLOB</code> value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setClob(String parameterName, Clob x) throws SQLException {

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
         * @param parameterName the name of the parameter
         * @param x             the Java input stream that contains the ASCII parameter value
         * @param length        the number of bytes in the stream
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {

        }

        /**
         * Sets the designated parameter to the given input stream, which will have
         * the specified number of bytes.
         * When a very large binary value is input to a <code>LONGVARBINARY</code>
         * parameter, it may be more practical to send it via a
         * <code>java.io.InputStream</code> object. The data will be read from the stream
         * as needed until end-of-file is reached.
         *
         * <P><B>Note:</B> This stream object can either be a standard
         * Java stream object or your own subclass that implements the
         * standard interface.
         *
         * @param parameterName the name of the parameter
         * @param x             the java input stream which contains the binary parameter value
         * @param length        the number of bytes in the stream
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {

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
         * @param parameterName the name of the parameter
         * @param reader        the <code>java.io.Reader</code> object that
         *                      contains the UNICODE data used as the designated parameter
         * @param length        the number of characters in the stream
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.6
         */
        @Override
        public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {

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
         * @param parameterName the name of the parameter
         * @param x             the Java input stream that contains the ASCII parameter value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
         * @since 1.6
         */
        @Override
        public void setAsciiStream(String parameterName, InputStream x) throws SQLException {

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
         * @param parameterName the name of the parameter
         * @param x             the java input stream which contains the binary parameter value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
         * @since 1.6
         */
        @Override
        public void setBinaryStream(String parameterName, InputStream x) throws SQLException {

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
         * @param parameterName the name of the parameter
         * @param reader        the <code>java.io.Reader</code> object that contains the
         *                      Unicode data
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
         * @since 1.6
         */
        @Override
        public void setCharacterStream(String parameterName, Reader reader) throws SQLException {

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
         * @param parameterName the name of the parameter
         * @param value         the parameter value
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if the driver does not support national
         *                                         character sets;  if the driver can detect that a data conversion
         *                                         error could occur; if a database access error occurs; or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
         * @since 1.6
         */
        @Override
        public void setNCharacterStream(String parameterName, Reader value) throws SQLException {

        }

        /**
         * Sets the designated parameter to a <code>Reader</code> object.
         * This method differs from the <code>setCharacterStream (int, Reader)</code> method
         * because it informs the driver that the parameter value should be sent to
         * the server as a <code>CLOB</code>.  When the <code>setCharacterStream</code> method is used, the
         * driver may have to do extra work to determine whether the parameter
         * data should be send to the server as a <code>LONGVARCHAR</code> or a <code>CLOB</code>
         *
         * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
         * it might be more efficient to use a version of
         * <code>setClob</code> which takes a length parameter.
         *
         * @param parameterName the name of the parameter
         * @param reader        An object that contains the data to set the parameter value to.
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or this method is called on
         *                                         a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
         * @since 1.6
         */
        @Override
        public void setClob(String parameterName, Reader reader) throws SQLException {

        }

        /**
         * Sets the designated parameter to a <code>InputStream</code> object.
         * This method differs from the <code>setBinaryStream (int, InputStream)</code>
         * method because it informs the driver that the parameter value should be
         * sent to the server as a <code>BLOB</code>.  When the <code>setBinaryStream</code> method is used,
         * the driver may have to do extra work to determine whether the parameter
         * data should be send to the server as a <code>LONGVARBINARY</code> or a <code>BLOB</code>
         *
         * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
         * it might be more efficient to use a version of
         * <code>setBlob</code> which takes a length parameter.
         *
         * @param parameterName the name of the parameter
         * @param inputStream   An object that contains the data to set the parameter
         *                      value to.
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
         * @since 1.6
         */
        @Override
        public void setBlob(String parameterName, InputStream inputStream) throws SQLException {

        }

        /**
         * Sets the designated parameter to a <code>Reader</code> object.
         * This method differs from the <code>setCharacterStream (int, Reader)</code> method
         * because it informs the driver that the parameter value should be sent to
         * the server as a <code>NCLOB</code>.  When the <code>setCharacterStream</code> method is used, the
         * driver may have to do extra work to determine whether the parameter
         * data should be send to the server as a <code>LONGNVARCHAR</code> or a <code>NCLOB</code>
         * <P><B>Note:</B> Consult your JDBC driver documentation to determine if
         * it might be more efficient to use a version of
         * <code>setNClob</code> which takes a length parameter.
         *
         * @param parameterName the name of the parameter
         * @param reader        An object that contains the data to set the parameter value to.
         * @throws SQLException                    if parameterName does not correspond to a named
         *                                         parameter; if the driver does not support national character sets;
         *                                         if the driver can detect that a data conversion
         *                                         error could occur;  if a database access error occurs or
         *                                         this method is called on a closed <code>CallableStatement</code>
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support this method
         * @since 1.6
         */
        @Override
        public void setNClob(String parameterName, Reader reader) throws SQLException {

        }

        /**
         * <p>Returns an object representing the value of OUT parameter
         * {@code parameterIndex} and will convert from the
         * SQL type of the parameter to the requested Java data type, if the
         * conversion is supported. If the conversion is not
         * supported or null is specified for the type, a
         * <code>SQLException</code> is thrown.
         * <p>
         * At a minimum, an implementation must support the conversions defined in
         * Appendix B, Table B-3 and conversion of appropriate user defined SQL
         * types to a Java type which implements {@code SQLData}, or {@code Struct}.
         * Additional conversions may be supported and are vendor defined.
         *
         * @param parameterIndex the first parameter is 1, the second is 2, and so on
         * @param type           Class representing the Java data type to convert the
         *                       designated parameter to.
         * @return an instance of {@code type} holding the OUT parameter value
         * @throws SQLException                    if conversion is not supported, type is null or
         *                                         another error occurs. The getCause() method of the
         *                                         exception may provide a more detailed exception, for example, if
         *                                         a conversion error occurs
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.7
         */
        @Override
        public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
            return null;
        }

        /**
         * <p>Returns an object representing the value of OUT parameter
         * {@code parameterName} and will convert from the
         * SQL type of the parameter to the requested Java data type, if the
         * conversion is supported. If the conversion is not
         * supported  or null is specified for the type, a
         * <code>SQLException</code> is thrown.
         * <p>
         * At a minimum, an implementation must support the conversions defined in
         * Appendix B, Table B-3 and conversion of appropriate user defined SQL
         * types to a Java type which implements {@code SQLData}, or {@code Struct}.
         * Additional conversions may be supported and are vendor defined.
         *
         * @param parameterName the name of the parameter
         * @param type          Class representing the Java data type to convert
         *                      the designated parameter to.
         * @return an instance of {@code type} holding the OUT parameter
         * value
         * @throws SQLException                    if conversion is not supported, type is null or
         *                                         another error occurs. The getCause() method of the
         *                                         exception may provide a more detailed exception, for example, if
         *                                         a conversion error occurs
         * @throws SQLFeatureNotSupportedException if the JDBC driver does not support
         *                                         this method
         * @since 1.7
         */
        @Override
        public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
            return null;
        }

        public ResultSet executeQuery() throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setNull(int parameterIndex, int sqlType)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBoolean(int parameterIndex, boolean x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setByte(int parameterIndex, byte x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setShort(int parameterIndex, short x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setInt(int parameterIndex, int x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setLong(int parameterIndex, long x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setFloat(int parameterIndex, float x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setDouble(int parameterIndex, double x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBigDecimal(int parameterIndex, BigDecimal x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setString(int parameterIndex, String x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBytes(int parameterIndex, byte[] x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setDate(int parameterIndex, Date x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTime(int parameterIndex, Time x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTimestamp(int parameterIndex, Timestamp x)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setAsciiStream(int parameterIndex, InputStream x, int length)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setUnicodeStream(int parameterIndex, InputStream x,
                int length) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBinaryStream(int parameterIndex, InputStream x,
                int length) throws SQLException {
            throw new AssertionFailedError();
        }

        public void clearParameters() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setObject(int parameterIndex, Object x, int targetSqlType,
                int scale) throws SQLException {
            throw new AssertionFailedError();
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

        public void setObject(int parameterIndex, Object x, int targetSqlType)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setObject(int parameterIndex, Object x) throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute() throws SQLException {
            throw new AssertionFailedError();
        }

        public void addBatch() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setCharacterStream(int parameterIndex, Reader reader,
                int length) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setRef(int i, Ref x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setBlob(int i, Blob x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setClob(int i, Clob x) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setArray(int i, Array x) throws SQLException {
            throw new AssertionFailedError();
        }

        public ResultSetMetaData getMetaData() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setDate(int parameterIndex, Date x, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTime(int parameterIndex, Time x, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setNull(int paramIndex, int sqlType, String typeName)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public void setURL(int parameterIndex, URL x) throws SQLException {
            throw new AssertionFailedError();
        }

        public ParameterMetaData getParameterMetaData() throws SQLException {
            throw new AssertionFailedError();
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

        public ResultSet executeQuery(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public void close() throws SQLException {
            throw new AssertionFailedError();
        }

        public int getMaxFieldSize() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setMaxFieldSize(int max) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getMaxRows() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setMaxRows(int max) throws SQLException {
            throw new AssertionFailedError();
        }

        public void setEscapeProcessing(boolean enable) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getQueryTimeout() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setQueryTimeout(int seconds) throws SQLException {
            throw new AssertionFailedError();
        }

        public void cancel() throws SQLException {
            throw new AssertionFailedError();
        }

        public SQLWarning getWarnings() throws SQLException {
            throw new AssertionFailedError();
        }

        public void clearWarnings() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setCursorName(String name) throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public ResultSet getResultSet() throws SQLException {
            throw new AssertionFailedError();
        }

        public int getUpdateCount() throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean getMoreResults() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setFetchDirection(int direction) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getFetchDirection() throws SQLException {
            throw new AssertionFailedError();
        }

        public void setFetchSize(int rows) throws SQLException {
            throw new AssertionFailedError();
        }

        public int getFetchSize() throws SQLException {
            throw new AssertionFailedError();
        }

        public int getResultSetConcurrency() throws SQLException {
            throw new AssertionFailedError();
        }

        public int getResultSetType() throws SQLException {
            throw new AssertionFailedError();
        }

        public void addBatch(String sql) throws SQLException {
            throw new AssertionFailedError();
        }

        public void clearBatch() throws SQLException {
            throw new AssertionFailedError();
        }

        public int[] executeBatch() throws SQLException {
            throw new AssertionFailedError();
        }

        public Connection getConnection() throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean getMoreResults(int current) throws SQLException {
            throw new AssertionFailedError();
        }

        public ResultSet getGeneratedKeys() throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate(String sql, int autoGeneratedKeys)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate(String sql, int[] columnIndexes)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public int executeUpdate(String sql, String[] columnNames)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute(String sql, int autoGeneratedKeys)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute(String sql, int[] columnIndexes)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public boolean execute(String sql, String[] columnNames)
                throws SQLException {
            throw new AssertionFailedError();
        }

        public int getResultSetHoldability() throws SQLException {
            throw new AssertionFailedError();
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

}

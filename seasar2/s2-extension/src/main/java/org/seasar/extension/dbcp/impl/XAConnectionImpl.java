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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

/**
 * {@link XAConnection}の実装です。
 * 
 * @author higa
 * 
 */
public class XAConnectionImpl implements XAConnection {

    private Connection connection;

    private XAResource xaResource;

    private List listeners = new ArrayList();

    /**
     * {@link XAConnectionImpl}を作成します。
     * 
     * @param connection
     *            コネクション
     */
    public XAConnectionImpl(Connection connection) {
        this.connection = connection;
        this.xaResource = new DBXAResourceImpl(connection);
    }

    public XAResource getXAResource() {
        return xaResource;
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public void close() throws SQLException {
        if (connection == null) {
            return;
        }
        if (!connection.isClosed()) {
            connection.close();
        }
        connection = null;
    }

    public synchronized void addConnectionEventListener(
            final ConnectionEventListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeConnectionEventListener(
            final ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Registers a <code>StatementEventListener</code> with this <code>PooledConnection</code> object.  Components that
     * wish to be notified when  <code>PreparedStatement</code>s created by the
     * connection are closed or are detected to be invalid may use this method
     * to register a <code>StatementEventListener</code> with this <code>PooledConnection</code> object.
     * <p>
     *
     * @param listener an component which implements the <code>StatementEventListener</code>
     *                 interface that is to be registered with this <code>PooledConnection</code> object
     *                 <p>
     * @since 1.6
     */
    @Override
    public void addStatementEventListener(StatementEventListener listener) {

    }

    /**
     * Removes the specified <code>StatementEventListener</code> from the list of
     * components that will be notified when the driver detects that a
     * <code>PreparedStatement</code> has been closed or is invalid.
     * <p>
     *
     * @param listener the component which implements the
     *                 <code>StatementEventListener</code> interface that was previously
     *                 registered with this <code>PooledConnection</code> object
     *                 <p>
     * @since 1.6
     */
    @Override
    public void removeStatementEventListener(StatementEventListener listener) {

    }
}
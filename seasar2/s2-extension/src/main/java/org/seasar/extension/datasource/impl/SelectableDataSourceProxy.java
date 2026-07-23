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
package org.seasar.extension.datasource.impl;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.seasar.extension.datasource.DataSourceFactory;
import org.seasar.framework.exception.EmptyRuntimeException;

/**
 * 他のデータソースに委譲するデータソースのプロキシです。
 * <p>
 * このプロキシはWEBアプリケーション等でユーザごとに異なったデータソースを切り替えたい場合に使われることを想定しています。
 * データソースを利用するDAOはこのプロキシをデータソースとして使用します。
 * </p>
 * 
 * @author koichik
 * @author higa
 * 
 */
public class SelectableDataSourceProxy implements DataSource {

    /**
     * データソースファクトリです。
     */
    protected DataSourceFactory dataSourceFactory;

    /**
     * @param dataSourceFactory
     *            データソースファクトリ
     */
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * <code>DataSourceFactory</code>からデータソースを取得します。
     * 
     * @return スレッドコンテキストに設定された名前を持つデータソース
     * @throws EmptyRuntimeException
     *             スレッドコンテキストにデータソース名が設定されていない場合にスローされます
     * @throws org.seasar.framework.container.ComponentNotFoundException
     *             <code>DataSourceFactory</code>に設定されたデータソース名を持つコンポーネントがS2コンテナに登録されていない場合にスローされます
     */
    public DataSource getDataSource() {
        String dataSourceName = dataSourceFactory.getSelectableDataSourceName();
        if (dataSourceName == null) {
            throw new EmptyRuntimeException("dataSourceName");
        }
        return dataSourceFactory.getDataSource(dataSourceName);
    }

    /**
     * スレッドコンテキストに設定された名前を持つデータソースからコネクションを取得して返します。
     * 
     * @return スレッドコンテキストに設定された名前を持つデータソースから取得したコネクション
     * @throws EmptyRuntimeException
     *             スレッドコンテキストにデータソース名が設定されていない場合にスローされます
     * @throws org.seasar.framework.container.ComponentNotFoundException
     *             スレッドコンテキストに設定されたデータソース名を持つコンポーネントがS2コンテナに登録されていない場合にスローされます
     * @throws SQLException
     *             データソースで例外が発生した場合にスローされます
     */
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /**
     * スレッドコンテキストに設定された名前を持つデータソースからコネクションを取得して返します。
     * 
     * @param username
     *            ユーザ名
     * @param password
     *            パスワード
     * @return スレッドコンテキストに設定された名前を持つデータソースから取得したコネクション
     * @throws EmptyRuntimeException
     *             スレッドコンテキストにデータソース名が設定されていない場合にスローされます
     * @throws org.seasar.framework.container.ComponentNotFoundException
     *             スレッドコンテキストに設定されたデータソース名を持つコンポーネントがS2コンテナに登録されていない場合にスローされます
     * @throws SQLException
     *             データソースで例外が発生した場合にスローされます
     */
    public Connection getConnection(final String username, final String password)
            throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    /**
     * スレッドコンテキストに設定された名前を持つデータソースからログライターを取得して返します。
     * 
     * @return スレッドコンテキストに設定された名前を持つデータソースから取得したログライター
     * @throws EmptyRuntimeException
     *             スレッドコンテキストにデータソース名が設定されていない場合にスローされます
     * @throws org.seasar.framework.container.ComponentNotFoundException
     *             スレッドコンテキストに設定されたデータソース名を持つコンポーネントがS2コンテナに登録されていない場合にスローされます
     * @throws SQLException
     *             データソースで例外が発生した場合にスローされます
     */
    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    /**
     * スレッドコンテキストに設定された名前を持つデータソースにログライターを設定します。
     * 
     * @param out
     *            スレッドコンテキストに設定された名前を持つデータソースに設定するログライター
     * @throws EmptyRuntimeException
     *             スレッドコンテキストにデータソース名が設定されていない場合にスローされます
     * @throws org.seasar.framework.container.ComponentNotFoundException
     *             スレッドコンテキストに設定されたデータソース名を持つコンポーネントがS2コンテナに登録されていない場合にスローされます
     * @throws SQLException
     *             データソースで例外が発生した場合にスローされます
     */
    public void setLogWriter(final PrintWriter out) throws SQLException {
        getDataSource().setLogWriter(out);
    }

    /**
     * スレッドコンテキストに設定された名前を持つデータソースからログインタイムアウト時間(秒）を取得して返します。
     * 
     * @return スレッドコンテキストに設定された名前を持つデータソースから取得したログインタイムアウト時間(秒）
     * @throws EmptyRuntimeException
     *             スレッドコンテキストにデータソース名が設定されていない場合にスローされます
     * @throws org.seasar.framework.container.ComponentNotFoundException
     *             スレッドコンテキストに設定されたデータソース名を持つコンポーネントがS2コンテナに登録されていない場合にスローされます
     * @throws SQLException
     *             データソースで例外が発生した場合にスローされます
     */
    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    /**
     * Return the parent Logger of all the Loggers used by this data source. This
     * should be the Logger farthest from the root Logger that is
     * still an ancestor of all of the Loggers used by this data source. Configuring
     * this Logger will affect all of the log messages generated by the data source.
     * In the worst case, this may be the root Logger.
     *
     * @return the parent Logger for this data source
     * @throws SQLFeatureNotSupportedException if the data source does not use
     *                                         {@code java.util.logging}
     * @since 1.7
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    /**
     * スレッドコンテキストに設定された名前を持つデータソースにログインタイムアウト時間(秒）を設定します。
     * 
     * @param seconds
     *            スレッドコンテキストに設定された名前を持つデータソースに設定するログインタイムアウト時間(秒）
     * @throws EmptyRuntimeException
     *             スレッドコンテキストにデータソース名が設定されていない場合にスローされます
     * @throws org.seasar.framework.container.ComponentNotFoundException
     *             スレッドコンテキストに設定されたデータソース名を持つコンポーネントがS2コンテナに登録されていない場合にスローされます
     * @throws SQLException
     *             データソースで例外が発生した場合にスローされます
     */
    public void setLoginTimeout(int seconds) throws SQLException {
        getDataSource().setLoginTimeout(seconds);
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

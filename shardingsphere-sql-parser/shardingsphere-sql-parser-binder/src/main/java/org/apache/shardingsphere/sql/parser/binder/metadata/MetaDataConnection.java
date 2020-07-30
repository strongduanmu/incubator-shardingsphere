/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.sql.parser.binder.metadata;

import lombok.RequiredArgsConstructor;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Meta data connection.
 */
@RequiredArgsConstructor
public final class MetaDataConnection implements Connection {
    
    private final Connection connection;
    
    @Override
    public String getCatalog() {
        try {
            return connection.getCatalog();
        } catch (SQLException ex) {
            return null;
        }
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }
    
    @Override
    public String getSchema() {
        try {
            return connection.getSchema();
        } catch (SQLException ex) {
            return null;
        }
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
        connection.setSchema(schema);
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return connection.prepareStatement(sql, columnIndexes);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return connection.prepareStatement(sql, columnNames);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return connection.prepareCall(sql);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }
    
    @Override
    public void commit() throws SQLException {
        connection.commit();
    }
    
    @Override
    public void close() throws SQLException {
        connection.close();
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        return connection.setSavepoint(name);
    }
    
    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }
    
    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return connection.isValid(timeout);
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }
    
    @Override
    public void setClientInfo(final Properties props) throws SQLClientInfoException {
        connection.setClientInfo(props);
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return connection.getClientInfo(name);
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        connection.abort(executor);
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }
}

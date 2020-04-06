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

package org.apache.shardingsphere.shardingjdbc.jdbc.refreh;

import org.apache.shardingsphere.shardingjdbc.jdbc.core.context.impl.ShardingRuntimeContext;
import org.apache.shardingsphere.sql.parser.binder.statement.SQLStatementContext;

import java.sql.SQLException;

/**
 * Meta data refresh strategy.
 * 
 * @param <T> type of SQL statement context
 */
public interface MetaDataRefreshStrategy<T extends SQLStatementContext> {
    
    /**
     * Refresh meta data.
     *
     * @param shardingRuntimeContext sharding runtime context
     * @param sqlStatementContext SQL statement context
     * @throws SQLException SQL exception
     */
    void refreshMetaData(ShardingRuntimeContext shardingRuntimeContext, T sqlStatementContext) throws SQLException;
}

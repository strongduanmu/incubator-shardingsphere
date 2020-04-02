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

package org.apache.shardingsphere.shardingjdbc.jdbc.refresh;

import lombok.SneakyThrows;
import org.apache.shardingsphere.shardingjdbc.jdbc.refreh.MetaDataRefreshStrategy;
import org.apache.shardingsphere.shardingjdbc.jdbc.refreh.impl.CreateIndexStatementMetaDataRefreshStrategy;
import org.apache.shardingsphere.sql.parser.binder.statement.ddl.CreateIndexStatementContext;
import org.apache.shardingsphere.sql.parser.sql.segment.ddl.index.IndexSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.generic.table.SimpleTableSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.generic.table.TableNameSegment;
import org.apache.shardingsphere.sql.parser.sql.statement.ddl.CreateIndexStatement;
import org.apache.shardingsphere.sql.parser.sql.value.identifier.IdentifierValue;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CreateIndexStatementMetaDataRefreshStrategyTest extends AbstractMetaDataRefreshStrategyTest {
    
    @SneakyThrows
    @Test
    public void refreshMetaData() {
        MetaDataRefreshStrategy<CreateIndexStatementContext> metaDataRefreshStrategy = new CreateIndexStatementMetaDataRefreshStrategy();
        final CreateIndexStatement createIndexStatement = new CreateIndexStatement();
        createIndexStatement.setIndex(new IndexSegment(1, 2, new IdentifierValue("t_order_index")));
        createIndexStatement.setTable(new SimpleTableSegment(new TableNameSegment(1, 3, new IdentifierValue("t_order"))));
        CreateIndexStatementContext createIndexStatementContext = new CreateIndexStatementContext(createIndexStatement);
        metaDataRefreshStrategy.refreshMetaData(getRuntimeContext(), createIndexStatementContext);
        assertThat(getRuntimeContext().getMetaData().getSchema().get("t_order").getIndexes().containsKey("t_order_index"), is(true));
    }
}


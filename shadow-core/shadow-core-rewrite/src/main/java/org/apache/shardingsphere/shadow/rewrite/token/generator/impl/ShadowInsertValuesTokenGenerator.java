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

package org.apache.shardingsphere.shadow.rewrite.token.generator.impl;

import lombok.Setter;
import org.apache.shardingsphere.shadow.rewrite.token.generator.BaseShadowSQLTokenGenerator;
import org.apache.shardingsphere.shadow.rewrite.token.pojo.ShadowInsertValuesToken;
import org.apache.shardingsphere.sql.parser.binder.segment.insert.values.InsertValueContext;
import org.apache.shardingsphere.sql.parser.binder.statement.SQLStatementContext;
import org.apache.shardingsphere.sql.parser.binder.statement.dml.InsertStatementContext;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.assignment.InsertValuesSegment;
import org.apache.shardingsphere.infra.rewrite.sql.token.generator.OptionalSQLTokenGenerator;
import org.apache.shardingsphere.infra.rewrite.sql.token.pojo.generic.InsertValue;
import org.apache.shardingsphere.infra.rewrite.sql.token.pojo.generic.InsertValuesToken;

import java.util.Collection;
import java.util.Iterator;

/**
 * Insert values token generator for shadow.
 */
@Setter
public final class ShadowInsertValuesTokenGenerator extends BaseShadowSQLTokenGenerator implements OptionalSQLTokenGenerator<InsertStatementContext> {
    
    @Override
    protected boolean isGenerateSQLTokenForShadow(final SQLStatementContext sqlStatementContext) {
        return sqlStatementContext instanceof InsertStatementContext && (((InsertStatementContext) sqlStatementContext).getSqlStatement()).getColumnNames().contains(getShadowRule().getColumn());
    }
    
    @Override
    public InsertValuesToken generateSQLToken(final InsertStatementContext insertStatementContext) {
        Collection<InsertValuesSegment> insertValuesSegments = insertStatementContext.getSqlStatement().getValues();
        InsertValuesToken result = new ShadowInsertValuesToken(getStartIndex(insertValuesSegments), getStopIndex(insertValuesSegments));
        for (InsertValueContext each : insertStatementContext.getInsertValueContexts()) {
            InsertValue insertValueToken = new InsertValue(each.getValueExpressions());
            Iterator<String> descendingColumnNames = insertStatementContext.getDescendingColumnNames();
            while (descendingColumnNames.hasNext()) {
                String columnName = descendingColumnNames.next();
                if (getShadowRule().getColumn().equals(columnName)) {
                    removeValueToken(insertValueToken, insertStatementContext, columnName);
                }
            }
            result.getInsertValues().add(insertValueToken);
        }
        return result;
    }
    
    private void removeValueToken(final InsertValue insertValueToken, final InsertStatementContext insertStatementContext, final String columnName) {
        int columnIndex = insertStatementContext.getColumnNames().indexOf(columnName);
        insertValueToken.getValues().remove(columnIndex);
    }
    
    private int getStartIndex(final Collection<InsertValuesSegment> segments) {
        int result = segments.iterator().next().getStartIndex();
        for (InsertValuesSegment each : segments) {
            result = Math.min(result, each.getStartIndex());
        }
        return result;
    }
    
    private int getStopIndex(final Collection<InsertValuesSegment> segments) {
        int result = segments.iterator().next().getStopIndex();
        for (InsertValuesSegment each : segments) {
            result = Math.max(result, each.getStopIndex());
        }
        return result;
    }
}

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

package org.apache.shardingsphere.scaling.core.config.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.scaling.core.config.RdbmsConfiguration;
import org.apache.shardingsphere.scaling.core.job.position.PositionManager;
import org.apache.shardingsphere.scaling.core.job.position.PrimaryKeyPosition;

/**
 * Rdbms configuration Util.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RdbmsConfigurationUtil {
    
    /**
     * Get sql where condition whit primary key.
     *
     * @param rdbmsConfiguration rdbms configuration
     * @return sql where condition
     */
    public static String getWhereCondition(final RdbmsConfiguration rdbmsConfiguration) {
        return getWhereCondition(rdbmsConfiguration.getPrimaryKey(), rdbmsConfiguration.getPositionManager());
    }
    
    private static String getWhereCondition(final String primaryKey, final PositionManager<PrimaryKeyPosition> positionManager) {
        if (null == primaryKey || null == positionManager) {
            return "";
        }
        PrimaryKeyPosition position = positionManager.getCurrentPosition();
        return String.format("WHERE %s BETWEEN %d AND %d", primaryKey, position.getBeginValue(), position.getEndValue());
    }
}

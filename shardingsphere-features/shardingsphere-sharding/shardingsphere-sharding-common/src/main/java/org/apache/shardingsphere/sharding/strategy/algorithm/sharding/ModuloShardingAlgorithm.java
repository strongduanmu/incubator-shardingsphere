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

package org.apache.shardingsphere.sharding.strategy.algorithm.sharding;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.sharding.api.sharding.ShardingAutoTableAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * Modulo sharding algorithm.
 * 
 * <p>Shard by `y = x mod v` algorithm. 
 * v is `MODULO_VALUE`. </p>
 */
public final class ModuloShardingAlgorithm implements StandardShardingAlgorithm<Comparable<?>>, ShardingAutoTableAlgorithm {
    
    private static final String MODULO_VALUE = "mod.value";
    
    @Getter
    @Setter
    private Properties properties = new Properties();
    
    @Override
    public void init() {
        Preconditions.checkNotNull(properties.get(MODULO_VALUE), "Modulo value cannot be null.");
    }
    
    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<Comparable<?>> shardingValue) {
        for (String each : availableTargetNames) {
            if (each.endsWith(getLongValue(shardingValue.getValue()) % getModuloValue() + "")) {
                return each;
            }
        }
        return null;
    }
    
    @Override
    public Collection<String> doSharding(final Collection<String> availableTargetNames, final RangeShardingValue<Comparable<?>> shardingValue) {
        if (isContainAllTargets(shardingValue)) {
            return availableTargetNames;
        }
        return getAvailableTargetNames(availableTargetNames, shardingValue);
    }
    
    private boolean isContainAllTargets(final RangeShardingValue<Comparable<?>> shardingValue) {
        return !shardingValue.getValueRange().hasUpperBound() || shardingValue.getValueRange().hasLowerBound()
                && getLongValue(shardingValue.getValueRange().upperEndpoint()) - getLongValue(shardingValue.getValueRange().lowerEndpoint()) >= getModuloValue() - 1;
    }
    
    private Collection<String> getAvailableTargetNames(final Collection<String> availableTargetNames, final RangeShardingValue<Comparable<?>> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        for (long i = getLongValue(shardingValue.getValueRange().lowerEndpoint()); i <= getLongValue(shardingValue.getValueRange().upperEndpoint()); i++) {
            for (String each : availableTargetNames) {
                if (each.endsWith(i % getModuloValue() + "")) {
                    result.add(each);
                }
            }
        }
        return result;
    }
    
    private long getModuloValue() {
        return Long.parseLong(properties.get(MODULO_VALUE).toString());
    }
    
    @Override
    public String getType() {
        return "MOD";
    }
    
    @Override
    public int getAutoTablesAmount() {
        Preconditions.checkNotNull(properties.get(MODULO_VALUE), "Modulo value cannot be null.");
        return Integer.parseInt(properties.get(MODULO_VALUE).toString());
    }
    
    private long getLongValue(final Comparable<?> value) {
        return Long.parseLong(value.toString());
    }
}

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

package org.apache.shardingsphere.sharding.yaml.swapper;

import com.google.common.base.Preconditions;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.YamlTableRuleConfiguration;
import org.apache.shardingsphere.infra.yaml.swapper.YamlSwapper;

/**
 * Table rule configuration YAML swapper.
 */
public final class TableRuleConfigurationYamlSwapper implements YamlSwapper<YamlTableRuleConfiguration, ShardingTableRuleConfiguration> {
    
    private final ShardingStrategyConfigurationYamlSwapper shardingStrategyConfigurationYamlSwapper = new ShardingStrategyConfigurationYamlSwapper();
    
    private final KeyGeneratorConfigurationYamlSwapper keyGeneratorConfigurationYamlSwapper = new KeyGeneratorConfigurationYamlSwapper();
    
    @Override
    public YamlTableRuleConfiguration swap(final ShardingTableRuleConfiguration data) {
        YamlTableRuleConfiguration result = new YamlTableRuleConfiguration();
        result.setLogicTable(data.getLogicTable());
        result.setActualDataNodes(data.getActualDataNodes());
        if (null != data.getDatabaseShardingStrategy()) {
            result.setDatabaseStrategy(shardingStrategyConfigurationYamlSwapper.swap(data.getDatabaseShardingStrategy()));
        }
        if (null != data.getTableShardingStrategy()) {
            result.setTableStrategy(shardingStrategyConfigurationYamlSwapper.swap(data.getTableShardingStrategy()));
        }
        if (null != data.getKeyGenerator()) {
            result.setKeyGenerator(keyGeneratorConfigurationYamlSwapper.swap(data.getKeyGenerator()));
        }
        return result;
    }
    
    @Override
    public ShardingTableRuleConfiguration swap(final YamlTableRuleConfiguration yamlConfiguration) {
        Preconditions.checkNotNull(yamlConfiguration.getLogicTable(), "Logic table cannot be null.");
        ShardingTableRuleConfiguration result = new ShardingTableRuleConfiguration(yamlConfiguration.getLogicTable(), yamlConfiguration.getActualDataNodes());
        if (null != yamlConfiguration.getDatabaseStrategy()) {
            result.setDatabaseShardingStrategy(shardingStrategyConfigurationYamlSwapper.swap(yamlConfiguration.getDatabaseStrategy()));
        }
        if (null != yamlConfiguration.getTableStrategy()) {
            result.setTableShardingStrategy(shardingStrategyConfigurationYamlSwapper.swap(yamlConfiguration.getTableStrategy()));
        }
        if (null != yamlConfiguration.getKeyGenerator()) {
            result.setKeyGenerator(keyGeneratorConfigurationYamlSwapper.swap(yamlConfiguration.getKeyGenerator()));
        }
        return result;
    }
}

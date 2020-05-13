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

package org.apache.shardingsphere.orchestration.core.configcenter;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.shardingsphere.masterslave.api.config.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.shadow.ShadowRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.core.rule.Authentication;
import org.apache.shardingsphere.core.yaml.config.YamlRootRuleConfigurations;
import org.apache.shardingsphere.core.yaml.config.common.YamlAuthenticationConfiguration;
import org.apache.shardingsphere.core.yaml.constructor.YamlRootRuleConfigurationsConstructor;
import org.apache.shardingsphere.core.yaml.swapper.AuthenticationYamlSwapper;
import org.apache.shardingsphere.core.yaml.swapper.root.RuleRootConfigurationsYamlSwapper;
import org.apache.shardingsphere.encrypt.api.EncryptRuleConfiguration;
import org.apache.shardingsphere.orchestration.center.ConfigCenterRepository;
import org.apache.shardingsphere.orchestration.core.configuration.DataSourceConfigurationYamlSwapper;
import org.apache.shardingsphere.orchestration.core.configuration.YamlDataSourceConfiguration;
import org.apache.shardingsphere.underlying.common.config.DataSourceConfiguration;
import org.apache.shardingsphere.underlying.common.config.RuleConfiguration;
import org.apache.shardingsphere.underlying.common.yaml.engine.YamlEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Configuration service.
 */
public final class ConfigCenter {
    
    private final ConfigCenterNode node;
    
    private final ConfigCenterRepository repository;
    
    public ConfigCenter(final String name, final ConfigCenterRepository configCenterRepository) {
        this.node = new ConfigCenterNode(name);
        this.repository = configCenterRepository;
    }
    
    /**
     * Persist rule configuration.
     *
     * @param shardingSchemaName sharding schema name
     * @param dataSourceConfigs data source configuration map
     * @param ruleConfigurations rule configurations
     * @param authentication authentication
     * @param props sharding properties
     * @param isOverwrite is overwrite registry center's configuration
     */
    public void persistConfigurations(final String shardingSchemaName, final Map<String, DataSourceConfiguration> dataSourceConfigs, final Collection<RuleConfiguration> ruleConfigurations,
                                      final Authentication authentication, final Properties props, final boolean isOverwrite) {
        persistDataSourceConfiguration(shardingSchemaName, dataSourceConfigs, isOverwrite);
        persistRuleConfigurations(shardingSchemaName, ruleConfigurations, isOverwrite);
        persistAuthentication(authentication, isOverwrite);
        persistProperties(props, isOverwrite);
        persistShardingSchemaName(shardingSchemaName);
    }
    
    private void persistDataSourceConfiguration(final String shardingSchemaName, final Map<String, DataSourceConfiguration> dataSourceConfigurations, final boolean isOverwrite) {
        if (isOverwrite || !hasDataSourceConfiguration(shardingSchemaName)) {
            Preconditions.checkState(null != dataSourceConfigurations && !dataSourceConfigurations.isEmpty(), "No available data source in `%s` for orchestration.", shardingSchemaName);
            Map<String, YamlDataSourceConfiguration> yamlDataSourceConfigurations = dataSourceConfigurations.entrySet().stream()
                    .collect(Collectors.toMap(Entry::getKey, entry -> new DataSourceConfigurationYamlSwapper().swap(entry.getValue())));
            repository.persist(node.getDataSourcePath(shardingSchemaName), YamlEngine.marshal(yamlDataSourceConfigurations));
        }
    }
    
    /**
     * Judge whether schema has data source configuration.
     *
     * @param shardingSchemaName shading schema name
     * @return has data source configuration or not
     */
    public boolean hasDataSourceConfiguration(final String shardingSchemaName) {
        return !Strings.isNullOrEmpty(repository.get(node.getDataSourcePath(shardingSchemaName)));
    }
    
    private void persistRuleConfigurations(final String shardingSchemaName, final Collection<RuleConfiguration> ruleConfigurations, final boolean isOverwrite) {
        if (ruleConfigurations.isEmpty()) {
            return;
        }
        if (isOverwrite || !hasRuleConfiguration(shardingSchemaName)) {
            persistRuleConfigurations(shardingSchemaName, ruleConfigurations);
        }
    }
    
    private void persistRuleConfigurations(final String shardingSchemaName, final Collection<RuleConfiguration> ruleConfigurations) {
        Collection<RuleConfiguration> configurations = new LinkedList<>();
        for (RuleConfiguration each : ruleConfigurations) {
            if (each instanceof ShardingRuleConfiguration) {
                ShardingRuleConfiguration config = (ShardingRuleConfiguration) each;
                Preconditions.checkState(!config.getTableRuleConfigs().isEmpty() || null != config.getDefaultTableShardingStrategyConfig(),
                        "No available sharding rule configuration in `%s` for orchestration.", shardingSchemaName);
                configurations.add(each);
            } else if (each instanceof MasterSlaveRuleConfiguration) {
                MasterSlaveRuleConfiguration config = (MasterSlaveRuleConfiguration) each;
                config.getDataSources().forEach(group -> Preconditions.checkState(
                        !group.getMasterDataSourceName().isEmpty(), "No available master-slave rule configuration in `%s` for orchestration.", shardingSchemaName));
                configurations.add(each);
            } else if (each instanceof EncryptRuleConfiguration) {
                EncryptRuleConfiguration config = (EncryptRuleConfiguration) each;
                Preconditions.checkState(!config.getEncryptors().isEmpty(), "No available encrypt rule configuration in `%s` for orchestration.", shardingSchemaName);
                configurations.add(each);
            } else if (each instanceof ShadowRuleConfiguration) {
                ShadowRuleConfiguration config = (ShadowRuleConfiguration) each;
                Preconditions.checkState(!config.getColumn().isEmpty() && null != config.getShadowMappings(), "No available shadow rule configuration in `%s` for orchestration.", shardingSchemaName);
                // TODO process shadow
            }
        }
        repository.persist(node.getRulePath(shardingSchemaName), YamlEngine.marshal(new RuleRootConfigurationsYamlSwapper().swap(configurations)));
    }
    
    /**
     * Judge whether schema has rule configuration.
     *
     * @param shardingSchemaName sharding schema name
     * @return has rule configuration or not
     */
    public boolean hasRuleConfiguration(final String shardingSchemaName) {
        return !Strings.isNullOrEmpty(repository.get(node.getRulePath(shardingSchemaName)));
    }
    
    private void persistAuthentication(final Authentication authentication, final boolean isOverwrite) {
        if (null != authentication && (isOverwrite || !hasAuthentication())) {
            repository.persist(node.getAuthenticationPath(), YamlEngine.marshal(new AuthenticationYamlSwapper().swap(authentication)));
        }
    }
    
    private boolean hasAuthentication() {
        return !Strings.isNullOrEmpty(repository.get(node.getAuthenticationPath()));
    }
    
    private void persistProperties(final Properties props, final boolean isOverwrite) {
        if (isOverwrite || !hasProperties()) {
            repository.persist(node.getPropsPath(), YamlEngine.marshal(props));
        }
    }
    
    private boolean hasProperties() {
        return !Strings.isNullOrEmpty(repository.get(node.getPropsPath()));
    }
    
    private void persistShardingSchemaName(final String shardingSchemaName) {
        String shardingSchemaNames = repository.get(node.getSchemaPath());
        if (Strings.isNullOrEmpty(shardingSchemaNames)) {
            repository.persist(node.getSchemaPath(), shardingSchemaName);
            return;
        }
        List<String> schemaNameList = Splitter.on(",").splitToList(shardingSchemaNames);
        if (schemaNameList.contains(shardingSchemaName)) {
            return;
        }
        List<String> newArrayList = new ArrayList<>(schemaNameList);
        newArrayList.add(shardingSchemaName);
        repository.persist(node.getSchemaPath(), Joiner.on(",").join(newArrayList));
    }
    
    /**
     * Judge is sharding rule or master-slave rule.
     *
     * @param shardingSchemaName sharding schema name
     * @return is sharding rule or not
     */
    public boolean isShardingRule(final String shardingSchemaName) {
        return repository.get(node.getRulePath(shardingSchemaName)).contains("shardingRule:\n");
    }
    
    /**
     * Judge is encrypt rule or not.
     * @param shardingSchemaName sharding schema name
     * @return is encrypt rule or not
     */
    public boolean isEncryptRule(final String shardingSchemaName) {
        return repository.get(node.getRulePath(shardingSchemaName)).contains("encryptRule:\n");
    }
    
    /**
     * Judge is shadow rule or not.
     * @param shardingSchemaName sharding schema name
     * @return is shadow rule or not
     */
    public boolean isShadowRule(final String shardingSchemaName) {
        return repository.get(node.getRulePath(shardingSchemaName)).contains("shadowRule:\n");
    }
    
    /**
     * Load data source configurations.
     *
     * @param shardingSchemaName sharding schema name
     * @return data source configurations
     */
    @SuppressWarnings("unchecked")
    public Map<String, DataSourceConfiguration> loadDataSourceConfigurations(final String shardingSchemaName) {
        Map<String, YamlDataSourceConfiguration> result = (Map) YamlEngine.unmarshal(repository.get(node.getDataSourcePath(shardingSchemaName)));
        Preconditions.checkState(null != result && !result.isEmpty(), "No available data sources to load for orchestration.");
        return result.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new DataSourceConfigurationYamlSwapper().swap(e.getValue())));
    }
    
    /**
     * Load rule configurations.
     *
     * @param shardingSchemaName sharding schema name
     * @return rule configurations
     */
    public Collection<RuleConfiguration> loadRuleConfigurations(final String shardingSchemaName) {
        return new RuleRootConfigurationsYamlSwapper().swap(YamlEngine.unmarshal(
                repository.get(node.getRulePath(shardingSchemaName)), YamlRootRuleConfigurations.class, new YamlRootRuleConfigurationsConstructor()));
    }
    
    /**
     * Load master-slave rule configuration.
     *
     * @param shardingSchemaName sharding schema name
     * @return master-slave rule configuration
     */
    public MasterSlaveRuleConfiguration loadMasterSlaveRuleConfiguration(final String shardingSchemaName) {
        return (MasterSlaveRuleConfiguration) new RuleRootConfigurationsYamlSwapper().swap(
                YamlEngine.unmarshal(repository.get(node.getRulePath(shardingSchemaName)), YamlRootRuleConfigurations.class)).iterator().next();
    }
    
    /**
     * Load encrypt rule configuration.
     *
     * @param shardingSchemaName sharding schema name
     * @return encrypt rule configuration
     */
    public EncryptRuleConfiguration loadEncryptRuleConfiguration(final String shardingSchemaName) {
        System.out.println("00:" + repository.get(node.getRulePath(shardingSchemaName)));
        Collection<RuleConfiguration> ruleConfigurations = new RuleRootConfigurationsYamlSwapper().swap(
                YamlEngine.unmarshal(repository.get(node.getRulePath(shardingSchemaName)), YamlRootRuleConfigurations.class));
        return ruleConfigurations.isEmpty() ? new EncryptRuleConfiguration(Collections.emptyMap(), Collections.emptyMap()) : (EncryptRuleConfiguration) ruleConfigurations.iterator().next();
    }
    
    /**
     * Load shadow rule configuration.
     *
     * @param shardingSchemaName sharding schema name
     * @return shadow rule configuration
     */
    // TODO fix shadow
    public ShadowRuleConfiguration loadShadowRuleConfiguration(final String shardingSchemaName) {
        return (ShadowRuleConfiguration) new RuleRootConfigurationsYamlSwapper().swap(
                YamlEngine.unmarshal(repository.get(node.getRulePath(shardingSchemaName)), YamlRootRuleConfigurations.class)).iterator().next();
    }
    
    /**
     * Load authentication.
     *
     * @return authentication
     */
    public Authentication loadAuthentication() {
        return new AuthenticationYamlSwapper().swap(YamlEngine.unmarshal(repository.get(node.getAuthenticationPath()), YamlAuthenticationConfiguration.class));
    }
    
    /**
     * Load properties configuration.
     *
     * @return properties
     */
    public Properties loadProperties() {
        return YamlEngine.unmarshalProperties(repository.get(node.getPropsPath()));
    }
    
    /**
     * Get all sharding schema names.
     * 
     * @return all sharding schema names
     */
    public Collection<String> getAllShardingSchemaNames() {
        String shardingSchemaNames = repository.get(node.getSchemaPath());
        return node.splitShardingSchemaName(shardingSchemaNames);
    }
}

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

package org.apache.shardingsphere.scaling.core.job.preparer.resumer;

import org.apache.shardingsphere.scaling.core.config.DataSourceConfiguration;
import org.apache.shardingsphere.scaling.core.config.JDBCDataSourceConfiguration;
import org.apache.shardingsphere.scaling.core.config.RdbmsConfiguration;
import org.apache.shardingsphere.scaling.core.config.ScalingContext;
import org.apache.shardingsphere.scaling.core.config.ServerConfiguration;
import org.apache.shardingsphere.scaling.core.config.SyncConfiguration;
import org.apache.shardingsphere.scaling.core.datasource.DataSourceManager;
import org.apache.shardingsphere.scaling.core.job.ShardingScalingJob;
import org.apache.shardingsphere.scaling.core.job.position.Position;
import org.apache.shardingsphere.scaling.core.job.position.PositionManager;
import org.apache.shardingsphere.scaling.core.job.position.PrimaryKeyPosition;
import org.apache.shardingsphere.scaling.core.job.position.PrimaryKeyPositionManager;
import org.apache.shardingsphere.scaling.core.job.position.resume.ResumablePositionManager;
import org.apache.shardingsphere.scaling.core.job.position.resume.ResumablePositionManagerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class SyncPositionResumerTest {
    
    private static final String DATA_SOURCE_URL = "jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL";
    
    private static final String USERNAME = "root";
    
    private static final String PASSWORD = "password";
    
    private ShardingScalingJob shardingScalingJob;
    
    private ResumablePositionManager resumablePositionManager;
    
    private SyncPositionResumer syncPositionResumer;
    
    @Before
    public void setUp() {
        ScalingContext.getInstance().init(new ServerConfiguration());
        shardingScalingJob = new ShardingScalingJob("scalingTest", 0);
        shardingScalingJob.getSyncConfigurations().add(mockSyncConfiguration());
        resumablePositionManager = ResumablePositionManagerFactory.newInstance("MySQL", "/scalingTest/item-0");
        syncPositionResumer = new SyncPositionResumer();
    }
    
    @Test
    public void assertResumePosition() {
        resumablePositionManager.getInventoryPositionManagerMap().put("ds0", new PrimaryKeyPositionManager(new PrimaryKeyPosition(0, 100)));
        resumablePositionManager.getIncrementalPositionManagerMap().put("ds0.t_order", mockPositionManager());
        syncPositionResumer.resumePosition(shardingScalingJob, new DataSourceManager(), resumablePositionManager);
        assertEquals(1, shardingScalingJob.getIncrementalDataTasks().size());
        assertEquals(0, shardingScalingJob.getInventoryDataTasks().size());
    }
    
    @Test
    public void assertPersistPosition() {
        ResumablePositionManager resumablePositionManager = mock(ResumablePositionManager.class);
        syncPositionResumer.persistPosition(shardingScalingJob, resumablePositionManager);
        verify(resumablePositionManager).persistIncrementalPosition();
        verify(resumablePositionManager).persistInventoryPosition();
    }
    
    private PositionManager mockPositionManager() {
        return new PositionManager() {
            @Override
            public Position getCurrentPosition() {
                return null;
            }
            
            @Override
            public void updateCurrentPosition(final Position newPosition) {
            
            }
        };
    }
    
    private SyncConfiguration mockSyncConfiguration() {
        RdbmsConfiguration dumperConfig = mockDumperConfig();
        RdbmsConfiguration importerConfig = new RdbmsConfiguration();
        Map<String, String> tableMap = new HashMap<>();
        tableMap.put("t_order", "t_order");
        return new SyncConfiguration(3, tableMap,
                dumperConfig, importerConfig);
    }
    
    private RdbmsConfiguration mockDumperConfig() {
        DataSourceConfiguration dataSourceConfiguration = new JDBCDataSourceConfiguration(DATA_SOURCE_URL, USERNAME, PASSWORD);
        RdbmsConfiguration result = new RdbmsConfiguration();
        result.setDataSourceName("ds0");
        result.setDataSourceConfiguration(dataSourceConfiguration);
        return result;
    }
}

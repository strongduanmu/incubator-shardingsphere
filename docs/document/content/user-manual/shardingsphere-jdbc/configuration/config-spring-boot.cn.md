+++
title = "Spring Boot配置"
weight = 3
+++

## 注意事项

行表达式标识符可以使用`${...}`或`$->{...}`，但前者与Spring本身的属性文件占位符冲突，因此在Spring环境中使用行表达式标识符建议使用`$->{...}`。

## 配置示例

### 数据分片

```properties
spring.shardingsphere.datasource.names=ds0,ds1

spring.shardingsphere.datasource.ds0.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.ds0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds0.url=jdbc:mysql://localhost:3306/ds0
spring.shardingsphere.datasource.ds0.username=root
spring.shardingsphere.datasource.ds0.password=

spring.shardingsphere.datasource.ds1.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds1.url=jdbc:mysql://localhost:3306/ds1
spring.shardingsphere.datasource.ds1.username=root
spring.shardingsphere.datasource.ds1.password=

spring.shardingsphere.sharding.tables.t_order.actual-data-nodes=ds$->{0..1}.t_order$->{0..1}
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.algorithm-expression=t_order$->{order_id % 2}

spring.shardingsphere.sharding.tables.t_order.key-generate-strategy.column=order_id
spring.shardingsphere.sharding.tables.t_order.key-generate-strategy.key-generator-name=snowflake

spring.shardingsphere.sharding.tables.t_order_item.actual-data-nodes=ds$->{0..1}.t_order_item$->{0..1}
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.algorithm-expression=t_order_item$->{order_id % 2}

spring.shardingsphere.sharding.tables.t_order_item.key-generate-strategy.column=order_item_id
spring.shardingsphere.sharding.tables.t_order_item.key-generate-strategy.key-generator-name=snowflake

spring.shardingsphere.sharding.binding-tables=t_order,t_order_item
spring.shardingsphere.sharding.broadcast-tables=t_config

spring.shardingsphere.sharding.default-database-strategy.inline.sharding-column=user_id
spring.shardingsphere.sharding.default-database-strategy.inline.algorithm-expression=ds$->{user_id % 2}

spring.shardingsphere.sharding.key-generators.snowflake.type=SNOWFLAKE
```

### 读写分离

```properties
spring.shardingsphere.datasource.names=master,slave0,slave1

spring.shardingsphere.datasource.master.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.master.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.master.url=jdbc:mysql://localhost:3306/master
spring.shardingsphere.datasource.master.username=root
spring.shardingsphere.datasource.master.password=

spring.shardingsphere.datasource.slave0.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.slave0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.slave0.url=jdbc:mysql://localhost:3306/slave0
spring.shardingsphere.datasource.slave0.username=root
spring.shardingsphere.datasource.slave0.password=

spring.shardingsphere.datasource.slave1.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.slave1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.slave1.url=jdbc:mysql://localhost:3306/slave1
spring.shardingsphere.datasource.slave1.username=root
spring.shardingsphere.datasource.slave1.password=

spring.shardingsphere.rules.master-slave.load-balancers.round_robin.type=ROUND_ROBIN
spring.shardingsphere.masterslave.name=ms
spring.shardingsphere.masterslave.master-data-source-name=master
spring.shardingsphere.masterslave.slave-data-source-names=slave0,slave1
spring.shardingsphere.masterslave.load-balancer-name=round_robin

spring.shardingsphere.properties.sql.show=true
```

### 数据加密

```properties
spring.shardingsphere.datasource.name=ds

spring.shardingsphere.datasource.ds.type=org.apache.commons.dbcp2.BasicDataSource
spring.shardingsphere.datasource.ds.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds.url=jdbc:mysql://127.0.0.1:3306/encrypt?serverTimezone=UTC&useSSL=false
spring.shardingsphere.datasource.ds.username=root
spring.shardingsphere.datasource.ds.password=
spring.shardingsphere.datasource.ds.max-total=100

spring.shardingsphere.encrypt.encryptors.aes_encryptor.type=AES
spring.shardingsphere.encrypt.encryptors.aes_encryptor.properties.aes.key.value=123456
spring.shardingsphere.encrypt.tables.t_order.columns.user_id.cipher-column=user_encrypt
spring.shardingsphere.encrypt.tables.t_order.columns.user_id.assisted-query-column=user_assisted
spring.shardingsphere.encrypt.tables.t_order.columns.user_id.plain-column=user_decrypt
spring.shardingsphere.encrypt.tables.t_order.columns.user_id.encryptor-name=aes_encryptor

spring.shardingsphere.properties.sql.show=true
spring.shardingsphere.properties.query.with.cipher.column=true
```

### 数据分片 + 读写分离

```properties
spring.shardingsphere.datasource.names=master0,master1,master0slave0,master0slave1,master1slave0,master1slave1

spring.shardingsphere.datasource.master0.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.master0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.master0.url=jdbc:mysql://localhost:3306/master0
spring.shardingsphere.datasource.master0.username=root
spring.shardingsphere.datasource.master0.password=

spring.shardingsphere.datasource.master0slave0.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.master0slave0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.master0slave0.url=jdbc:mysql://localhost:3306/master0slave0
spring.shardingsphere.datasource.master0slave0.username=root
spring.shardingsphere.datasource.master0slave0.password=
spring.shardingsphere.datasource.master0slave1.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.master0slave1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.master0slave1.url=jdbc:mysql://localhost:3306/master0slave1
spring.shardingsphere.datasource.master0slave1.username=root
spring.shardingsphere.datasource.master0slave1.password=

spring.shardingsphere.datasource.master1.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.master1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.master1.url=jdbc:mysql://localhost:3306/master1
spring.shardingsphere.datasource.master1.username=root
spring.shardingsphere.datasource.master1.password=

spring.shardingsphere.datasource.master1slave0.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.master1slave0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.master1slave0.url=jdbc:mysql://localhost:3306/master1slave0
spring.shardingsphere.datasource.master1slave0.username=root
spring.shardingsphere.datasource.master1slave0.password=
spring.shardingsphere.datasource.master1slave1.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.master1slave1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.master1slave1.url=jdbc:mysql://localhost:3306/master1slave1
spring.shardingsphere.datasource.master1slave1.username=root
spring.shardingsphere.datasource.master1slave1.password=

spring.shardingsphere.sharding.tables.t_order.actual-data-nodes=ds$->{0..1}.t_order$->{0..1}
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.algorithm-expression=t_order$->{order_id % 2}

spring.shardingsphere.sharding.tables.t_order.key-generate-strategy.column=order_id
spring.shardingsphere.sharding.tables.t_order.key-generate-strategy.key-generator-name=snowflake

spring.shardingsphere.sharding.tables.t_order_item.actual-data-nodes=ds$->{0..1}.t_order_item$->{0..1}
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.algorithm-expression=t_order_item$->{order_id % 2}

spring.shardingsphere.sharding.tables.t_order_item.key-generate-strategy.column=order_item_id
spring.shardingsphere.sharding.tables.t_order_item.key-generate-strategy.key-generator-name=snowflake

spring.shardingsphere.sharding.binding-tables=t_order,t_order_item
spring.shardingsphere.sharding.broadcast-tables=t_config

spring.shardingsphere.sharding.default-database-strategy.inline.sharding-column=user_id
spring.shardingsphere.sharding.default-database-strategy.inline.algorithm-expression=master$->{user_id % 2}

spring.shardingsphere.sharding.master-slave-rules.ds0.master-data-source-name=master0
spring.shardingsphere.sharding.master-slave-rules.ds0.slave-data-source-names=master0slave0, master0slave1
spring.shardingsphere.sharding.master-slave-rules.ds1.master-data-source-name=master1
spring.shardingsphere.sharding.master-slave-rules.ds1.slave-data-source-names=master1slave0, master1slave1

spring.shardingsphere.sharding.key-generators.snowflake.type=SNOWFLAKE
```

### 数据分片 + 数据加密

```properties
spring.shardingsphere.datasource.names=ds_0,ds_1

spring.shardingsphere.datasource.ds_0.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds_0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds_0.jdbc-url=jdbc:mysql://localhost:3306/demo_ds_0
spring.shardingsphere.datasource.ds_0.username=root
spring.shardingsphere.datasource.ds_0.password=

spring.shardingsphere.datasource.ds_1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds_1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds_1.jdbc-url=jdbc:mysql://localhost:3306/demo_ds_1
spring.shardingsphere.datasource.ds_1.username=root
spring.shardingsphere.datasource.ds_1.password=

spring.shardingsphere.sharding.default-database-strategy.inline.sharding-column=user_id
spring.shardingsphere.sharding.default-database-strategy.inline.algorithm-expression=ds_$->{user_id % 2}

spring.shardingsphere.sharding.tables.t_order.actual-data-nodes=ds_$->{0..1}.t_order_$->{0..1}
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.algorithm-expression=t_order_$->{order_id % 2}

spring.shardingsphere.sharding.tables.t_order.key-generate-strategy.column=order_id
spring.shardingsphere.sharding.tables.t_order.key-generate-strategy.key-generator-name=snowflake

spring.shardingsphere.sharding.tables.t_order_item.actual-data-nodes=ds_$->{0..1}.t_order_item_$->{0..1}
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.algorithm-expression=t_order_item_$->{order_id % 2}

spring.shardingsphere.sharding.tables.t_order_item.key-generate-strategy.column=order_item_id
spring.shardingsphere.sharding.tables.t_order_item.key-generate-strategy.key-generator-name=snowflake

spring.shardingsphere.sharding.encrypt-rule.encryptors.aes_encryptor.type=AES
spring.shardingsphere.sharding.encrypt-rule.encryptors.aes_encryptor.properties.aes.key.value=123456
spring.shardingsphere.sharding.encrypt-rule.tables.t_order.columns.user_id.cipher-column=user_encrypt
spring.shardingsphere.sharding.encrypt-rule.tables.t_order.columns.user_id.assisted-query-column=user_assisted
spring.shardingsphere.sharding.encrypt-rule.tables.t_order.columns.user_id.plain-column=user_decrypt
spring.shardingsphere.sharding.encrypt-rule.tables.t_order.columns.user_id.encryptor-name=aes_encryptor

spring.shardingsphere.sharding.key-generators.snowflake.type=SNOWFLAKE
```

### 治理

```properties
spring.shardingsphere.datasource.names=ds,ds0,ds1
spring.shardingsphere.datasource.ds.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.ds.driver-class-name=org.h2.Driver
spring.shardingsphere.datasource.ds.url=jdbc:mysql://localhost:3306/ds
spring.shardingsphere.datasource.ds.username=root
spring.shardingsphere.datasource.ds.password=

spring.shardingsphere.datasource.ds0.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.ds0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds0.url=jdbc:mysql://localhost:3306/ds0
spring.shardingsphere.datasource.ds0.username=root
spring.shardingsphere.datasource.ds0.password=

spring.shardingsphere.datasource.ds1.type=org.apache.commons.dbcp.BasicDataSource
spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.ds1.url=jdbc:mysql://localhost:3306/ds1
spring.shardingsphere.datasource.ds1.username=root
spring.shardingsphere.datasource.ds1.password=

spring.shardingsphere.sharding.default-data-source-name=ds
spring.shardingsphere.sharding.default-database-strategy.inline.sharding-column=user_id
spring.shardingsphere.sharding.default-database-strategy.inline.algorithm-expression=ds$->{user_id % 2}
spring.shardingsphere.sharding.tables.t_order.actual-data-nodes=ds$->{0..1}.t_order$->{0..1}
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.algorithm-expression=t_order$->{order_id % 2}
spring.shardingsphere.sharding.tables.t_order.key-generator.column=order_id
spring.shardingsphere.sharding.tables.t_order_item.actual-data-nodes=ds$->{0..1}.t_order_item$->{0..1}
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.algorithm-expression=t_order_item$->{order_id % 2}
spring.shardingsphere.sharding.tables.t_order_item.key-generator.column=order_item_id
spring.shardingsphere.sharding.binding-tables=t_order,t_order_item
spring.shardingsphere.sharding.broadcast-tables=t_config

spring.shardingsphere.orchestration.spring_boot_ds_sharding.orchestration-type=registry_center,config_center,metadata_center
spring.shardingsphere.orchestration.spring_boot_ds_sharding.instance-type=zookeeper
spring.shardingsphere.orchestration.spring_boot_ds_sharding.server-lists=localhost:2181
spring.shardingsphere.orchestration.spring_boot_ds_sharding.namespace=orchestration-spring-boot-sharding-test
spring.shardingsphere.orchestration.spring_boot_ds_sharding.properties.overwrite=true
```

### JNDI

以上配置示例中，所有数据源配置均可使用JNDI代替，如对于`数据分片`:
```properties
spring.shardingsphere.datasource.names=ds0,ds1

spring.shardingsphere.datasource.ds0.jndi-name=java:comp/env/jdbc/ds0
spring.shardingsphere.datasource.ds1.jndi-name=jdbc/ds1

spring.shardingsphere.sharding.tables.t_order.actual-data-nodes=ds$->{0..1}.t_order$->{0..1}
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order.table-strategy.inline.algorithm-expression=t_order$->{order_id % 2}

spring.shardingsphere.sharding.tables.t_order.key-generate-strategy.column=order_id
spring.shardingsphere.sharding.tables.t_order.key-generate-strategy.key-generator-name=snowflake

spring.shardingsphere.sharding.tables.t_order_item.actual-data-nodes=ds$->{0..1}.t_order_item$->{0..1}
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.sharding-column=order_id
spring.shardingsphere.sharding.tables.t_order_item.table-strategy.inline.algorithm-expression=t_order_item$->{order_id % 2}

spring.shardingsphere.sharding.tables.t_order_item.key-generate-strategy.column=order_item_id
spring.shardingsphere.sharding.tables.t_order_item.key-generate-strategy.key-generator-name=snowflake

spring.shardingsphere.sharding.binding-tables=t_order,t_order_item
spring.shardingsphere.sharding.broadcast-tables=t_config

spring.shardingsphere.sharding.default-database-strategy.inline.sharding-column=user_id
spring.shardingsphere.sharding.default-database-strategy.inline.algorithm-expression=ds$->{user_id % 2}

spring.shardingsphere.sharding.key-generators.snowflake.type=SNOWFLAKE
```

## 配置项说明

### 数据分片

```properties
spring.shardingsphere.datasource.names= #数据源名称，多数据源以逗号分隔

spring.shardingsphere.datasource.<data-source-name>.type= #数据库连接池类名称
spring.shardingsphere.datasource.<data-source-name>.driver-class-name= #数据库驱动类名
spring.shardingsphere.datasource.<data-source-name>.url= #数据库url连接
spring.shardingsphere.datasource.<data-source-name>.username= #数据库用户名
spring.shardingsphere.datasource.<data-source-name>.password= #数据库密码
spring.shardingsphere.datasource.<data-source-name>.xxx= #数据库连接池的其它属性

spring.shardingsphere.sharding.tables.<logic-table-name>.actual-data-nodes= #由数据源名 + 表名组成，以小数点分隔。多个表以逗号分隔，支持inline表达式。缺省表示使用已知数据源与逻辑表名称生成数据节点，用于广播表（即每个库中都需要一个同样的表用于关联查询，多为字典表）或只分库不分表且所有库的表结构完全一致的情况

#分库策略，缺省表示使用默认分库策略，以下的分片策略只能选其一

#用于单分片键的标准分片场景
spring.shardingsphere.sharding.tables.<logic-table-name>.database-strategy.standard.sharding-column= #分片列名称
spring.shardingsphere.sharding.tables.<logic-table-name>.database-strategy.standard.precise-algorithm-class-name= #精确分片算法类名称，用于=和IN。该类需实现PreciseShardingAlgorithm接口并提供无参数的构造器
spring.shardingsphere.sharding.tables.<logic-table-name>.database-strategy.standard.range-algorithm-class-name= #范围分片算法类名称，用于BETWEEN，可选。该类需实现RangeShardingAlgorithm接口并提供无参数的构造器

#用于多分片键的复合分片场景
spring.shardingsphere.sharding.tables.<logic-table-name>.database-strategy.complex.sharding-columns= #分片列名称，多个列以逗号分隔
spring.shardingsphere.sharding.tables.<logic-table-name>.database-strategy.complex.algorithm-class-name= #复合分片算法类名称。该类需实现ComplexKeysShardingAlgorithm接口并提供无参数的构造器

#行表达式分片策略
spring.shardingsphere.sharding.tables.<logic-table-name>.database-strategy.inline.sharding-column= #分片列名称
spring.shardingsphere.sharding.tables.<logic-table-name>.database-strategy.inline.algorithm-expression= #分片算法行表达式，需符合groovy语法

#Hint分片策略
spring.shardingsphere.sharding.tables.<logic-table-name>.database-strategy.hint.algorithm-class-name= #Hint分片算法类名称。该类需实现HintShardingAlgorithm接口并提供无参数的构造器

#分表策略，同分库策略
spring.shardingsphere.sharding.tables.<logic-table-name>.table-strategy.xxx= #省略

spring.shardingsphere.sharding.key-generators.<key-generator-name>.type= #自增列值生成器类型，缺省表示使用默认自增列值生成器。可使用用户自定义的列值生成器或选择内置类型：SNOWFLAKE/UUID
spring.shardingsphere.sharding.key-generators.<key-generator-name>.properties.<property-name>= #属性配置, 注意：使用SNOWFLAKE算法，需要配置worker.id与max.tolerate.time.difference.milliseconds属性。若使用此算法生成值作分片值，建议配置max.vibration.offset属性

spring.shardingsphere.sharding.tables.<logic-table-name>.key-generate-strategy.column= #自增列名称，缺省表示不使用自增主键生成器
spring.shardingsphere.sharding.tables.<logic-table-name>.key-generate-strategy.key-generator-name= #自增列算法名称，缺省表示使用雪花算法

spring.shardingsphere.sharding.binding-tables[0]= #绑定表规则列表
spring.shardingsphere.sharding.binding-tables[1]= #绑定表规则列表
spring.shardingsphere.sharding.binding-tables[x]= #绑定表规则列表

spring.shardingsphere.sharding.broadcast-tables[0]= #广播表规则列表
spring.shardingsphere.sharding.broadcast-tables[1]= #广播表规则列表
spring.shardingsphere.sharding.broadcast-tables[x]= #广播表规则列表

spring.shardingsphere.sharding.default-data-source-name= #未配置分片规则的表将通过默认数据源定位
spring.shardingsphere.sharding.default-database-strategy.xxx= #默认数据库分片策略，同分库策略
spring.shardingsphere.sharding.default-table-strategy.xxx= #默认表分片策略，同分表策略

spring.shardingsphere.rules.master-slave.load-balancers.<load-balancer-name>.type=#

spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.master-data-source-name= #详见读写分离部分
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.slave-data-source-names[0]= #详见读写分离部分
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.slave-data-source-names[1]= #详见读写分离部分
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.slave-data-source-names[x]= #详见读写分离部分
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.load-balance-algorithm-class-name= #详见读写分离部分
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.load-balancer-name= #详见读写分离部分

spring.shardingsphere.properties.sql.show= #是否开启SQL显示，默认值: false
spring.shardingsphere.properties.executor.size= #工作线程数量，默认值: CPU核数
```

### 读写分离

```properties
#省略数据源配置，与数据分片一致

spring.shardingsphere.rules.master-slave.load-balancers.<load-balancer-name>.type=#

spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.master-data-source-name= #主库数据源名称
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.slave-data-source-names[0]= #从库数据源名称列表
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.slave-data-source-names[1]= #从库数据源名称列表
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.slave-data-source-names[x]= #从库数据源名称列表
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.load-balance-algorithm-class-name= #从库负载均衡算法类名称。该类需实现MasterSlaveLoadBalanceAlgorithm接口且提供无参数构造器
spring.shardingsphere.sharding.master-slave-rules.<master-slave-data-source-name>.load-balancer-name= #从库负载均衡算法名称

spring.shardingsphere.properties.sql.show= #是否开启SQL显示，默认值: false
spring.shardingsphere.properties.executor.size= #工作线程数量，默认值: CPU核数
spring.shardingsphere.properties.check.table.metadata.enabled= #是否在启动时检查分表元数据一致性，默认值: false
```

### 数据加密
```properties
#省略数据源配置，与数据分片一致

spring.shardingsphere.encrypt.encryptors.<encrypt-algorithm-name>.type= #加解密算法类型，可自定义或选择内置类型：MD5/AES
spring.shardingsphere.encrypt.encryptors.<encrypt-algorithm-name>.properties.<property-name>= #属性配置, 注意：使用 AES 加密算法，需要配置 AES 加密算法的 KEY 属性：aes.key.value
spring.shardingsphere.encrypt.tables.<table-name>.columns.<logic-column-name>.cipher-column= #存储密文的字段
spring.shardingsphere.encrypt.tables.<table-name>.columns.<logic-column-name>.assisted-query-column= #辅助查询字段，针对 QueryAssistedEncryptAlgorithm 类型的加解密算法进行辅助查询
spring.shardingsphere.encrypt.tables.<table-name>.columns.<logic-column-name>.plain-column= #存储明文的字段
spring.shardingsphere.encrypt.tables.<table-name>.columns.<logic-column-name>.encryptor-name= #加密算法名字
```
### 治理

```properties
#省略数据源、数据分片、读写分离和数据加密配置

spring.shardingsphere.orchestration.spring_boot_ds_sharding.orchestration-type= #治理类型，例如config_center/registry_center/metadata_center
spring.shardingsphere.orchestration.spring_boot_ds_sharding.instance-type= #配置/注册/元数据中心实例类型。如：zookeeper
spring.shardingsphere.orchestration.spring_boot_ds_sharding.server-lists= #连接注册/配置/元数据中心服务器的列表。包括IP地址和端口号。多个地址用逗号分隔。如: host1:2181,host2:2181
spring.shardingsphere.orchestration.spring_boot_ds_sharding.namespace= #注册/配置/元数据中心的命名空间
spring.shardingsphere.orchestration.spring_boot_ds_sharding.properties.overwrite=true #本地配置是否覆盖配置中心配置。如果可覆盖，每次启动都以本地配置为准
spring.shardingsphere.orchestration.spring_boot_ds_sharding.properties.digest= #连接配置/注册/元数据中心的权限令牌。缺省为不需要权限验证
spring.shardingsphere.orchestration.spring_boot_ds_sharding.properties.operation-timeout-milliseconds= #操作超时的毫秒数，默认500毫秒
spring.shardingsphere.orchestration.spring_boot_ds_sharding.properties.max-retries= #连接失败后的最大重试次数，默认3次
spring.shardingsphere.orchestration.spring_boot_ds_sharding.properties.retry-interval-milliseconds= #重试间隔毫秒数，默认500毫秒
spring.shardingsphere.orchestration.spring_boot_ds_sharding.properties.time-to-live-seconds= #临时节点存活秒数，默认60秒
```

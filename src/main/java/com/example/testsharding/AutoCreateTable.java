//package com.example.testsharding;
//
//
//import groovy.util.logging.Log4j2;
//import io.netty.util.internal.ConcurrentSet;
//import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
//import org.apache.shardingsphere.infra.config.props.ConfigurationProperties;
//import org.apache.shardingsphere.infra.rewrite.context.SQLRewriteContext;
//import org.apache.shardingsphere.infra.rewrite.context.SQLRewriteContextDecorator;
//import org.apache.shardingsphere.infra.route.context.RouteContext;
//import org.apache.shardingsphere.infra.route.context.RouteUnit;
//import org.apache.shardingsphere.sharding.rewrite.context.ShardingSQLRewriteContextDecorator;
//import org.apache.shardingsphere.sharding.rule.ShardingRule;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.time.LocalDateTime;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.Set;
//
//@Log4j2
//public class AutoCreateTable implements SQLRewriteContextDecorator<ShardingRule> {
//
//    private static final Set<String> autoCreateTables = new ConcurrentSet<>();
//    private static volatile ShardingSphereDataSource source;
//    private static volatile int deleteDay = 360;
//    private static Set<String> existsTableNames;
//
//    private static final ShardingSQLRewriteContextDecorator ssrcd = new ShardingSQLRewriteContextDecorator();
//
//
//    private static void init() {
//        if (null == existsTableNames) {
//            synchronized (autoCreateTables) {
//                if (null == existsTableNames) {
//
//                    String cacheType = BeanStaticUtils.getConfigValue("spring.cache.type", String.class, "");
//                    if ("redis".equals(cacheType)) {
//                        StringRedisTemplate redisTemplate = BeanStaticUtils.getBean("stringRedisTemplate", StringRedisTemplate.class);
//                        existsTableNames = new DefaultRedisSet<String>("__AutoCreateTable.existsTableNames__", redisTemplate);
//                    } else {
//                        existsTableNames = new ConcurrentHashSet<>(64);
//                    }
//
//                    autoCreateTables.addAll(BeanStaticUtils.getConfigValue("spring.shardingsphere.autoCreateTables.names", Set.class, new HashSet()));
//                    deleteDay = BeanStaticUtils.getConfigValue("spring.shardingsphere.autoCreateTables.delete-day", Integer.class, 360);
//                    if (deleteDay <= 0)
//                        deleteDay = 360;
//                    source = BeanStaticUtils.getBean(ShardingSphereDataSource.class);
//                    if (autoCreateTables.isEmpty()) {
//                        log.warn("autoCreateTables is empty");
//                        return;
//                    }
//                    new ShardingTableHistory().selectList(o -> o.in(e -> e.getLogicTableName(), autoCreateTables)
//                                    .select(e -> e.getActualTableName(), e -> e.getDbName()))
//                            .stream().map(m -> m.getDbName() + "." + m.getActualTableName()).forEach( e -> existsTableNames.add(e.toLowerCase()) );
//                }
//            }
//        }
//
//    }
//
//    /**
//     * Decorate SQL rewrite context.
//     *
//     * @param rule              rule
//     * @param props             ShardingSphere properties
//     * @param sqlRewriteContext SQL rewrite context to be decorated
//     * @param routeContext      route context
//     */
//    @Override
//    public void decorate(ShardingRule rule, ConfigurationProperties props, SQLRewriteContext sqlRewriteContext, RouteContext routeContext) {
//        ssrcd.decorate(rule, props, sqlRewriteContext, routeContext);
//
//        init();
//
//        Collection<RouteUnit> routeUnits = routeContext.getRouteUnits();
//        for (RouteUnit routeUnit : routeUnits) {
//            for (String logicTableName : routeUnit.getLogicTableNames()) {
//                if (!autoCreateTables.contains(logicTableName)) {
//                    continue;
//                }
//
//                Set<String> actualTableNames = routeUnit.getActualTableNames(logicTableName);
//                String datasourceName = routeUnit.getDataSourceMapper().getActualName();
//
//
//                for (String tableName : actualTableNames) {
//
//                    String key = datasourceName + "." + tableName;
//
//                    if (existsTableNames.contains(key)) {
//                        continue;
//                    }
//
//                    synchronized (existsTableNames) {
//
//                        if (existsTableNames.contains(key)) {
//                            continue;
//                        }
//                        try (Connection connection = source.getConnection();
//                             Statement statement = connection.createStatement()) {
//                            existsTableNames.add(key);
//                            statement.execute("create table IF NOT EXISTS " + tableName + " AS SELECT * FROM " + logicTableName + " WHERE 1 = 2 ");
//                            ShardingTableHistory shardingTableHistory = new ShardingTableHistory();
//                            shardingTableHistory.setLogicTableName(logicTableName);
//                            shardingTableHistory.setActualTableName(tableName);
//                            shardingTableHistory.setCreateTime(LocalDateTime.now());
//                            shardingTableHistory.setStatus("nominal");
//                            shardingTableHistory.setDbName(datasourceName);
//                            shardingTableHistory.setPlanDeleteTime(LocalDateTime.now().plusDays(deleteDay));
//                            shardingTableHistory.insert();
//
//                        } catch (SQLException e) {
//                            log.error(e.getMessage(), e);
//                        }
//                    }
//
//                }
//
//            }
//        }
//    }
//
//    /**
//     * Get order of load.
//     *
//     * @return load order
//     */
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//
//    /**
//     * Get type class.
//     *
//     * @return type class
//     */
//    @Override
//    public Class<ShardingRule> getTypeClass() {
//        return ShardingRule.class;
//    }
//}
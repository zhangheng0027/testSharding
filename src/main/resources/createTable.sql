-- oee.equipment_oee definition

CREATE TABLE `equipment_oee`
(
    `serial_no`                bigint       NOT NULL AUTO_INCREMENT,
    `equipment_id`             varchar(100) NOT NULL COMMENT '设备编号',
    `statistics_cycle`         varchar(10)  NOT NULL COMMENT '统计周期 A、1分钟；B、5分钟；C、30 分钟；D、1小时；E、1天',
    `statistics_time`          varchar(100) NOT NULL COMMENT '开始统计时间，yyyyMMddHHmm，如果统计周期是天 201301010000',
    `lastest_status_serial_no` bigint       NOT NULL COMMENT '最后未更新的状态编号',
    `oee_ratio` double DEFAULT NULL COMMENT 'oee 指标',
    `availability` double DEFAULT NULL COMMENT '可用率',
    `effectiveness_performance` double DEFAULT NULL COMMENT '设备性能效率',
    `quality_ratio` double DEFAULT NULL COMMENT '质量指数',
    `actual_production_time`   bigint DEFAULT NULL COMMENT '实际生产时间（秒），根据设备状态确定',
    `plan_occupancy_time`      bigint DEFAULT NULL COMMENT '计划占用时间，如果取不到，则整个周期内多属于计划占用时间',
    `beat_time` double DEFAULT NULL COMMENT '节拍时间（秒），每个对象的计划运行时间',
    `produced_quantity`        bigint DEFAULT NULL COMMENT '统计周期内已生产数量',
    `good_quantity`            bigint DEFAULT NULL COMMENT '统计周期内合格品数量',
    PRIMARY KEY (`serial_no`),
    KEY                        `equipment_oee_statistics_time_IDX` (`statistics_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1699337705471848450 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='oee 相关指标';


-- oee.equipment_status definition

CREATE TABLE `equipment_status`
(
    `serial_no`        bigint       NOT NULL AUTO_INCREMENT,
    `equipment_id`     varchar(100) NOT NULL COMMENT '设备编号',
    `status`           varchar(100) NOT NULL COMMENT '当前状态 码值 equipmentStatus',
    `begin_time`       timestamp    NOT NULL COMMENT '状态的开始时间',
    `end_time`         timestamp NULL DEFAULT NULL COMMENT '状态的结束时间',
    `duration`         bigint DEFAULT NULL COMMENT '持续时间 ms',
    `statistical_date` date   DEFAULT NULL COMMENT '统计日期',
    `left_serial_no`   bigint DEFAULT NULL COMMENT '上一状态的 id',
    `right_serial_no`  bigint DEFAULT NULL COMMENT '下一状态 id',
    PRIMARY KEY (`serial_no`),
    KEY                `equipment_status_begin_time_IDX` (`begin_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1699326721709559810 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备状态表';
package com.example.testsharding;


import com.baomidou.mybatisplus.core.toolkit.Sequence;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class OeeCycleAlgorithmSharding implements ComplexKeysShardingAlgorithm<String> {

    /**
     * Sharding.
     *
     * @param availableTargetNames available data sources or tables's names
     * @param shardingValue        sharding value
     * @return sharding results for data sources or tables's names
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> shardingValue) {
        Map<String, Collection<String>> columnNameAndShardingValuesMap = shardingValue.getColumnNameAndShardingValuesMap();

        Collection<String> statistics_cycle = columnNameAndShardingValuesMap.getOrDefault("statistics_cycle", Collections.EMPTY_LIST);
        Collection<String> statistics_time = columnNameAndShardingValuesMap.getOrDefault("statistics_time", Collections.EMPTY_LIST);
        Collection<Long> serialNo = columnNameAndShardingValuesMap.getOrDefault("serial_no", Collections.EMPTY_LIST);

        Set<String> result = new HashSet<>();

        boolean flag = false;

        for (var s : statistics_cycle) {
            if (!flag && ("A".equals(s) || "B".equals(s) || "C".equals(s))) {
                flag = true;
            } else {
                result.add(shardingValue.getLogicTableName());
            }
        }

        if (flag) {

            for (String s : statistics_time) {
                String substring = s.substring(0, 6);
                result.add(shardingValue.getLogicTableName() + "_" + Integer.valueOf(substring));
            }
        }

        if (result.isEmpty()) {
            // 没有带周期sql
            result.addAll(availableTargetNames);
            for (Long aLong : serialNo) {
                /** @see  Sequence#twepoch */
                if (aLong < 1288834974657L)
                    continue;
                String year = handleYearMonth(aLong);
                result.add(shardingValue.getLogicTableName() + "_" + year);
            }
        }
        return result;
    }


    /**
     *  解析雪花算法生成的年份
     * @param serialNo mybatis plus 雪花算法生成的id
     * @return yyyyMM 格式的年月
     */
    private static String handleYearMonth(long serialNo) {

        Instant currentInstant = Instant.ofEpochMilli(Sequence.parseIdTimestamp(serialNo));
        LocalDate currentDate = currentInstant.atZone(ZoneId.systemDefault()).toLocalDate();
        return currentDate.getYear() + (currentDate.getMonthValue() > 10 ? "" + currentDate.getMonthValue() : "0" + currentDate.getMonthValue());
    }

    /**
     * Initialize SPI.
     *
     * @param props properties to be initialized
     */
    @Override
    public void init(Properties props) {

    }
}
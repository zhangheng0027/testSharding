package com.example.testsharding;

import com.example.testsharding.generator.service.EquipmentOeeService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest
class TestShardingApplicationTests {

//    @Resource
//    EquipmentOeeService equipmentOeeService;
//
//    @Test
//    public void test01() {
//        equipmentOeeService.test01();
//    }


    @Resource
    DataSource dataSource;

    @Test
    public void test02() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("select * from equipment_oee").execute();
            connection.prepareStatement("select * from equipment_status").execute();
        }
        // result
        // ShardingSphere-SQL                       : Logic SQL: select * from equipment_oee
        // ShardingSphere-SQL                       : Actual SQL: aa ::: select * from equipment_oee
        // ShardingSphere-SQL                       : Logic SQL: select * from equipment_status
        // ShardingSphere-SQL                       : Actual SQL: aa ::: select * from equipment_status
    }


}

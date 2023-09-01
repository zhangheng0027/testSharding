package com.example.testsharding;

import com.example.testsharding.generator.service.EquipmentOeeService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestShardingApplicationTests {

    @Resource
    EquipmentOeeService equipmentOeeService;

    @Test
    public void test01() {
        equipmentOeeService.test01();
    }

}

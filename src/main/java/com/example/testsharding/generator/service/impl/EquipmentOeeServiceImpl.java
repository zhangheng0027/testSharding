package com.example.testsharding.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.testsharding.generator.domain.EquipmentOee;
import com.example.testsharding.generator.service.EquipmentOeeService;
import com.example.testsharding.generator.mapper.EquipmentOeeMapper;
import org.springframework.stereotype.Service;

/**
* @author hzhang
* @description 针对表【equipment_oee(oee 相关指标)】的数据库操作Service实现
* @createDate 2023-08-29 10:52:43
*/
@Service
public class EquipmentOeeServiceImpl extends ServiceImpl<EquipmentOeeMapper, EquipmentOee>
    implements EquipmentOeeService{


    @Override
    public void test01() {
        EquipmentOee oee = new EquipmentOee();
        oee.setEquipmentId("ab");
        oee.setStatisticsCycle("A");
        oee.setStatisticsTime("202308121052");
        oee.setLastestStatusSerialNo(1L);
        save(oee);
    }

}





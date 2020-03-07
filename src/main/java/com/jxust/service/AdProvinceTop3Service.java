package com.jxust.service;

import java.util.List;
import com.jxust.entity.AdProvinceTop3;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface AdProvinceTop3Service{


    int insert(AdProvinceTop3 record);

    int insertSelective(AdProvinceTop3 record);

    int batchInsert(List<AdProvinceTop3> list);

    List<AdProvinceTop3> getAll();
}

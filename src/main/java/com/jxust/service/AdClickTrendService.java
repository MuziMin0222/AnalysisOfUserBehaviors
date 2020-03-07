package com.jxust.service;

import java.util.List;
import com.jxust.entity.AdClickTrend;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface AdClickTrendService{


    int insert(AdClickTrend record);

    int insertSelective(AdClickTrend record);

    int batchInsert(List<AdClickTrend> list);

    List<AdClickTrend> getAll();
}

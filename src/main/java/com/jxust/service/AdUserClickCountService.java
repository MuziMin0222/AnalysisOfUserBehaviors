package com.jxust.service;

import java.util.List;

import com.jxust.entity.AdStat;
import com.jxust.entity.AdUserClickCount;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface AdUserClickCountService{


    int insert(AdUserClickCount record);

    int insertSelective(AdUserClickCount record);

    int batchInsert(List<AdUserClickCount> list);

    List<AdUserClickCount> getAll();
}

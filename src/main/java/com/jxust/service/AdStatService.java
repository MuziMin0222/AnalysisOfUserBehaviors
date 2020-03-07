package com.jxust.service;

import java.util.List;
import com.jxust.entity.AdStat;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface AdStatService{


    int insert(AdStat record);

    int insertSelective(AdStat record);

    int batchInsert(List<AdStat> list);

    List<AdStat> getAll();
}

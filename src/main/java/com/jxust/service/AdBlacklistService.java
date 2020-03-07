package com.jxust.service;

import com.jxust.entity.AdBlacklist;
import java.util.List;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface AdBlacklistService{


    int insert(AdBlacklist record);

    int insertSelective(AdBlacklist record);

    int batchInsert(List<AdBlacklist> list);

    List<AdBlacklist> getAll();
}

package com.jxust.service;

import java.util.List;
import com.jxust.entity.SessionAggrStat;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface SessionAggrStatService{


    int insert(SessionAggrStat record);

    int insertSelective(SessionAggrStat record);

    int batchInsert(List<SessionAggrStat> list);

    List<SessionAggrStat> getAll();
}

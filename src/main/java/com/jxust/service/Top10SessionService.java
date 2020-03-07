package com.jxust.service;

import java.util.List;
import com.jxust.entity.Top10Session;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface Top10SessionService{


    int insert(Top10Session record);

    int insertSelective(Top10Session record);

    int batchInsert(List<Top10Session> list);

    List<Top10Session> getAll();
}

package com.jxust.service;

import java.util.List;
import com.jxust.entity.SessionDetail;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface SessionDetailService{


    int insert(SessionDetail record);

    int insertSelective(SessionDetail record);

    int batchInsert(List<SessionDetail> list);

    List<SessionDetail> getAll();
}

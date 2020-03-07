package com.jxust.service;

import java.util.List;
import com.jxust.entity.SessionRandomExtract;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface SessionRandomExtractService{


    int insert(SessionRandomExtract record);

    int insertSelective(SessionRandomExtract record);

    int batchInsert(List<SessionRandomExtract> list);

    List<SessionRandomExtract> getAll();
}

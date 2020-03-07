package com.jxust.service;

import java.util.List;
import com.jxust.entity.AreaTop3Product;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface AreaTop3ProductService{


    int insert(AreaTop3Product record);

    int insertSelective(AreaTop3Product record);

    int batchInsert(List<AreaTop3Product> list);

    List<AreaTop3Product> getAll();
}

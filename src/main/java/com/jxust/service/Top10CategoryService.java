package com.jxust.service;

import java.util.List;
import com.jxust.entity.Top10Category;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface Top10CategoryService{


    int insert(Top10Category record);

    int insertSelective(Top10Category record);

    int batchInsert(List<Top10Category> list);

    List<Top10Category> getAll();
}

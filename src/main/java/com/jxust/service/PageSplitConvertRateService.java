package com.jxust.service;

import java.util.List;
import com.jxust.entity.PageSplitConvertRate;
    
/**
* Create by Jayden in 2020/3/7
*/

public interface PageSplitConvertRateService{


    int insert(PageSplitConvertRate record);

    int insertSelective(PageSplitConvertRate record);

    int batchInsert(List<PageSplitConvertRate> list);

    List<PageSplitConvertRate> getAll();
}

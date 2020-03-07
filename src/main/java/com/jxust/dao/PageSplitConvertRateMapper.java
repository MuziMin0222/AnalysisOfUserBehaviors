package com.jxust.dao;

import com.jxust.entity.PageSplitConvertRate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface PageSplitConvertRateMapper {
    int insert(PageSplitConvertRate record);

    int insertSelective(PageSplitConvertRate record);

    int batchInsert(@Param("list") List<PageSplitConvertRate> list);

    List<PageSplitConvertRate> findAll();


}
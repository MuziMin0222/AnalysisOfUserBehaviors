package com.jxust.dao;

import com.jxust.entity.AdClickTrend;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface AdClickTrendMapper {
    int insert(AdClickTrend record);

    int insertSelective(AdClickTrend record);

    int batchInsert(@Param("list") List<AdClickTrend> list);

    List<AdClickTrend> findAll();


}
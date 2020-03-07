package com.jxust.dao;

import com.jxust.entity.AdStat;
import com.jxust.entity.AdUserClickCount;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface AdUserClickCountMapper {
    int insert(AdUserClickCount record);

    int insertSelective(AdUserClickCount record);

    int batchInsert(@Param("list") List<AdUserClickCount> list);

    List<AdUserClickCount> findAll();


}
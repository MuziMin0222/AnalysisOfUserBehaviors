package com.jxust.dao;

import com.jxust.entity.AdStat;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface AdStatMapper {
    int insert(AdStat record);

    int insertSelective(AdStat record);

    int batchInsert(@Param("list") List<AdStat> list);

    List<AdStat> findAll();

}
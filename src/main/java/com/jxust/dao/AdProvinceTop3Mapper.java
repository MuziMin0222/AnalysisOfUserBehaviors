package com.jxust.dao;

import com.jxust.entity.AdProvinceTop3;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface AdProvinceTop3Mapper {
    int insert(AdProvinceTop3 record);

    int insertSelective(AdProvinceTop3 record);

    int batchInsert(@Param("list") List<AdProvinceTop3> list);

    List<AdProvinceTop3> findAll();


}
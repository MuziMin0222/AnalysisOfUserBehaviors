package com.jxust.dao;

import com.jxust.entity.AreaTop3Product;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface AreaTop3ProductMapper {
    int insert(AreaTop3Product record);

    int insertSelective(AreaTop3Product record);

    int batchInsert(@Param("list") List<AreaTop3Product> list);

    List<AreaTop3Product> findAll();


}
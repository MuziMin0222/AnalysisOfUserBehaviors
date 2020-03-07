package com.jxust.dao;

import com.jxust.entity.Top10Session;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface Top10SessionMapper {
    int insert(Top10Session record);

    int insertSelective(Top10Session record);

    int batchInsert(@Param("list") List<Top10Session> list);

    List<Top10Session> findAll();


}
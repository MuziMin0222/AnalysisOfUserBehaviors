package com.jxust.dao;

import com.jxust.entity.Top10Category;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface Top10CategoryMapper {
    int insert(Top10Category record);

    int insertSelective(Top10Category record);

    int batchInsert(@Param("list") List<Top10Category> list);

    List<Top10Category> findAll();


}
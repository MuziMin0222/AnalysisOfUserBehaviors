package com.jxust.dao;

import com.jxust.entity.SessionAggrStat;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface SessionAggrStatMapper {
    int insert(SessionAggrStat record);

    int insertSelective(SessionAggrStat record);

    int batchInsert(@Param("list") List<SessionAggrStat> list);

    List<SessionAggrStat> findAll();


}
package com.jxust.dao;

import com.jxust.entity.SessionDetail;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface SessionDetailMapper {
    int insert(SessionDetail record);

    int insertSelective(SessionDetail record);

    int batchInsert(@Param("list") List<SessionDetail> list);

    List<SessionDetail> findAll();


}
package com.jxust.dao;

import com.jxust.entity.SessionRandomExtract;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface SessionRandomExtractMapper {
    int insert(SessionRandomExtract record);

    int insertSelective(SessionRandomExtract record);

    int batchInsert(@Param("list") List<SessionRandomExtract> list);

    List<SessionRandomExtract> findAll();


}
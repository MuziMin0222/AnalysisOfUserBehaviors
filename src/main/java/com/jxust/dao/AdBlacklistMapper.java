package com.jxust.dao;

import com.jxust.entity.AdBlacklist;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* Create by Jayden in 2020/3/7
*/

@Mapper
public interface AdBlacklistMapper {
    int insert(AdBlacklist record);

    int insertSelective(AdBlacklist record);

    int batchInsert(@Param("list") List<AdBlacklist> list);

    List<AdBlacklist> findAll();


}
package com.jxust.service.impl;

import com.jxust.entity.AdStat;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.jxust.entity.AdUserClickCount;
import com.jxust.dao.AdUserClickCountMapper;
import com.jxust.service.AdUserClickCountService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class AdUserClickCountServiceImpl implements AdUserClickCountService{

    @Resource
    private AdUserClickCountMapper adUserClickCountMapper;

    @Override
    public int insert(AdUserClickCount record) {
        return adUserClickCountMapper.insert(record);
    }

    @Override
    public int insertSelective(AdUserClickCount record) {
        return adUserClickCountMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<AdUserClickCount> list) {
        return adUserClickCountMapper.batchInsert(list);
    }

    @Override
    public List<AdUserClickCount> getAll() {
        return adUserClickCountMapper.findAll();
    }

}

package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.jxust.dao.AdStatMapper;
import com.jxust.entity.AdStat;
import com.jxust.service.AdStatService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class AdStatServiceImpl implements AdStatService{

    @Resource
    private AdStatMapper adStatMapper;

    @Override
    public int insert(AdStat record) {
        return adStatMapper.insert(record);
    }

    @Override
    public int insertSelective(AdStat record) {
        return adStatMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<AdStat> list) {
        return adStatMapper.batchInsert(list);
    }

    @Override
    public List<AdStat> getAll() {
        return adStatMapper.findAll();
    }

}

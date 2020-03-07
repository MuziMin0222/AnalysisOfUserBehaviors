package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.jxust.dao.AdClickTrendMapper;
import java.util.List;
import com.jxust.entity.AdClickTrend;
import com.jxust.service.AdClickTrendService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class AdClickTrendServiceImpl implements AdClickTrendService{

    @Resource
    private AdClickTrendMapper adClickTrendMapper;

    @Override
    public int insert(AdClickTrend record) {
        return adClickTrendMapper.insert(record);
    }

    @Override
    public int insertSelective(AdClickTrend record) {
        return adClickTrendMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<AdClickTrend> list) {
        return adClickTrendMapper.batchInsert(list);
    }

    @Override
    public List<AdClickTrend> getAll() {
        return adClickTrendMapper.findAll();
    }

}

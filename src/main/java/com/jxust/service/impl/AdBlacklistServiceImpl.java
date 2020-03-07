package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.jxust.entity.AdBlacklist;
import com.jxust.dao.AdBlacklistMapper;
import java.util.List;
import com.jxust.service.AdBlacklistService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class AdBlacklistServiceImpl implements AdBlacklistService{

    @Resource
    private AdBlacklistMapper adBlacklistMapper;

    @Override
    public int insert(AdBlacklist record) {
        return adBlacklistMapper.insert(record);
    }

    @Override
    public int insertSelective(AdBlacklist record) {
        return adBlacklistMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<AdBlacklist> list) {
        return adBlacklistMapper.batchInsert(list);
    }

    @Override
    public List<AdBlacklist> getAll() {
        return adBlacklistMapper.findAll();
    }

}

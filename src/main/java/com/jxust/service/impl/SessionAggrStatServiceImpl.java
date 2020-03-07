package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.jxust.dao.SessionAggrStatMapper;
import com.jxust.entity.SessionAggrStat;
import com.jxust.service.SessionAggrStatService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class SessionAggrStatServiceImpl implements SessionAggrStatService{

    @Resource
    private SessionAggrStatMapper sessionAggrStatMapper;

    @Override
    public int insert(SessionAggrStat record) {
        return sessionAggrStatMapper.insert(record);
    }

    @Override
    public int insertSelective(SessionAggrStat record) {
        return sessionAggrStatMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<SessionAggrStat> list) {
        return sessionAggrStatMapper.batchInsert(list);
    }

    @Override
    public List<SessionAggrStat> getAll() {
        return sessionAggrStatMapper.findAll();
    }

}

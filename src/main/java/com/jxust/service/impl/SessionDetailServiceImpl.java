package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.jxust.dao.SessionDetailMapper;
import java.util.List;
import com.jxust.entity.SessionDetail;
import com.jxust.service.SessionDetailService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class SessionDetailServiceImpl implements SessionDetailService{

    @Resource
    private SessionDetailMapper sessionDetailMapper;

    @Override
    public int insert(SessionDetail record) {
        return sessionDetailMapper.insert(record);
    }

    @Override
    public int insertSelective(SessionDetail record) {
        return sessionDetailMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<SessionDetail> list) {
        return sessionDetailMapper.batchInsert(list);
    }

    @Override
    public List<SessionDetail> getAll() {
        return sessionDetailMapper.findAll();
    }

}

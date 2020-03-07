package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.jxust.entity.SessionRandomExtract;
import com.jxust.dao.SessionRandomExtractMapper;
import com.jxust.service.SessionRandomExtractService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class SessionRandomExtractServiceImpl implements SessionRandomExtractService{

    @Resource
    private SessionRandomExtractMapper sessionRandomExtractMapper;

    @Override
    public int insert(SessionRandomExtract record) {
        return sessionRandomExtractMapper.insert(record);
    }

    @Override
    public int insertSelective(SessionRandomExtract record) {
        return sessionRandomExtractMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<SessionRandomExtract> list) {
        return sessionRandomExtractMapper.batchInsert(list);
    }

    @Override
    public List<SessionRandomExtract> getAll() {
        return sessionRandomExtractMapper.findAll();
    }

}

package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.jxust.dao.Top10SessionMapper;
import com.jxust.entity.Top10Session;
import com.jxust.service.Top10SessionService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class Top10SessionServiceImpl implements Top10SessionService{

    @Resource
    private Top10SessionMapper top10SessionMapper;

    @Override
    public int insert(Top10Session record) {
        return top10SessionMapper.insert(record);
    }

    @Override
    public int insertSelective(Top10Session record) {
        return top10SessionMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<Top10Session> list) {
        return top10SessionMapper.batchInsert(list);
    }

    @Override
    public List<Top10Session> getAll() {
        return top10SessionMapper.findAll();
    }

}

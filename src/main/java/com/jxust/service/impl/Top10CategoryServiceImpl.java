package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.jxust.dao.Top10CategoryMapper;
import com.jxust.entity.Top10Category;
import com.jxust.service.Top10CategoryService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class Top10CategoryServiceImpl implements Top10CategoryService{

    @Resource
    private Top10CategoryMapper top10CategoryMapper;

    @Override
    public int insert(Top10Category record) {
        return top10CategoryMapper.insert(record);
    }

    @Override
    public int insertSelective(Top10Category record) {
        return top10CategoryMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<Top10Category> list) {
        return top10CategoryMapper.batchInsert(list);
    }

    @Override
    public List<Top10Category> getAll() {
        return top10CategoryMapper.findAll();
    }

}

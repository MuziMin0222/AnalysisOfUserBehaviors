package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.jxust.entity.AreaTop3Product;
import com.jxust.dao.AreaTop3ProductMapper;
import com.jxust.service.AreaTop3ProductService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class AreaTop3ProductServiceImpl implements AreaTop3ProductService{

    @Resource
    private AreaTop3ProductMapper areaTop3ProductMapper;

    @Override
    public int insert(AreaTop3Product record) {
        return areaTop3ProductMapper.insert(record);
    }

    @Override
    public int insertSelective(AreaTop3Product record) {
        return areaTop3ProductMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<AreaTop3Product> list) {
        return areaTop3ProductMapper.batchInsert(list);
    }

    @Override
    public List<AreaTop3Product> getAll() {
        return areaTop3ProductMapper.findAll();
    }

}

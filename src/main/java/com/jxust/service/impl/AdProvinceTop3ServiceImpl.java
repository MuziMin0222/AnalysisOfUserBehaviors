package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.jxust.entity.AdProvinceTop3;
import com.jxust.dao.AdProvinceTop3Mapper;
import com.jxust.service.AdProvinceTop3Service;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class AdProvinceTop3ServiceImpl implements AdProvinceTop3Service{

    @Resource
    private AdProvinceTop3Mapper adProvinceTop3Mapper;

    @Override
    public int insert(AdProvinceTop3 record) {
        return adProvinceTop3Mapper.insert(record);
    }

    @Override
    public int insertSelective(AdProvinceTop3 record) {
        return adProvinceTop3Mapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<AdProvinceTop3> list) {
        return adProvinceTop3Mapper.batchInsert(list);
    }

    @Override
    public List<AdProvinceTop3> getAll() {
        return adProvinceTop3Mapper.findAll();
    }

}

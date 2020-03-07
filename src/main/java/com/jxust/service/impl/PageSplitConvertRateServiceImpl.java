package com.jxust.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.jxust.entity.PageSplitConvertRate;
import com.jxust.dao.PageSplitConvertRateMapper;
import com.jxust.service.PageSplitConvertRateService;

/**
* Create by Jayden in 2020/3/7
*/

@Service
public class PageSplitConvertRateServiceImpl implements PageSplitConvertRateService{

    @Resource
    private PageSplitConvertRateMapper pageSplitConvertRateMapper;

    @Override
    public int insert(PageSplitConvertRate record) {
        return pageSplitConvertRateMapper.insert(record);
    }

    @Override
    public int insertSelective(PageSplitConvertRate record) {
        return pageSplitConvertRateMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<PageSplitConvertRate> list) {
        return pageSplitConvertRateMapper.batchInsert(list);
    }

    @Override
    public List<PageSplitConvertRate> getAll() {
        return pageSplitConvertRateMapper.findAll();
    }

}

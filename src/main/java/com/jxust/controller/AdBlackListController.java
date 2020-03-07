package com.jxust.controller;

import com.jxust.entity.AdBlacklist;
import com.jxust.service.AdBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by Jayden in 2020/3/7
 */
@RestController
@RequestMapping(value = "/api")
public class AdBlackListController {

    @Autowired
    private AdBlacklistService adBlacklistService;

    @GetMapping(value = "/ad/black")
    public Map<String,Object[]> getAll(){
        /**
         * 1. 查出所有的数据
         * 2. 封装成一个数组
         */
        List<AdBlacklist> adBlacklistList = adBlacklistService.getAll();
        Object[] ids = adBlacklistList.stream().map(AdBlacklist::getUserid).collect(Collectors.toList()).toArray();
        Map<String,Object[]> result = new HashMap<>();
        result.put("data",ids);
        return result;
    }
}

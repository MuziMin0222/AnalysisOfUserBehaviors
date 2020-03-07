package com.jxust.controller;

import com.jxust.entity.AdClickTrend;
import com.jxust.service.AdClickTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by Jayden in 2020/3/7
 */
@RestController
@RequestMapping(value = "/api")
public class AdClickTrendController {

    @Autowired
    private AdClickTrendService adClickTrendService;

    @GetMapping("/ad/click/trend")
    public Map<String,Object> getAll(){
        List<AdClickTrend> list = adClickTrendService.getAll();
        Map<String,Object> result = new HashMap<>();
        result.put("data",list);
        return result;
    }
}

package com.jxust.controller;

import com.jxust.entity.AdProvinceTop3;
import com.jxust.service.AdProvinceTop3Service;
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
public class AdProvinceTop3Controller {

    @Autowired
    private AdProvinceTop3Service adProvinceTop3Service;

    @GetMapping(value = "/ad/province/top3")
    public Map<String,Object> getAll(){
        List<AdProvinceTop3> list = adProvinceTop3Service.getAll();
        Map<String,Object> result = new HashMap<>();
        result.put("data",list);
        return result;
    }
}

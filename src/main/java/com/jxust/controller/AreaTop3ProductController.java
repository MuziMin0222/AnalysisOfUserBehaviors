package com.jxust.controller;

import com.jxust.entity.AdUserClickCount;
import com.jxust.entity.AreaTop3Product;
import com.jxust.service.AreaTop3ProductService;
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
public class AreaTop3ProductController {

    @Autowired
    private AreaTop3ProductService areaTop3ProductService;

    @GetMapping(value = "/area/top3/product")
    public Map<String,Object> getAll(){
        Map<String,Object> result = new HashMap<>();
        List<AreaTop3Product> list = areaTop3ProductService.getAll();
        result.put("data",list);
        return result;
    }

}

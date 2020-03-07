package com.jxust.controller;

import com.jxust.entity.SessionRandomExtract;
import com.jxust.entity.Top10Category;
import com.jxust.service.Top10CategoryService;
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
public class Top10CategoryController {

    @Autowired
    private Top10CategoryService top10CategoryService;

    @GetMapping(value = "/top10/category")
    public Map<String,Object> getAll(){
        Map<String,Object> result = new HashMap<>();
        List<Top10Category> list = top10CategoryService.getAll();
        result.put("data",list);
        return result;
    }
}

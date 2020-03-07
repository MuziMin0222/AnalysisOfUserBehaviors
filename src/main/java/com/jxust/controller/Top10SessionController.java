package com.jxust.controller;

import com.jxust.entity.Top10Category;
import com.jxust.entity.Top10Session;
import com.jxust.service.Top10SessionService;
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
public class Top10SessionController {

    @Autowired
    private Top10SessionService top10SessionService;

    @GetMapping(value = "/top10/session")
    public Map<String,Object> getAll(){
        Map<String,Object> result = new HashMap<>();
        List<Top10Session> list = top10SessionService.getAll();
        result.put("data",list);
        return result;
    }
}

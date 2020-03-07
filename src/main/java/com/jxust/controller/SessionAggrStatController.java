package com.jxust.controller;

import com.jxust.entity.PageSplitConvertRate;
import com.jxust.entity.SessionAggrStat;
import com.jxust.service.SessionAggrStatService;
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
public class SessionAggrStatController {

    @Autowired
    private SessionAggrStatService sessionAggrStatService;


    @GetMapping(value = "/session/aggr/stat")
    public Map<String,Object> getAll(){
        Map<String,Object> result = new HashMap<>();
        List<SessionAggrStat> list = sessionAggrStatService.getAll();
        result.put("data",list);
        return result;
    }
}

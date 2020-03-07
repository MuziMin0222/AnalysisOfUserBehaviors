package com.jxust.controller;

import com.jxust.entity.SessionAggrStat;
import com.jxust.entity.SessionDetail;
import com.jxust.service.SessionDetailService;
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
public class SessionDetailController {

    @Autowired
    private SessionDetailService sessionDetailService;


    @GetMapping(value = "/session/detail")
    public Map<String,Object> getAll(){
        Map<String,Object> result = new HashMap<>();
        List<SessionDetail> list = sessionDetailService.getAll();
        result.put("data",list);
        return result;
    }
}

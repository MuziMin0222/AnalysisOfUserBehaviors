package com.jxust.controller;

import com.jxust.entity.SessionDetail;
import com.jxust.entity.SessionRandomExtract;
import com.jxust.service.SessionRandomExtractService;
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
public class SessionRandomExtractController {

    @Autowired
    private SessionRandomExtractService sessionRandomExtractService;


    @GetMapping(value = "/session/random/extract")
    public Map<String,Object> getAll(){
        Map<String,Object> result = new HashMap<>();
        List<SessionRandomExtract> list = sessionRandomExtractService.getAll();
        result.put("data",list);
        return result;
    }
}

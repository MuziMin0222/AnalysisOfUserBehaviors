package com.jxust.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
* Create by Jayden in 2020/3/7
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Top10Session implements Serializable {
    private String taskid;

    private Long categoryid;

    private String sessionid;

    private Long clickcount;

    private static final long serialVersionUID = 1L;
}
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
public class SessionAggrStat implements Serializable {
    private String taskid;

    private Long sessionCount;

    private Double visitLength1s3sRatio;

    private Double visitLength4s6sRatio;

    private Double visitLength7s9sRatio;

    private Double visitLength10s30sRatio;

    private Double visitLength30s60sRatio;

    private Double visitLength1m3mRatio;

    private Double visitLength3m10mRatio;

    private Double visitLength10m30mRatio;

    private Double visitLength30mRatio;

    private Double stepLength13Ratio;

    private Double stepLength46Ratio;

    private Double stepLength79Ratio;

    private Double stepLength1030Ratio;

    private Double stepLength3060Ratio;

    private Double stepLength60Ratio;

    private static final long serialVersionUID = 1L;
}
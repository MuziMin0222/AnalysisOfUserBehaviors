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
public class AreaTop3Product implements Serializable {
    private String taskid;

    private String area;

    private String arealevel;

    private Long productid;

    private String cityinfos;

    private Long clickcount;

    private String productname;

    private String productstatus;

    private static final long serialVersionUID = 1L;
}
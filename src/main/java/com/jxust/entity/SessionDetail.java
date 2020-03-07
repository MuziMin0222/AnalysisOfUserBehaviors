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
public class SessionDetail implements Serializable {
    private String taskid;

    private Long userid;

    private String sessionid;

    private Long pageid;

    private String actiontime;

    private String searchkeyword;

    private Long clickcategoryid;

    private Long clickproductid;

    private String ordercategoryids;

    private String orderproductids;

    private String paycategoryids;

    private String payproductids;

    private static final long serialVersionUID = 1L;
}
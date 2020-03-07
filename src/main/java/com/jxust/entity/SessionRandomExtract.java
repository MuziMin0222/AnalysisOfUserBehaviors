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
public class SessionRandomExtract implements Serializable {
    private String taskid;

    private String sessionid;

    private String starttime;

    private String searchkeywords;

    private String clickcategoryids;

    private static final long serialVersionUID = 1L;
}
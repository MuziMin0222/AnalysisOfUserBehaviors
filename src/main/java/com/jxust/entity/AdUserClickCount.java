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
public class AdUserClickCount implements Serializable {
    private String date;

    private Integer userid;

    private Integer adid;

    private Integer clickcount;

    private static final long serialVersionUID = 1L;
}
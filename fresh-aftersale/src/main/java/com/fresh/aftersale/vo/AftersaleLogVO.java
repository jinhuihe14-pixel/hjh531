package com.fresh.aftersale.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AftersaleLogVO implements Serializable {

    private Long id;

    private Long aftersaleId;

    private Integer operateType;

    private String operateDesc;

    private Long operatorId;

    private String operatorName;

    private String remark;

    private LocalDateTime createTime;

}

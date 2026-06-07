package com.fresh.aftersale.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AftersaleHandleDTO implements Serializable {

    private Long aftersaleId;

    private Integer status;

    private BigDecimal refundAmount;

    private String remark;

}

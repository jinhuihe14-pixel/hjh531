package com.fresh.aftersale.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AftersaleApplyDTO implements Serializable {

    private String orderNo;

    private Integer type;

    private Integer reason;

    private BigDecimal applyAmount;

    private String remark;

    private List<AftersaleItemDTO> items;

}

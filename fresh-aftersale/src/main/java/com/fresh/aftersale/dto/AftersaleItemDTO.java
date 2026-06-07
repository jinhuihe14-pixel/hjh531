package com.fresh.aftersale.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AftersaleItemDTO implements Serializable {

    private Long skuId;

    private String skuName;

    private BigDecimal price;

    private Integer qty;

    private BigDecimal subtotal;

    private Integer refundQty;

}

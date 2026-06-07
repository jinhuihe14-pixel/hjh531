package com.fresh.aftersale.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AftersaleItemVO implements Serializable {

    private Long id;

    private Long aftersaleId;

    private Long skuId;

    private String skuName;

    private BigDecimal price;

    private Integer qty;

    private BigDecimal subtotal;

    private Integer refundQty;

}

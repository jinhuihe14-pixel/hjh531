package com.fresh.aftersale.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AftersaleOrderVO implements Serializable {

    private Long id;

    private String aftersaleNo;

    private String orderNo;

    private Long userId;

    private Integer type;

    private String typeDesc;

    private Integer reason;

    private String reasonDesc;

    private Integer status;

    private String statusDesc;

    private BigDecimal applyAmount;

    private BigDecimal refundAmount;

    private LocalDateTime applyTime;

    private LocalDateTime handleTime;

    private LocalDateTime completeTime;

    private String remark;

}

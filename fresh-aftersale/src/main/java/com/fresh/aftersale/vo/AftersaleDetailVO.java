package com.fresh.aftersale.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AftersaleDetailVO implements Serializable {

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

    private Long handlerId;

    private String remark;

    private Integer approvalLevel;

    private String approvalLevelDesc;

    private List<AftersaleItemVO> items;

    private List<AftersaleLogVO> logs;

}

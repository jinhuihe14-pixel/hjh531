package com.fresh.aftersale.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("aftersale_order")
public class AftersaleOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String aftersaleNo;

    private String orderNo;

    private Long userId;

    private Integer type;

    private Integer reason;

    private Integer status;

    private BigDecimal applyAmount;

    private BigDecimal refundAmount;

    private LocalDateTime applyTime;

    private LocalDateTime handleTime;

    private LocalDateTime completeTime;

    private Long handlerId;

    private String remark;

    private Integer approvalLevel;

    private Integer approvalStatus;

    private Long currentApprover;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

}

package com.fresh.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dispatch_record")
public class DispatchRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deliveryId;

    private String deliveryNo;

    private Long riderId;

    private String riderName;

    private Integer dispatchType;

    private String dispatchReason;

    private BigDecimal distance;

    private Integer estimateTime;

    private LocalDateTime dispatchTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

}

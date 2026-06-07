package com.fresh.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("delivery_order")
public class DeliveryOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deliveryNo;

    private String orderNo;

    private Long orderWarehouseId;

    private Long warehouseId;

    private Long riderId;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer status;

    private BigDecimal deliveryFee;

    private BigDecimal distance;

    private Integer estimateTime;

    private LocalDateTime pickupTime;

    private LocalDateTime deliveryTime;

    private String cancelReason;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

}

package com.fresh.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Integer status;

    private String statusDesc;

    private Long addressId;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String remark;

    private LocalDateTime payTime;

    private LocalDateTime deliveryTime;

    private LocalDateTime completeTime;

    private LocalDateTime createTime;

    private List<OrderItemVO> items;

    private List<OrderWarehouseVO> warehouses;

}

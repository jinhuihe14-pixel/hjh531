package com.fresh.delivery.dto;

import com.fresh.common.page.PageQuery;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryOrderQueryDTO extends PageQuery {

    private String deliveryNo;

    private String orderNo;

    private Long riderId;

    private Long warehouseId;

    private Integer status;

    private String receiverPhone;

}

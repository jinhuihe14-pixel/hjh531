package com.fresh.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateDTO {

    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String remark;

    @NotEmpty(message = "订单商品不能为空")
    private List<OrderItemDTO> items;

}

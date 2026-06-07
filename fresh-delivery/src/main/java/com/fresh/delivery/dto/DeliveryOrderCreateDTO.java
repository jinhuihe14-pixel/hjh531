package com.fresh.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryOrderCreateDTO {

    @NotBlank(message = "主订单号不能为空")
    private String orderNo;

    @NotNull(message = "仓库履约单ID不能为空")
    private Long orderWarehouseId;

    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    private BigDecimal deliveryFee;

    private String remark;

}

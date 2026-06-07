package com.fresh.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RiderAddDTO {

    @NotBlank(message = "骑手姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String avatar;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @NotNull(message = "所属仓库不能为空")
    private Long currentWarehouseId;

    private BigDecimal currentLongitude;

    private BigDecimal currentLatitude;

}

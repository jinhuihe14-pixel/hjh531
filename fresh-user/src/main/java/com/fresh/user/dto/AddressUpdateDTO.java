package com.fresh.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "修改地址请求")
public class AddressUpdateDTO {

    @NotNull(message = "地址ID不能为空")
    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "收货人")
    private String receiver;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "详细地址")
    private String detail;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "是否默认：0-否，1-是")
    private Integer isDefault;

}

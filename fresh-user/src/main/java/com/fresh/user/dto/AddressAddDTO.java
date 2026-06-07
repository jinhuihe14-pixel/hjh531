package com.fresh.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "新增地址请求")
public class AddressAddDTO {

    @NotBlank(message = "收货人不能为空")
    @Schema(description = "收货人")
    private String receiver;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @NotBlank(message = "省份不能为空")
    @Schema(description = "省份")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Schema(description = "城市")
    private String city;

    @NotBlank(message = "区县不能为空")
    @Schema(description = "区县")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Schema(description = "详细地址")
    private String detail;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @NotNull(message = "是否默认不能为空")
    @Schema(description = "是否默认：0-否，1-是")
    private Integer isDefault;

}

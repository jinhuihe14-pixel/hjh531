package com.fresh.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fresh.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse")
@Schema(description = "前置仓")
public class Warehouse extends BaseEntity {

    @Schema(description = "仓库名称")
    private String name;

    @Schema(description = "仓库编码")
    private String code;

    @Schema(description = "负责人")
    private String manager;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "配送半径（米）")
    private Integer deliveryRadius;

    @Schema(description = "营业时间")
    private String businessHours;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;
}

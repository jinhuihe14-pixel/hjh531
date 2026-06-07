package com.fresh.product.entity;

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

    @Schema(description = "地址")
    private String address;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "配送半径（米）")
    private Integer deliveryRadius;
}

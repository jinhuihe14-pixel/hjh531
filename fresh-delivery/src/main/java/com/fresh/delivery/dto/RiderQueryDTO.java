package com.fresh.delivery.dto;

import com.fresh.common.page.PageQuery;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RiderQueryDTO extends PageQuery {

    private String name;

    private String phone;

    private Integer status;

    private Integer workStatus;

    private Long warehouseId;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer radius;

}

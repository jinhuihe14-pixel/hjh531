package com.fresh.order.dto;

import com.fresh.common.page.PageQuery;
import lombok.Data;

@Data
public class OrderQueryDTO extends PageQuery {

    private Integer status;

    private String orderNo;

    private Long userId;

}

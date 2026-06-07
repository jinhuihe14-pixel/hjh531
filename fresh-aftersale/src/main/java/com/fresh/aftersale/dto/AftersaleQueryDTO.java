package com.fresh.aftersale.dto;

import com.fresh.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AftersaleQueryDTO extends PageQuery {

    private Integer type;

    private Integer status;

    private String orderNo;

    private String aftersaleNo;

}

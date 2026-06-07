package com.fresh.settlement.dto;

import com.fresh.common.page.PageQuery;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PickerSettlementQueryDTO extends PageQuery {

    private String settlementNo;

    private Long pickerId;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}

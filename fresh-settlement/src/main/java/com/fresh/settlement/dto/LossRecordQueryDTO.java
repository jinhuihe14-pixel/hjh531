package com.fresh.settlement.dto;

import com.fresh.common.page.PageQuery;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LossRecordQueryDTO extends PageQuery {

    private String lossNo;

    private Integer type;

    private Integer responsibleParty;

    private Long settlementId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}

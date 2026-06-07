package com.fresh.settlement.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LossRecordVO {

    private Long id;

    private String lossNo;

    private Integer type;

    private String typeDesc;

    private Long skuId;

    private String skuName;

    private BigDecimal qty;

    private BigDecimal amount;

    private Integer responsibleParty;

    private String responsiblePartyDesc;

    private Long settlementId;

    private String remark;

    private LocalDateTime createTime;

}

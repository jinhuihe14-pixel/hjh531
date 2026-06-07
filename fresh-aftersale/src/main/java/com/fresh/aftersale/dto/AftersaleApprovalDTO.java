package com.fresh.aftersale.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AftersaleApprovalDTO implements Serializable {

    private Long aftersaleId;

    private Boolean pass;

    private String remark;

}

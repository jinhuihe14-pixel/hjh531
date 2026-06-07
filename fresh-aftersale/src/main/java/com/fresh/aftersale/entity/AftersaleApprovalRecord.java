package com.fresh.aftersale.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("aftersale_approval_record")
public class AftersaleApprovalRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long aftersaleId;

    private Integer approvalLevel;

    private Integer approvalStatus;

    private Long approverId;

    private String approverName;

    private String approvalRemark;

    private LocalDateTime approvalTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

}

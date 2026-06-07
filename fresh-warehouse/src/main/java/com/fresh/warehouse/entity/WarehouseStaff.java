package com.fresh.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fresh.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse_staff")
@Schema(description = "仓内操作人员")
public class WarehouseStaff extends BaseEntity {

    @Schema(description = "所属仓库ID")
    private Long warehouseId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "角色：1-仓管，2-分拣员，3-配送员")
    private Integer role;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}

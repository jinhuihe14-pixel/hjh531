-- 售后客服服务数据库脚本

CREATE DATABASE IF NOT EXISTS fresh_aftersale DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fresh_aftersale;

-- 售后单表
DROP TABLE IF EXISTS aftersale_order;
CREATE TABLE aftersale_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    aftersale_no VARCHAR(64) NOT NULL COMMENT '售后单号',
    order_no VARCHAR(64) NOT NULL COMMENT '主订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type TINYINT NOT NULL COMMENT '售后类型：1-退款 2-换货 3-补发 4-部分退款 5-整单退款 6-拒收退款 7-商品变质退款 8-少货补发 9-少货退款',
    reason TINYINT COMMENT '售后原因：1-质量问题 2-商品变质 3-少发商品 4-发错商品 5-商品损坏 6-拒收 7-其他',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核 1-处理中 2-已完成 3-已驳回 4-已取消 5-待审批 6-退款中',
    apply_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '申请金额',
    refund_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '实际退款金额',
    apply_time DATETIME COMMENT '申请时间',
    handle_time DATETIME COMMENT '处理时间',
    complete_time DATETIME COMMENT '完成时间',
    handler_id BIGINT COMMENT '处理人ID',
    remark VARCHAR(500) COMMENT '备注',
    approval_level TINYINT DEFAULT 1 COMMENT '审批级别：1-客服直接处理 2-主管审批 3-经理审批',
    approval_status TINYINT DEFAULT 0 COMMENT '审批状态：0-待审批 1-审批通过 2-审批驳回',
    current_approver BIGINT COMMENT '当前审批人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    UNIQUE KEY uk_aftersale_no (aftersale_no),
    KEY idx_user_id (user_id),
    KEY idx_order_no (order_no),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='售后单表';

-- 售后明细表
DROP TABLE IF EXISTS aftersale_item;
CREATE TABLE aftersale_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    aftersale_id BIGINT NOT NULL COMMENT '售后单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    sku_name VARCHAR(200) COMMENT 'SKU名称',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    qty INT DEFAULT 0 COMMENT '数量',
    subtotal DECIMAL(10,2) DEFAULT 0.00 COMMENT '小计',
    refund_qty INT DEFAULT 0 COMMENT '退款/退货数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    KEY idx_aftersale_id (aftersale_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='售后明细表';

-- 售后操作日志表
DROP TABLE IF EXISTS aftersale_log;
CREATE TABLE aftersale_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    aftersale_id BIGINT NOT NULL COMMENT '售后单ID',
    operate_type TINYINT COMMENT '操作类型：1-提交申请 2-取消申请 3-审核驳回 4-审核通过提交审批 5-审核通过开始处理 6-售后处理中 7-退款完成 8-主管审批通过 9-经理审批通过 10-审批驳回',
    operate_desc VARCHAR(200) COMMENT '操作描述',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人名称',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    KEY idx_aftersale_id (aftersale_id),
    KEY idx_operator_id (operator_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='售后操作日志表';

-- 审批记录表
DROP TABLE IF EXISTS aftersale_approval_record;
CREATE TABLE aftersale_approval_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    aftersale_id BIGINT NOT NULL COMMENT '售后单ID',
    approval_level TINYINT NOT NULL COMMENT '审批级别：1-客服 2-主管 3-经理',
    approval_status TINYINT NOT NULL COMMENT '审批状态：0-驳回 1-通过',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(50) COMMENT '审批人名称',
    approval_remark VARCHAR(500) COMMENT '审批意见',
    approval_time DATETIME COMMENT '审批时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    KEY idx_aftersale_id (aftersale_id),
    KEY idx_approver_id (approver_id),
    KEY idx_approval_time (approval_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批记录表';

-- 创建数据库
CREATE DATABASE IF NOT EXISTS fresh_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fresh_order;

-- 订单主表
DROP TABLE IF EXISTS order_main;
CREATE TABLE order_main (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
    pay_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态：1-待支付 2-待发货 3-配送中 4-已完成 5-已取消 6-售后中',
    address_id BIGINT DEFAULT NULL COMMENT '收货地址ID',
    receiver_name VARCHAR(32) DEFAULT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(16) DEFAULT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(255) DEFAULT NULL COMMENT '收货地址',
    longitude DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    latitude DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    delivery_time DATETIME DEFAULT NULL COMMENT '发货时间',
    complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 订单明细表
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单号',
    sku_id BIGINT NOT NULL COMMENT '商品SKU ID',
    sku_name VARCHAR(128) DEFAULT NULL COMMENT '商品名称',
    sku_image VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
    quantity INT NOT NULL DEFAULT 0 COMMENT '数量',
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '小计金额',
    warehouse_id BIGINT DEFAULT NULL COMMENT '发货仓库ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_order_no (order_no),
    KEY idx_sku_id (sku_id),
    KEY idx_warehouse_id (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 子订单/仓履约单表
DROP TABLE IF EXISTS order_warehouse;
CREATE TABLE order_warehouse (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    warehouse_name VARCHAR(64) DEFAULT NULL COMMENT '仓库名称',
    status TINYINT NOT NULL DEFAULT 2 COMMENT '状态：2-待发货 3-配送中 4-已完成 5-已取消',
    delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '配送费',
    rider_id BIGINT DEFAULT NULL COMMENT '骑手ID',
    delivery_time DATETIME DEFAULT NULL COMMENT '发货时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_order_no (order_no),
    KEY idx_warehouse_id (warehouse_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='子订单/仓履约单表';

-- 订单操作日志表
DROP TABLE IF EXISTS order_log;
CREATE TABLE order_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单号',
    before_status TINYINT DEFAULT NULL COMMENT '操作前状态',
    after_status TINYINT DEFAULT NULL COMMENT '操作后状态',
    operate_type VARCHAR(32) DEFAULT NULL COMMENT '操作类型',
    operate_remark VARCHAR(255) DEFAULT NULL COMMENT '操作备注',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    operator_name VARCHAR(32) DEFAULT NULL COMMENT '操作人名称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_order_no (order_no),
    KEY idx_operate_type (operate_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';

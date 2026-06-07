CREATE DATABASE IF NOT EXISTS fresh_warehouse DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fresh_warehouse;

DROP TABLE IF EXISTS warehouse;
CREATE TABLE warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    code VARCHAR(50) NOT NULL COMMENT '仓库编码',
    manager VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    province VARCHAR(50) DEFAULT NULL COMMENT '省份',
    city VARCHAR(50) DEFAULT NULL COMMENT '城市',
    district VARCHAR(50) DEFAULT NULL COMMENT '区县',
    detail_address VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
    longitude DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    latitude DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    delivery_radius INT DEFAULT 3000 COMMENT '配送半径（米）',
    business_hours VARCHAR(100) DEFAULT NULL COMMENT '营业时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    UNIQUE KEY uk_code (code),
    INDEX idx_status (status),
    INDEX idx_longitude_latitude (longitude, latitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='前置仓表';

DROP TABLE IF EXISTS stock_transfer;
CREATE TABLE stock_transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    transfer_no VARCHAR(50) NOT NULL COMMENT '调拨单号',
    from_warehouse_id BIGINT NOT NULL COMMENT '调出仓ID',
    to_warehouse_id BIGINT NOT NULL COMMENT '调入仓ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1-待审批，2-已批准，3-已驳回，4-调拨中，5-已完成',
    apply_user_id BIGINT DEFAULT NULL COMMENT '申请人ID',
    approve_user_id BIGINT DEFAULT NULL COMMENT '审批人ID',
    approve_time DATETIME DEFAULT NULL COMMENT '审批时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    UNIQUE KEY uk_transfer_no (transfer_no),
    INDEX idx_from_warehouse_id (from_warehouse_id),
    INDEX idx_to_warehouse_id (to_warehouse_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存调拨单表';

DROP TABLE IF EXISTS stock_transfer_item;
CREATE TABLE stock_transfer_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    transfer_id BIGINT NOT NULL COMMENT '调拨单ID',
    transfer_no VARCHAR(50) NOT NULL COMMENT '调拨单号',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    qty INT DEFAULT 0 COMMENT '调拨数量',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_transfer_id (transfer_id),
    INDEX idx_transfer_no (transfer_no),
    INDEX idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存调拨明细表';

DROP TABLE IF EXISTS warehouse_staff;
CREATE TABLE warehouse_staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    warehouse_id BIGINT NOT NULL COMMENT '所属仓库ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role TINYINT DEFAULT 1 COMMENT '角色：1-仓管，2-分拣员，3-配送员',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓内操作人员表';

INSERT INTO warehouse (name, code, manager, phone, province, city, district, detail_address, longitude, latitude, delivery_radius, business_hours, status, sort) VALUES
('朝阳路店', 'WH001', '张三', '13800138001', '北京市', '北京市', '朝阳区', '朝阳路1号', 116.4551, 39.9243, 3000, '08:00-22:00', 1, 1),
('海淀路店', 'WH002', '李四', '13800138002', '北京市', '北京市', '海淀区', '海淀路2号', 116.3104, 39.9827, 3000, '08:00-22:00', 1, 2),
('西城店', 'WH003', '王五', '13800138003', '北京市', '北京市', '西城区', '西城街3号', 116.3642, 39.9128, 3000, '08:00-22:00', 1, 3);

INSERT INTO warehouse_staff (warehouse_id, user_id, name, phone, role, status, remark) VALUES
(1, 1, '张仓管', '13800138001', 1, 1, '朝阳路店仓管'),
(1, 2, '李分拣', '13800138002', 2, 1, '朝阳路店分拣员'),
(2, 3, '王仓管', '13800138003', 1, 1, '海淀路店仓管');

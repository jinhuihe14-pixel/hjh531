CREATE DATABASE IF NOT EXISTS fresh_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fresh_product;

DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父级分类ID',
    level TINYINT DEFAULT 1 COMMENT '层级：1-一级分类，2-二级分类，3-三级分类',
    sort INT DEFAULT 0 COMMENT '排序',
    icon VARCHAR(255) DEFAULT NULL COMMENT '图标',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    subtitle VARCHAR(500) DEFAULT NULL COMMENT '副标题',
    main_image VARCHAR(255) DEFAULT NULL COMMENT '主图',
    images TEXT DEFAULT NULL COMMENT '轮播图，多个用逗号分隔',
    description TEXT DEFAULT NULL COMMENT '商品描述',
    storage_type TINYINT DEFAULT 1 COMMENT '存储类型：1-常温，2-冷藏，3-冷冻',
    status TINYINT DEFAULT 0 COMMENT '状态：0-下架，1-上架',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品SPU表';

DROP TABLE IF EXISTS product_sku;
CREATE TABLE product_sku (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    product_id BIGINT NOT NULL COMMENT '商品SPU ID',
    sku_name VARCHAR(200) NOT NULL COMMENT 'SKU名称',
    price DECIMAL(10,2) NOT NULL COMMENT '销售价',
    original_price DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
    spec VARCHAR(100) DEFAULT NULL COMMENT '规格',
    weight INT DEFAULT 0 COMMENT '重量（克）',
    bar_code VARCHAR(50) DEFAULT NULL COMMENT '条形码',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_bar_code (bar_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品SKU表';

DROP TABLE IF EXISTS warehouse;
CREATE TABLE warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    code VARCHAR(50) NOT NULL COMMENT '仓库编码',
    address VARCHAR(255) DEFAULT NULL COMMENT '地址',
    longitude DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    latitude DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    delivery_radius INT DEFAULT 3000 COMMENT '配送半径（米）',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    UNIQUE KEY uk_code (code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='前置仓表';

DROP TABLE IF EXISTS warehouse_stock;
CREATE TABLE warehouse_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    available_qty INT DEFAULT 0 COMMENT '可售库存',
    locked_qty INT DEFAULT 0 COMMENT '锁定库存',
    in_transit_qty INT DEFAULT 0 COMMENT '在途库存',
    total_qty INT DEFAULT 0 COMMENT '合计库存',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    UNIQUE KEY uk_warehouse_sku (warehouse_id, sku_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库库存表';

DROP TABLE IF EXISTS stock_log;
CREATE TABLE stock_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    type TINYINT NOT NULL COMMENT '流水类型：1-入库，2-出库，3-锁定，4-释放，5-扣减，6-回补，7-调拨入，8-调拨出',
    qty INT NOT NULL COMMENT '变动数量',
    before_qty INT DEFAULT 0 COMMENT '变动前库存',
    after_qty INT DEFAULT 0 COMMENT '变动后库存',
    order_no VARCHAR(50) DEFAULT NULL COMMENT '关联订单号',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_type (type),
    INDEX idx_order_no (order_no),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存流水表';

INSERT INTO category (name, parent_id, level, sort, icon, status) VALUES
('水果', 0, 1, 1, '🍎', 1),
('蔬菜', 0, 1, 2, '🥬', 1),
('肉类', 0, 1, 3, '🥩', 1),
('海鲜', 0, 1, 4, '🦐', 1),
('乳制品', 0, 1, 5, '🥛', 1),
('烘焙', 0, 1, 6, '🍞', 1),
('饮料', 0, 1, 7, '🧃', 1),
('零食', 0, 1, 8, '🍪', 1);

INSERT INTO category (name, parent_id, level, sort, icon, status) VALUES
('苹果', 1, 2, 1, NULL, 1),
('香蕉', 1, 2, 2, NULL, 1),
('橙子', 1, 2, 3, NULL, 1),
('葡萄', 1, 2, 4, NULL, 1),
('西瓜', 1, 2, 5, NULL, 1),
('草莓', 1, 2, 6, NULL, 1),
('芒果', 1, 2, 7, NULL, 1),
('火龙果', 1, 2, 8, NULL, 1);

INSERT INTO category (name, parent_id, level, sort, icon, status) VALUES
('叶菜类', 2, 2, 1, NULL, 1),
('根茎类', 2, 2, 2, NULL, 1),
('茄果类', 2, 2, 3, NULL, 1),
('菌菇类', 2, 2, 4, NULL, 1),
('豆制品', 2, 2, 5, NULL, 1);

INSERT INTO warehouse (name, code, address, longitude, latitude, status, delivery_radius) VALUES
('朝阳门店', 'WH001', '北京市朝阳区朝阳门南大街1号', 116.435678, 39.923456, 1, 3000),
('中关村店', 'WH002', '北京市海淀区中关村大街1号', 116.312345, 39.987654, 1, 3000),
('国贸店', 'WH003', '北京市朝阳区国贸大厦', 116.467890, 39.912345, 1, 3000),
('西直门店', 'WH004', '北京市西城区西直门南大街', 116.356789, 39.945678, 1, 3000),
('望京店', 'WH005', '北京市朝阳区望京街道', 116.478901, 39.998765, 1, 3000);

INSERT INTO product (category_id, name, subtitle, main_image, images, description, storage_type, status, sort) VALUES
(9, '红富士苹果', '新鲜脆甜，产地直供', NULL, NULL, '精选山东烟台红富士苹果，果型端正，色泽鲜艳，果肉细腻，脆甜多汁。', 1, 1, 10),
(10, '进口香蕉', '香甜软糯，新鲜直达', NULL, NULL, '精选菲律宾进口香蕉，自然熟成，香甜软糯，营养丰富。', 1, 1, 9),
(11, '赣南脐橙', '甜酸可口，皮薄多汁', NULL, NULL, '江西赣南脐橙，果大皮薄，橙香浓郁，甜酸可口，汁水充足。', 1, 1, 8),
(14, '草莓', '新鲜采摘，香甜可口', NULL, NULL, '丹东99草莓，新鲜采摘，果大肉厚，香甜多汁，营养丰富。', 2, 1, 7),
(15, '小台农芒果', '香甜浓郁，肉质细腻', NULL, NULL, '海南小台农芒果，果香浓郁，果肉细腻，香甜可口。', 1, 1, 6);

INSERT INTO product_sku (product_id, sku_name, price, original_price, unit, spec, weight, bar_code, status) VALUES
(1, '红富士苹果 500g', 9.9, 12.9, '份', '约500g/份', 500, '6901234567890', 1),
(1, '红富士苹果 1kg', 18.9, 24.9, '份', '约1kg/份', 1000, '6901234567891', 1),
(1, '红富士苹果 2kg', 35.9, 45.9, '份', '约2kg/份', 2000, '6901234567892', 1),
(2, '进口香蕉 500g', 6.9, 8.9, '份', '约500g/份', 500, '6901234567893', 1),
(2, '进口香蕉 1kg', 12.9, 15.9, '份', '约1kg/份', 1000, '6901234567894', 1),
(3, '赣南脐橙 500g', 8.9, 10.9, '份', '约500g/份', 500, '6901234567895', 1),
(3, '赣南脐橙 1kg', 16.9, 19.9, '份', '约1kg/份', 1000, '6901234567896', 1),
(4, '草莓 300g', 19.9, 25.9, '盒', '约300g/盒', 300, '6901234567897', 1),
(5, '小台农芒果 500g', 12.9, 15.9, '份', '约500g/份', 500, '6901234567898', 1),
(5, '小台农芒果 1kg', 24.9, 29.9, '份', '约1kg/份', 1000, '6901234567899', 1);

INSERT INTO warehouse_stock (warehouse_id, sku_id, available_qty, locked_qty, in_transit_qty, total_qty) VALUES
(1, 1, 100, 0, 50, 150),
(1, 2, 80, 0, 30, 110),
(1, 3, 50, 0, 20, 70),
(1, 4, 120, 0, 40, 160),
(1, 5, 90, 0, 30, 120),
(1, 6, 100, 0, 50, 150),
(1, 7, 70, 0, 20, 90),
(1, 8, 60, 0, 20, 80),
(1, 9, 80, 0, 30, 110),
(1, 10, 50, 0, 20, 70),
(2, 1, 120, 0, 60, 180),
(2, 2, 100, 0, 40, 140),
(2, 3, 70, 0, 30, 100),
(2, 4, 150, 0, 50, 200),
(2, 5, 110, 0, 40, 150),
(2, 6, 120, 0, 60, 180),
(2, 7, 90, 0, 30, 120),
(2, 8, 80, 0, 30, 110),
(2, 9, 100, 0, 40, 140),
(2, 10, 70, 0, 30, 100),
(3, 1, 90, 0, 40, 130),
(3, 2, 70, 0, 20, 90),
(3, 3, 40, 0, 10, 50),
(3, 4, 100, 0, 30, 130),
(3, 5, 80, 0, 20, 100),
(3, 6, 90, 0, 40, 130),
(3, 7, 60, 0, 20, 80),
(3, 8, 50, 0, 10, 60),
(3, 9, 70, 0, 20, 90),
(3, 10, 40, 0, 10, 50);

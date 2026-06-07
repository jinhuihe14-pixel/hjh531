-- 创建数据库
CREATE DATABASE IF NOT EXISTS fresh_settlement DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fresh_settlement;

-- 供应商表
DROP TABLE IF EXISTS supplier;
CREATE TABLE supplier (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(128) NOT NULL COMMENT '供应商名称',
    contact VARCHAR(32) DEFAULT NULL COMMENT '联系人',
    phone VARCHAR(16) DEFAULT NULL COMMENT '联系电话',
    address VARCHAR(255) DEFAULT NULL COMMENT '地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    settlement_cycle TINYINT DEFAULT NULL COMMENT '结算周期：1-日结 2-周结 3-月结',
    bank_account VARCHAR(64) DEFAULT NULL COMMENT '银行账户',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_name (name),
    KEY idx_status (status),
    KEY idx_settlement_cycle (settlement_cycle)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 供应商结算单表
DROP TABLE IF EXISTS supplier_settlement;
CREATE TABLE supplier_settlement (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    settlement_no VARCHAR(32) NOT NULL COMMENT '结算单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    period_start DATETIME NOT NULL COMMENT '结算周期开始时间',
    period_end DATETIME NOT NULL COMMENT '结算周期结束时间',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '应付总金额',
    deduction_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '损耗抵扣金额',
    actual_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待对账 2-已确认 3-已付款',
    settlement_time DATETIME DEFAULT NULL COMMENT '结算时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_settlement_no (settlement_no),
    KEY idx_supplier_id (supplier_id),
    KEY idx_status (status),
    KEY idx_period_start (period_start),
    KEY idx_period_end (period_end),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商结算单表';

-- 供应商结算明细表
DROP TABLE IF EXISTS supplier_settlement_detail;
CREATE TABLE supplier_settlement_detail (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    settlement_id BIGINT NOT NULL COMMENT '结算单ID',
    sku_id BIGINT NOT NULL COMMENT '商品SKU ID',
    sku_name VARCHAR(128) DEFAULT NULL COMMENT '商品名称',
    actual_qty DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实发数量',
    loss_ratio DECIMAL(5,4) NOT NULL DEFAULT 0.0000 COMMENT '损耗比例',
    loss_qty DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '损耗数量',
    unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
    deduction_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '抵扣金额',
    actual_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实际金额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_settlement_id (settlement_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商结算明细表';

-- 分拣员结算单表
DROP TABLE IF EXISTS picker_settlement;
CREATE TABLE picker_settlement (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    settlement_no VARCHAR(32) NOT NULL COMMENT '结算单号',
    picker_id BIGINT NOT NULL COMMENT '分拣员ID',
    picker_name VARCHAR(32) DEFAULT NULL COMMENT '分拣员姓名',
    period_start DATETIME NOT NULL COMMENT '结算周期开始时间',
    period_end DATETIME NOT NULL COMMENT '结算周期结束时间',
    total_pieces INT NOT NULL DEFAULT 0 COMMENT '总件数',
    base_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '基础工资',
    subsidy DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '补贴',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待结算 2-已确认 3-已付款',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_settlement_no (settlement_no),
    KEY idx_picker_id (picker_id),
    KEY idx_status (status),
    KEY idx_period_start (period_start),
    KEY idx_period_end (period_end),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分拣员结算单表';

-- 分拣员每日统计表
DROP TABLE IF EXISTS picker_daily_stat;
CREATE TABLE picker_daily_stat (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    picker_id BIGINT NOT NULL COMMENT '分拣员ID',
    picker_name VARCHAR(32) DEFAULT NULL COMMENT '分拣员姓名',
    stat_date DATE NOT NULL COMMENT '统计日期',
    total_pieces INT NOT NULL DEFAULT 0 COMMENT '总件数',
    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '当日金额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_picker_id (picker_id),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分拣员每日统计表';

-- 骑手结算单表
DROP TABLE IF EXISTS rider_settlement;
CREATE TABLE rider_settlement (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    settlement_no VARCHAR(32) NOT NULL COMMENT '结算单号',
    rider_id BIGINT NOT NULL COMMENT '骑手ID',
    rider_name VARCHAR(32) DEFAULT NULL COMMENT '骑手姓名',
    period_start DATETIME NOT NULL COMMENT '结算周期开始时间',
    period_end DATETIME NOT NULL COMMENT '结算周期结束时间',
    order_count INT NOT NULL DEFAULT 0 COMMENT '订单数',
    total_distance DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总距离(公里)',
    base_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '基础金额',
    distance_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '距离补贴',
    time_subsidy DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '时效补贴',
    penalty_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '罚款金额',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '合计金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待结算 2-已确认 3-已付款',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_settlement_no (settlement_no),
    KEY idx_rider_id (rider_id),
    KEY idx_status (status),
    KEY idx_period_start (period_start),
    KEY idx_period_end (period_end),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='骑手结算单表';

-- 骑手每日统计表
DROP TABLE IF EXISTS rider_daily_stat;
CREATE TABLE rider_daily_stat (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    rider_id BIGINT NOT NULL COMMENT '骑手ID',
    rider_name VARCHAR(32) DEFAULT NULL COMMENT '骑手姓名',
    stat_date DATE NOT NULL COMMENT '统计日期',
    order_count INT NOT NULL DEFAULT 0 COMMENT '订单数',
    total_distance DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总距离(公里)',
    base_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '基础金额',
    distance_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '距离补贴',
    time_subsidy DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '时效补贴',
    penalty_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '罚款金额',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '合计金额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_rider_id (rider_id),
    KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='骑手每日统计表';

-- 损耗记录表
DROP TABLE IF EXISTS loss_record;
CREATE TABLE loss_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    loss_no VARCHAR(32) NOT NULL COMMENT '损耗单号',
    type TINYINT NOT NULL COMMENT '损耗类型：1-临期 2-破损 3-拒收',
    sku_id BIGINT NOT NULL COMMENT '商品SKU ID',
    sku_name VARCHAR(128) DEFAULT NULL COMMENT '商品名称',
    qty DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '损耗数量',
    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '损耗金额',
    responsible_party TINYINT NOT NULL COMMENT '责任方：1-供应商 2-仓库 3-骑手 4-平台',
    settlement_id BIGINT DEFAULT NULL COMMENT '关联结算单ID',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_loss_no (loss_no),
    KEY idx_type (type),
    KEY idx_sku_id (sku_id),
    KEY idx_responsible_party (responsible_party),
    KEY idx_settlement_id (settlement_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='损耗记录表';

-- 结算规则表
DROP TABLE IF EXISTS settlement_rule;
CREATE TABLE settlement_rule (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    rule_type TINYINT NOT NULL COMMENT '规则类型：1-配送补贴标准 2-分拣计件单价 3-损耗分摊规则',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    rule_content TEXT COMMENT '规则内容(JSON格式)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    effective_time DATETIME NOT NULL COMMENT '生效时间',
    expire_time DATETIME DEFAULT NULL COMMENT '失效时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_rule_type (rule_type),
    KEY idx_version (version),
    KEY idx_status (status),
    KEY idx_effective_time (effective_time),
    KEY idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结算规则表';

-- 初始化结算规则数据
INSERT INTO settlement_rule (rule_type, rule_name, rule_content, version, status, effective_time) VALUES
(1, '默认配送补贴标准', '{"basePrice": 5.00, "distanceStep": 1.00, "timeSubsidy": 2.00}', 1, 1, '2024-01-01 00:00:00'),
(2, '默认分拣计件单价', '{"pieceRate": 0.50, "dailyBonus": 50.00, "bonusThreshold": 200}', 1, 1, '2024-01-01 00:00:00'),
(3, '默认损耗分摊规则', '{"supplierRatio": 0.70, "warehouseRatio": 0.20, "platformRatio": 0.10}', 1, 1, '2024-01-01 00:00:00');

-- 创建数据库
CREATE DATABASE IF NOT EXISTS fresh_auth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fresh_auth;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    role VARCHAR(50) DEFAULT 'USER' COMMENT '角色',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 插入测试数据（密码：123456，已BCrypt加密）
INSERT INTO sys_user (username, password, nickname, phone, email, status, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', '13800138000', 'admin@fresh.com', 1, 'ADMIN'),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '普通用户', '13900139000', 'user@fresh.com', 1, 'USER');

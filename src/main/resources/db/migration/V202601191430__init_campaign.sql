-- V202601191430: 初始化活动表
-- 创建时间: 2026-01-19 14:30

CREATE TABLE IF NOT EXISTS t_campaign (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '活动ID',
    name VARCHAR(100) NOT NULL COMMENT '活动名称',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT, RUNNING, PAUSED, COMPLETED',
    audience_id BIGINT COMMENT '受众ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_name (name),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动表';

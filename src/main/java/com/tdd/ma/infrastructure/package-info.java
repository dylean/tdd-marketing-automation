/**
 * 基础设施层 (Infrastructure Layer)
 * 
 * 职责：
 * - 仓储实现（Repository Implementation）
 * - 数据库访问（MyBatis Mapper）
 * - 外部服务集成
 * - 消息队列
 * - 缓存实现
 * 
 * 依赖规则：
 * - 实现 domain 层定义的接口
 * - 可以依赖 domain 层
 * - 不能被 domain 层和 application 层依赖
 */
package com.tdd.ma.infrastructure;

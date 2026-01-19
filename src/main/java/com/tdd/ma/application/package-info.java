/**
 * 应用层 (Application Layer)
 * 
 * 职责：
 * - 编排领域服务和领域对象
 * - 事务管理
 * - 应用服务（Application Service）
 * - 命令处理（Command）
 * - 查询处理（Query）
 * 
 * 依赖规则：
 * - 可以依赖 domain 层
 * - 不能依赖 interfaces 层
 * - 不能依赖 infrastructure 层的具体实现
 */
package com.tdd.ma.application;

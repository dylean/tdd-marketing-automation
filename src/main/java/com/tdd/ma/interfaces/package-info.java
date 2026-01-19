/**
 * 接口层 (Interfaces Layer)
 * 
 * 职责：
 * - 处理用户请求（Controller）
 * - 数据传输对象（DTO）
 * - 请求/响应转换
 * - 参数校验
 * 
 * 依赖规则：
 * - 可以依赖 application 层
 * - 不能依赖 infrastructure 层
 * - 不能直接依赖 domain 层的内部实现
 */
package com.tdd.ma.interfaces;

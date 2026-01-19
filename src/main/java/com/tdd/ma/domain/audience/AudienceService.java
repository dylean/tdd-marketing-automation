package com.tdd.ma.domain.audience;

import java.util.Optional;

/**
 * 受众服务接口（防腐层，用于调用外部系统）
 */
public interface AudienceService {

    /**
     * 根据ID获取受众信息
     */
    Optional<Audience> getAudienceById(Long audienceId);

    /**
     * 检查受众是否存在
     */
    boolean existsById(Long audienceId);
}

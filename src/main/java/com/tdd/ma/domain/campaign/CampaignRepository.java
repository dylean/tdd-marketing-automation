package com.tdd.ma.domain.campaign;

import java.util.Optional;

/**
 * 活动仓储接口（领域层定义，基础设施层实现）
 */
public interface CampaignRepository {

    /**
     * 保存活动
     */
    Campaign save(Campaign campaign);

    /**
     * 根据ID查询活动
     */
    Optional<Campaign> findById(Long id);

    /**
     * 检查活动名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 删除活动
     */
    void deleteById(Long id);
}

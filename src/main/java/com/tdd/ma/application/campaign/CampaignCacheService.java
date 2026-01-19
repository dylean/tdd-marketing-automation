package com.tdd.ma.application.campaign;

import com.tdd.ma.application.campaign.dto.CampaignDTO;

/**
 * 活动缓存服务接口
 */
public interface CampaignCacheService {

    /**
     * 获取缓存
     */
    CampaignDTO get(Long campaignId);

    /**
     * 放入缓存
     */
    void put(Long campaignId, CampaignDTO campaign);

    /**
     * 清除缓存
     */
    void evict(Long campaignId);
}

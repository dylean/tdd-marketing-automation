package com.tdd.ma.infrastructure.cache;

import com.tdd.ma.application.campaign.CampaignCacheService;
import com.tdd.ma.application.campaign.dto.CampaignDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 活动缓存服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCampaignCacheService implements CampaignCacheService {

    private static final String CACHE_KEY_PREFIX = "campaign:";
    private static final long CACHE_TTL_MINUTES = 30;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public CampaignDTO get(Long campaignId) {
        String key = buildKey(campaignId);
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof CampaignDTO dto) {
                log.debug("Cache hit for campaign: {}", campaignId);
                return dto;
            }
        } catch (Exception e) {
            log.warn("Failed to get campaign from cache: {}", campaignId, e);
        }
        return null;
    }

    @Override
    public void put(Long campaignId, CampaignDTO campaign) {
        String key = buildKey(campaignId);
        try {
            redisTemplate.opsForValue().set(key, campaign, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Cached campaign: {}", campaignId);
        } catch (Exception e) {
            log.warn("Failed to cache campaign: {}", campaignId, e);
        }
    }

    @Override
    public void evict(Long campaignId) {
        String key = buildKey(campaignId);
        try {
            redisTemplate.delete(key);
            log.debug("Evicted campaign from cache: {}", campaignId);
        } catch (Exception e) {
            log.warn("Failed to evict campaign from cache: {}", campaignId, e);
        }
    }

    private String buildKey(Long campaignId) {
        return CACHE_KEY_PREFIX + campaignId;
    }
}

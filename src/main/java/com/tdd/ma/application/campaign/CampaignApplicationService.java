package com.tdd.ma.application.campaign;

import com.tdd.ma.application.campaign.dto.CampaignDTO;
import com.tdd.ma.application.campaign.dto.CreateCampaignCommand;
import com.tdd.ma.domain.audience.AudienceService;
import com.tdd.ma.domain.campaign.Campaign;
import com.tdd.ma.domain.campaign.CampaignRepository;
import com.tdd.ma.domain.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 活动应用服务
 */
@Service
@RequiredArgsConstructor
public class CampaignApplicationService {

    private final CampaignRepository campaignRepository;
    private final AudienceService audienceService;
    private final CampaignCacheService campaignCacheService;

    /**
     * 创建活动
     */
    @Transactional
    public CampaignDTO createCampaign(CreateCampaignCommand command) {
        // 校验活动名称唯一性
        if (campaignRepository.existsByName(command.name())) {
            throw new BusinessException("活动名称已存在");
        }

        // 校验受众是否存在
        if (command.audienceId() != null && !audienceService.existsById(command.audienceId())) {
            throw new BusinessException("受众不存在");
        }

        // 创建活动
        Campaign campaign = new Campaign(
                command.name(),
                command.startTime(),
                command.endTime(),
                command.audienceId()
        );

        // 保存活动
        Campaign saved = campaignRepository.save(campaign);

        return CampaignDTO.fromDomain(saved);
    }

    /**
     * 查询活动
     */
    @Transactional(readOnly = true)
    public CampaignDTO getCampaign(Long id) {
        // 先从缓存获取
        CampaignDTO cached = campaignCacheService.get(id);
        if (cached != null) {
            return cached;
        }

        // 从数据库查询
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        CampaignDTO dto = CampaignDTO.fromDomain(campaign);

        // 放入缓存
        campaignCacheService.put(id, dto);

        return dto;
    }

    /**
     * 启动活动
     */
    @Transactional
    public CampaignDTO startCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        campaign.start();
        Campaign saved = campaignRepository.save(campaign);

        // 清除缓存
        campaignCacheService.evict(id);

        return CampaignDTO.fromDomain(saved);
    }

    /**
     * 暂停活动
     */
    @Transactional
    public CampaignDTO pauseCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        campaign.pause();
        Campaign saved = campaignRepository.save(campaign);

        // 清除缓存
        campaignCacheService.evict(id);

        return CampaignDTO.fromDomain(saved);
    }

    /**
     * 完成活动
     */
    @Transactional
    public CampaignDTO completeCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException("活动不存在"));

        campaign.complete();
        Campaign saved = campaignRepository.save(campaign);

        // 清除缓存
        campaignCacheService.evict(id);

        return CampaignDTO.fromDomain(saved);
    }
}

package com.tdd.ma.infrastructure.persistence;

import com.tdd.ma.domain.campaign.Campaign;
import com.tdd.ma.domain.campaign.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 活动仓储实现
 */
@Repository
@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepository {

    private final CampaignMapper campaignMapper;

    @Override
    public Campaign save(Campaign campaign) {
        CampaignPO po = CampaignPO.fromDomain(campaign);
        if (po.getId() == null) {
            campaignMapper.insert(po);
        } else {
            campaignMapper.updateById(po);
        }
        campaign.setId(po.getId());
        return campaign;
    }

    @Override
    public Optional<Campaign> findById(Long id) {
        CampaignPO po = campaignMapper.selectById(id);
        return Optional.ofNullable(po).map(CampaignPO::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return campaignMapper.existsByName(name);
    }

    @Override
    public void deleteById(Long id) {
        campaignMapper.deleteById(id);
    }
}

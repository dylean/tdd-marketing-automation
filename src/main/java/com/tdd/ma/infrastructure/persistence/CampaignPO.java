package com.tdd.ma.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tdd.ma.domain.campaign.Campaign;
import com.tdd.ma.domain.campaign.CampaignStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动持久化对象
 */
@Data
@TableName("t_campaign")
public class CampaignPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private Long audienceId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static CampaignPO fromDomain(Campaign campaign) {
        CampaignPO po = new CampaignPO();
        po.setId(campaign.getId());
        po.setName(campaign.getName());
        po.setStartTime(campaign.getStartTime());
        po.setEndTime(campaign.getEndTime());
        po.setStatus(campaign.getStatus().name());
        po.setAudienceId(campaign.getAudienceId());
        po.setCreatedAt(campaign.getCreatedAt());
        po.setUpdatedAt(campaign.getUpdatedAt());
        return po;
    }

    public Campaign toDomain() {
        Campaign campaign = new Campaign(
                this.name,
                this.startTime,
                this.endTime,
                this.audienceId
        );
        campaign.setId(this.id);
        campaign.setStatus(CampaignStatus.valueOf(this.status));
        campaign.setCreatedAt(this.createdAt);
        campaign.setUpdatedAt(this.updatedAt);
        return campaign;
    }
}

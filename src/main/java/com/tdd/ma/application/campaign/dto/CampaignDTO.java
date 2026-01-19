package com.tdd.ma.application.campaign.dto;

import com.tdd.ma.domain.campaign.Campaign;
import com.tdd.ma.domain.campaign.CampaignStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 活动DTO
 */
public record CampaignDTO(
        Long id,
        String name,
        LocalDateTime startTime,
        LocalDateTime endTime,
        CampaignStatus status,
        Long audienceId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {

    public static CampaignDTO fromDomain(Campaign campaign) {
        return new CampaignDTO(
                campaign.getId(),
                campaign.getName(),
                campaign.getStartTime(),
                campaign.getEndTime(),
                campaign.getStatus(),
                campaign.getAudienceId(),
                campaign.getCreatedAt(),
                campaign.getUpdatedAt()
        );
    }
}

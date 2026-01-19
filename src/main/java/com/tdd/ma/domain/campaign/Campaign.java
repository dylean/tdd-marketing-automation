package com.tdd.ma.domain.campaign;

import com.tdd.ma.domain.common.BusinessException;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 营销活动聚合根
 */
@Getter
public class Campaign {

    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CampaignStatus status;
    private Long audienceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Campaign() {
    }

    public Campaign(String name, LocalDateTime startTime, LocalDateTime endTime, Long audienceId) {
        validateName(name);
        validateTime(startTime, endTime);
        
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.audienceId = audienceId;
        this.status = CampaignStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("活动名称不能为空");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("活动名称不能超过100个字符");
        }
    }

    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("活动开始时间不能为空");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("活动开始时间必须晚于当前时间");
        }
        if (endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("活动结束时间必须晚于开始时间");
        }
    }

    /**
     * 启动活动
     */
    public void start() {
        if (this.status != CampaignStatus.DRAFT) {
            throw new BusinessException("只有草稿状态的活动才能启动");
        }
        this.status = CampaignStatus.RUNNING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 暂停活动
     */
    public void pause() {
        if (this.status != CampaignStatus.RUNNING) {
            throw new BusinessException("只有运行中的活动才能暂停");
        }
        this.status = CampaignStatus.PAUSED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 恢复活动
     */
    public void resume() {
        if (this.status != CampaignStatus.PAUSED) {
            throw new BusinessException("只有暂停的活动才能恢复");
        }
        this.status = CampaignStatus.RUNNING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 完成活动
     */
    public void complete() {
        if (this.status != CampaignStatus.RUNNING && this.status != CampaignStatus.PAUSED) {
            throw new BusinessException("只有运行中或暂停的活动才能完成");
        }
        this.status = CampaignStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    // 用于持久化恢复
    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

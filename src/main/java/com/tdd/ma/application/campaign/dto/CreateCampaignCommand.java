package com.tdd.ma.application.campaign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 创建活动命令
 */
public record CreateCampaignCommand(
        @NotBlank(message = "活动名称不能为空")
        @Size(max = 100, message = "活动名称不能超过100个字符")
        String name,

        @NotNull(message = "开始时间不能为空")
        LocalDateTime startTime,

        LocalDateTime endTime,

        Long audienceId
) {
}

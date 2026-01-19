package com.tdd.ma.domain.campaign;

import com.tdd.ma.domain.common.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Campaign 领域对象测试")
class CampaignTest {

    @Nested
    @DisplayName("创建活动")
    class CreateCampaign {

        @Test
        @DisplayName("成功创建活动，状态为 DRAFT")
        void should_create_campaign_with_draft_status() {
            // Given
            String name = "双十一大促";
            LocalDateTime startTime = LocalDateTime.now().plusDays(7);

            // When
            Campaign campaign = new Campaign(name, startTime, null, null);

            // Then
            assertThat(campaign.getName()).isEqualTo(name);
            assertThat(campaign.getStartTime()).isEqualTo(startTime);
            assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.DRAFT);
            assertThat(campaign.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("活动名称为空时抛出异常")
        void should_throw_when_name_is_blank() {
            assertThatThrownBy(() -> new Campaign("", LocalDateTime.now().plusDays(1), null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("活动名称不能为空");
        }

        @Test
        @DisplayName("活动名称超过100字符时抛出异常")
        void should_throw_when_name_exceeds_100_chars() {
            String longName = "a".repeat(101);
            assertThatThrownBy(() -> new Campaign(longName, LocalDateTime.now().plusDays(1), null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("活动名称不能超过100个字符");
        }

        @Test
        @DisplayName("开始时间早于当前时间时抛出异常")
        void should_throw_when_start_time_is_past() {
            assertThatThrownBy(() -> new Campaign("活动", LocalDateTime.now().minusDays(1), null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("活动开始时间必须晚于当前时间");
        }

        @Test
        @DisplayName("结束时间早于开始时间时抛出异常")
        void should_throw_when_end_time_before_start_time() {
            LocalDateTime startTime = LocalDateTime.now().plusDays(7);
            LocalDateTime endTime = LocalDateTime.now().plusDays(1);

            assertThatThrownBy(() -> new Campaign("活动", startTime, endTime, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("活动结束时间必须晚于开始时间");
        }
    }

    @Nested
    @DisplayName("启动活动")
    class StartCampaign {

        @Test
        @DisplayName("草稿状态的活动可以启动")
        void should_start_draft_campaign() {
            // Given
            Campaign campaign = createDraftCampaign();

            // When
            campaign.start();

            // Then
            assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.RUNNING);
        }

        @Test
        @DisplayName("非草稿状态的活动不能启动")
        void should_throw_when_start_non_draft_campaign() {
            // Given
            Campaign campaign = createDraftCampaign();
            campaign.start(); // 变为 RUNNING

            // When & Then
            assertThatThrownBy(campaign::start)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("只有草稿状态的活动才能启动");
        }
    }

    @Nested
    @DisplayName("暂停活动")
    class PauseCampaign {

        @Test
        @DisplayName("运行中的活动可以暂停")
        void should_pause_running_campaign() {
            // Given
            Campaign campaign = createRunningCampaign();

            // When
            campaign.pause();

            // Then
            assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.PAUSED);
        }

        @Test
        @DisplayName("非运行中的活动不能暂停")
        void should_throw_when_pause_non_running_campaign() {
            // Given
            Campaign campaign = createDraftCampaign();

            // When & Then
            assertThatThrownBy(campaign::pause)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("只有运行中的活动才能暂停");
        }
    }

    @Nested
    @DisplayName("恢复活动")
    class ResumeCampaign {

        @Test
        @DisplayName("暂停的活动可以恢复")
        void should_resume_paused_campaign() {
            // Given
            Campaign campaign = createPausedCampaign();

            // When
            campaign.resume();

            // Then
            assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.RUNNING);
        }

        @Test
        @DisplayName("非暂停的活动不能恢复")
        void should_throw_when_resume_non_paused_campaign() {
            // Given
            Campaign campaign = createDraftCampaign();

            // When & Then
            assertThatThrownBy(campaign::resume)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("只有暂停的活动才能恢复");
        }
    }

    @Nested
    @DisplayName("完成活动")
    class CompleteCampaign {

        @Test
        @DisplayName("运行中的活动可以完成")
        void should_complete_running_campaign() {
            // Given
            Campaign campaign = createRunningCampaign();

            // When
            campaign.complete();

            // Then
            assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.COMPLETED);
        }

        @Test
        @DisplayName("暂停的活动可以完成")
        void should_complete_paused_campaign() {
            // Given
            Campaign campaign = createPausedCampaign();

            // When
            campaign.complete();

            // Then
            assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.COMPLETED);
        }

        @Test
        @DisplayName("草稿状态的活动不能完成")
        void should_throw_when_complete_draft_campaign() {
            // Given
            Campaign campaign = createDraftCampaign();

            // When & Then
            assertThatThrownBy(campaign::complete)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("只有运行中或暂停的活动才能完成");
        }
    }

    private Campaign createDraftCampaign() {
        return new Campaign("测试活动", LocalDateTime.now().plusDays(7), null, null);
    }

    private Campaign createRunningCampaign() {
        Campaign campaign = createDraftCampaign();
        campaign.start();
        return campaign;
    }

    private Campaign createPausedCampaign() {
        Campaign campaign = createRunningCampaign();
        campaign.pause();
        return campaign;
    }
}

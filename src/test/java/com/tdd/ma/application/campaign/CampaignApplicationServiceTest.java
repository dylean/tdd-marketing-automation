package com.tdd.ma.application.campaign;

import com.tdd.ma.application.campaign.dto.CampaignDTO;
import com.tdd.ma.application.campaign.dto.CreateCampaignCommand;
import com.tdd.ma.domain.audience.AudienceService;
import com.tdd.ma.domain.campaign.Campaign;
import com.tdd.ma.domain.campaign.CampaignRepository;
import com.tdd.ma.domain.campaign.CampaignStatus;
import com.tdd.ma.domain.common.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CampaignApplicationService 单元测试")
class CampaignApplicationServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private AudienceService audienceService;

    @Mock
    private CampaignCacheService campaignCacheService;

    private CampaignApplicationService service;

    @BeforeEach
    void setUp() {
        service = new CampaignApplicationService(campaignRepository, audienceService, campaignCacheService);
    }

    @Nested
    @DisplayName("创建活动")
    class CreateCampaign {

        @Test
        @DisplayName("成功创建活动")
        void should_create_campaign_successfully() {
            // Given
            var command = new CreateCampaignCommand(
                    "双十一大促",
                    LocalDateTime.now().plusDays(7),
                    null,
                    null
            );
            when(campaignRepository.existsByName(command.name())).thenReturn(false);
            when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> {
                Campaign campaign = invocation.getArgument(0);
                campaign.setId(1L);
                return campaign;
            });

            // When
            CampaignDTO result = service.createCampaign(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("双十一大促");
            assertThat(result.status()).isEqualTo(CampaignStatus.DRAFT);
            verify(campaignRepository).save(any(Campaign.class));
        }

        @Test
        @DisplayName("活动名称已存在时抛出异常")
        void should_throw_when_name_exists() {
            // Given
            var command = new CreateCampaignCommand(
                    "双十一大促",
                    LocalDateTime.now().plusDays(7),
                    null,
                    null
            );
            when(campaignRepository.existsByName(command.name())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> service.createCampaign(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("活动名称已存在");
            verify(campaignRepository, never()).save(any());
        }

        @Test
        @DisplayName("受众不存在时抛出异常")
        void should_throw_when_audience_not_exists() {
            // Given
            var command = new CreateCampaignCommand(
                    "双十一大促",
                    LocalDateTime.now().plusDays(7),
                    null,
                    100L
            );
            when(campaignRepository.existsByName(command.name())).thenReturn(false);
            when(audienceService.existsById(100L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> service.createCampaign(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("受众不存在");
        }
    }

    @Nested
    @DisplayName("查询活动")
    class GetCampaign {

        @Test
        @DisplayName("缓存命中时返回缓存数据")
        void should_return_cached_campaign() {
            // Given
            CampaignDTO cached = new CampaignDTO(
                    1L, "活动", LocalDateTime.now(), null,
                    CampaignStatus.DRAFT, null, LocalDateTime.now(), LocalDateTime.now()
            );
            when(campaignCacheService.get(1L)).thenReturn(cached);

            // When
            CampaignDTO result = service.getCampaign(1L);

            // Then
            assertThat(result).isEqualTo(cached);
            verify(campaignRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("缓存未命中时从数据库查询并缓存")
        void should_query_from_db_when_cache_miss() {
            // Given
            when(campaignCacheService.get(1L)).thenReturn(null);
            Campaign campaign = createCampaign(1L);
            when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

            // When
            CampaignDTO result = service.getCampaign(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            verify(campaignCacheService).put(eq(1L), any(CampaignDTO.class));
        }

        @Test
        @DisplayName("活动不存在时抛出异常")
        void should_throw_when_campaign_not_found() {
            // Given
            when(campaignCacheService.get(1L)).thenReturn(null);
            when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> service.getCampaign(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("活动不存在");
        }
    }

    @Nested
    @DisplayName("启动活动")
    class StartCampaign {

        @Test
        @DisplayName("成功启动活动")
        void should_start_campaign_successfully() {
            // Given
            Campaign campaign = createCampaign(1L);
            when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
            when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

            // When
            CampaignDTO result = service.startCampaign(1L);

            // Then
            assertThat(result.status()).isEqualTo(CampaignStatus.RUNNING);
            verify(campaignCacheService).evict(1L);
        }

        @Test
        @DisplayName("活动不存在时抛出异常")
        void should_throw_when_campaign_not_found() {
            // Given
            when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> service.startCampaign(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("活动不存在");
        }
    }

    private Campaign createCampaign(Long id) {
        Campaign campaign = new Campaign("测试活动", LocalDateTime.now().plusDays(7), null, null);
        campaign.setId(id);
        return campaign;
    }
}

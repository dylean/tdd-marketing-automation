package com.tdd.ma.integration;

import com.tdd.ma.application.campaign.dto.CampaignDTO;
import com.tdd.ma.application.campaign.dto.CreateCampaignCommand;
import com.tdd.ma.domain.campaign.CampaignStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 活动集成测试
 * 
 * 连接线上测试数据库运行
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("活动集成测试")
class CampaignIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("创建活动 - 完整流程")
    void should_create_campaign_successfully() {
        // Given
        var command = new CreateCampaignCommand(
                "集成测试活动-" + System.currentTimeMillis(),
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(14),
                null
        );

        // When
        ResponseEntity<CampaignDTO> response = restTemplate.postForEntity(
                "/api/campaigns",
                command,
                CampaignDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo(command.name());
        assertThat(response.getBody().status()).isEqualTo(CampaignStatus.DRAFT);
    }

    @Test
    @DisplayName("创建并启动活动")
    void should_create_and_start_campaign() {
        // Given - 创建活动
        var command = new CreateCampaignCommand(
                "启动测试活动-" + System.currentTimeMillis(),
                LocalDateTime.now().plusDays(7),
                null,
                null
        );

        ResponseEntity<CampaignDTO> createResponse = restTemplate.postForEntity(
                "/api/campaigns",
                command,
                CampaignDTO.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long campaignId = createResponse.getBody().id();

        // When - 启动活动
        ResponseEntity<CampaignDTO> startResponse = restTemplate.postForEntity(
                "/api/campaigns/" + campaignId + "/start",
                null,
                CampaignDTO.class
        );

        // Then
        assertThat(startResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(startResponse.getBody().status()).isEqualTo(CampaignStatus.RUNNING);
    }

    @Test
    @DisplayName("查询活动")
    void should_get_campaign_by_id() {
        // Given - 先创建活动
        var command = new CreateCampaignCommand(
                "查询测试活动-" + System.currentTimeMillis(),
                LocalDateTime.now().plusDays(7),
                null,
                null
        );

        ResponseEntity<CampaignDTO> createResponse = restTemplate.postForEntity(
                "/api/campaigns",
                command,
                CampaignDTO.class
        );

        Long campaignId = createResponse.getBody().id();

        // When - 查询活动
        ResponseEntity<CampaignDTO> getResponse = restTemplate.getForEntity(
                "/api/campaigns/" + campaignId,
                CampaignDTO.class
        );

        // Then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().id()).isEqualTo(campaignId);
        assertThat(getResponse.getBody().name()).isEqualTo(command.name());
    }

    @Test
    @DisplayName("活动完整生命周期：创建 -> 启动 -> 暂停 -> 恢复 -> 完成")
    void should_complete_campaign_lifecycle() {
        // 1. 创建活动
        var command = new CreateCampaignCommand(
                "生命周期测试-" + System.currentTimeMillis(),
                LocalDateTime.now().plusDays(7),
                null,
                null
        );

        ResponseEntity<CampaignDTO> createResponse = restTemplate.postForEntity(
                "/api/campaigns", command, CampaignDTO.class);
        assertThat(createResponse.getBody().status()).isEqualTo(CampaignStatus.DRAFT);

        Long id = createResponse.getBody().id();

        // 2. 启动活动
        ResponseEntity<CampaignDTO> startResponse = restTemplate.postForEntity(
                "/api/campaigns/" + id + "/start", null, CampaignDTO.class);
        assertThat(startResponse.getBody().status()).isEqualTo(CampaignStatus.RUNNING);

        // 3. 暂停活动
        ResponseEntity<CampaignDTO> pauseResponse = restTemplate.postForEntity(
                "/api/campaigns/" + id + "/pause", null, CampaignDTO.class);
        assertThat(pauseResponse.getBody().status()).isEqualTo(CampaignStatus.PAUSED);

        // 4. 完成活动
        ResponseEntity<CampaignDTO> completeResponse = restTemplate.postForEntity(
                "/api/campaigns/" + id + "/complete", null, CampaignDTO.class);
        assertThat(completeResponse.getBody().status()).isEqualTo(CampaignStatus.COMPLETED);
    }
}

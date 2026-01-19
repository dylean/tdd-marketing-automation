package com.tdd.ma.interfaces.rest;

import com.tdd.ma.application.campaign.CampaignApplicationService;
import com.tdd.ma.application.campaign.dto.CampaignDTO;
import com.tdd.ma.application.campaign.dto.CreateCampaignCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 活动 REST 控制器
 */
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignApplicationService campaignApplicationService;

    /**
     * 创建活动
     */
    @PostMapping
    public ResponseEntity<CampaignDTO> createCampaign(@Valid @RequestBody CreateCampaignCommand command) {
        CampaignDTO campaign = campaignApplicationService.createCampaign(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(campaign);
    }

    /**
     * 查询活动
     */
    @GetMapping("/{id}")
    public ResponseEntity<CampaignDTO> getCampaign(@PathVariable Long id) {
        CampaignDTO campaign = campaignApplicationService.getCampaign(id);
        return ResponseEntity.ok(campaign);
    }

    /**
     * 启动活动
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<CampaignDTO> startCampaign(@PathVariable Long id) {
        CampaignDTO campaign = campaignApplicationService.startCampaign(id);
        return ResponseEntity.ok(campaign);
    }

    /**
     * 暂停活动
     */
    @PostMapping("/{id}/pause")
    public ResponseEntity<CampaignDTO> pauseCampaign(@PathVariable Long id) {
        CampaignDTO campaign = campaignApplicationService.pauseCampaign(id);
        return ResponseEntity.ok(campaign);
    }

    /**
     * 完成活动
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<CampaignDTO> completeCampaign(@PathVariable Long id) {
        CampaignDTO campaign = campaignApplicationService.completeCampaign(id);
        return ResponseEntity.ok(campaign);
    }
}

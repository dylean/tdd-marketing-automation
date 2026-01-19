package com.tdd.ma.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 受众服务 Feign 客户端
 */
@FeignClient(name = "audience-service", url = "${audience.service.url:http://localhost:8081}")
public interface AudienceFeignClient {

    @GetMapping("/api/audiences/{id}")
    AudienceResponse getAudience(@PathVariable("id") Long id);

    @GetMapping("/api/audiences/{id}/exists")
    boolean existsById(@PathVariable("id") Long id);
}

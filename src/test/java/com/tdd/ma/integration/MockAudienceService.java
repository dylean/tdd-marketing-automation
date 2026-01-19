package com.tdd.ma.integration;

import com.tdd.ma.domain.audience.Audience;
import com.tdd.ma.domain.audience.AudienceService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 测试用的 Mock AudienceService
 */
@Service
@Primary
@Profile("test")
public class MockAudienceService implements AudienceService {

    @Override
    public Optional<Audience> getAudienceById(Long audienceId) {
        // 模拟：ID < 1000 的受众存在
        if (audienceId != null && audienceId < 1000) {
            return Optional.of(new Audience(
                    audienceId,
                    "测试受众-" + audienceId,
                    10000L,
                    LocalDateTime.now()
            ));
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long audienceId) {
        return audienceId != null && audienceId < 1000;
    }
}

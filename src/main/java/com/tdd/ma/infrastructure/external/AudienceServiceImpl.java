package com.tdd.ma.infrastructure.external;

import com.tdd.ma.domain.audience.Audience;
import com.tdd.ma.domain.audience.AudienceService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 受众服务实现（调用外部服务）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AudienceServiceImpl implements AudienceService {

    private final AudienceFeignClient audienceFeignClient;

    @Override
    public Optional<Audience> getAudienceById(Long audienceId) {
        try {
            AudienceResponse response = audienceFeignClient.getAudience(audienceId);
            if (response != null) {
                return Optional.of(new Audience(
                        response.id(),
                        response.name(),
                        response.userCount(),
                        response.updatedAt()
                ));
            }
        } catch (FeignException.NotFound e) {
            log.debug("Audience not found: {}", audienceId);
        } catch (Exception e) {
            log.error("Failed to get audience: {}", audienceId, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long audienceId) {
        try {
            return audienceFeignClient.existsById(audienceId);
        } catch (FeignException.NotFound e) {
            return false;
        } catch (Exception e) {
            log.error("Failed to check audience existence: {}", audienceId, e);
            return false;
        }
    }
}

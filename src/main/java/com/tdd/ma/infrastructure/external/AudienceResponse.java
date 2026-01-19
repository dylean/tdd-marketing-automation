package com.tdd.ma.infrastructure.external;

import java.time.LocalDateTime;

/**
 * 受众服务响应
 */
public record AudienceResponse(
        Long id,
        String name,
        Long userCount,
        LocalDateTime updatedAt
) {
}

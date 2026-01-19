package com.tdd.ma.domain.audience;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 受众人群（从外部系统获取）
 */
@Getter
public class Audience {

    private Long id;
    private String name;
    private Long userCount;
    private LocalDateTime updatedAt;

    public Audience(Long id, String name, Long userCount, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.userCount = userCount;
        this.updatedAt = updatedAt;
    }
}

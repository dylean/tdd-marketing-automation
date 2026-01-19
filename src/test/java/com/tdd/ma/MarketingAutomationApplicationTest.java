package com.tdd.ma;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MarketingAutomationApplicationTest {

    @Test
    void contextLoads() {
        // 验证 Spring 上下文能正常加载
    }
}

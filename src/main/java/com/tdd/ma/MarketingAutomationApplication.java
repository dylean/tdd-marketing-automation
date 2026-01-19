package com.tdd.ma;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 营销活动自动化平台启动类
 */
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.tdd.ma.infrastructure.persistence")
public class MarketingAutomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketingAutomationApplication.class, args);
    }
}

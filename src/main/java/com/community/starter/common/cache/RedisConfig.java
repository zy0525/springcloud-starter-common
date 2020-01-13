package com.community.starter.common.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: starter-common
 * @description: redis配置
 * @author: zy
 * @create: 2020-01-10 16:13
 **/
@Configuration
@ConditionalOnProperty(name = "starter.redis.enabled")
@Import(CacheConfig.class)
public class RedisConfig {
    @Bean
    public RedisClient redisClient() {
        return new RedisClient();
    }
}

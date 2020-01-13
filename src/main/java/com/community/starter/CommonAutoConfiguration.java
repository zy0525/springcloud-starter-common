package com.community.starter;


import com.community.starter.common.ContextUtil;
import com.community.starter.common.cache.RedisClient;
import com.community.starter.common.cache.RedisConfig;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties
@EnableAspectJAutoProxy(proxyTargetClass = true)
@AutoConfigureBefore({EndpointAutoConfiguration.class, HealthIndicatorAutoConfiguration.class, TraceAutoConfiguration.class, RedisAutoConfiguration.class, CacheAutoConfiguration.class, FeignAutoConfiguration.class, WebClientAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import({RedisConfig.class})
public class CommonAutoConfiguration {
    @Bean
    public ContextUtil contextUtil() {
        return new ContextUtil();
    }
}

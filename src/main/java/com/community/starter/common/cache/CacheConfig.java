package com.community.starter.common.cache;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * @program: starter-common
 * @description: 缓存
 * @author: zy
 * @create: 2020-01-10 16:14
 **/
@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Resource
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                String prefix = StringUtils.join(target.getClass().getName(), method.getName(), ":");

                StringBuilder paramSb = new StringBuilder();
                for (int i = 0; i < params.length; i++) {
                    Object obj = params[i];
                    if (null == obj || obj.getClass().isPrimitive()) {
                        paramSb.append(String.valueOf(obj));
                    } else {
                        String json = JSON.toJSONString(obj);
                        paramSb.append(json);
                    }

                    if (i < params.length - 1) {
                        paramSb.append(":");
                    }
                }

                String param = paramSb.toString();
                if (param.length() > 128) {
                    param = DigestUtils.md5Hex(param);
                }

                return prefix + param;
            }
        };

    }

    // 缓存管理器
    @Bean
    public CacheManager cacheManager() {
        // 设置序列化
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
                // value序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues()
                // 缓存过期时间
                .entryTtl(Duration.ofDays(1L));

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(lettuceConnectionFactory)
                .cacheDefaults(config)
                .transactionAware();
        @SuppressWarnings("serial")
        Set<String> cacheNames = new HashSet<String>() {
            {
                add("codeNameCache");
            }
        };
        builder.initialCacheNames(cacheNames);
        return builder.build();
    }


    /**
     * RedisTemplate配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        // 设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(factory);
        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);// key序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);// value序列化
        redisTemplate.setHashKeySerializer(stringSerializer);// Hash key序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);// Hash value序列化
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
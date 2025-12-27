package com.TMS.ManagementService.configs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.Collections;


@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    public ObjectMapper redisObjectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();

        mapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return mapper;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = redisObjectMapper();
        RedisSerializer<Object> customSerializer = new CustomJsonRedisSerializer(objectMapper);
        RedisSerializer<String> stringSerializer = RedisSerializer.string();


        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith( RedisSerializationContext.SerializationPair.fromSerializer(
                               stringSerializer
                        ))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(customSerializer))
                        .disableCachingNullValues()
                        .entryTtl(Duration.ofMinutes(15))
                        .computePrefixWith(cache -> "tms::" + cache + "::");

        return RedisCacheManager.builder(
                        RedisCacheWriter.nonLockingRedisCacheWriter(
                                connectionFactory,
                                BatchStrategies.scan(1000)
                        ))
                
                .cacheDefaults(defaultConfig)
                .transactionAware()
                .build();
    }
}
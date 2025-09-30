package com.project.ratelimiter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // <-- 추가
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@AutoConfiguration(after = RedisAutoConfiguration.class)
@ConditionalOnClass({RedisTemplate.class, StringRedisSerializer.class})
public class RedisStringTemplateConfig {

    // 이 내부 클래스는 RedisAutoConfiguration을 명시적으로 Import하여 활성화합니다.
    // 이렇게 해야 라이브러리 모듈 컨텍스트에서도 RedisConnectionFactory가 빈으로 등록될 수 있습니다.
    @Configuration // <-- @Configuration 어노테이션 추가
    @Import(RedisAutoConfiguration.class)
    static class RedisConnectionFactoryImporter {
        // 이 클래스는 RedisAutoConfiguration을 가져오기 위한 컨테이너 역할만 합니다.
        // 다른 로직은 필요 없습니다.
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
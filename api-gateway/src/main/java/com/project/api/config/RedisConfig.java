package com.project.api.config; // 패키지명 확인

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }


    // --- 이 부분을 추가합니다. ---
    @Bean // 별도의 Bean 이름을 지정하지 않으면 "redisTemplate"과 이름 충돌이 발생할 수 있으니 주의.
    // 하지만 제네릭 타입이 다르므로 Spring이 구분할 수 있습니다.
    // 명확하게 하려면 @Bean("stringStringRedisTemplate")과 같이 이름을 붙이는 것도 좋습니다.
    public RedisTemplate<String, String> stringStringRedisTemplate(RedisConnectionFactory connectionFactory) { // 메서드명 변경
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer()); // Hash Value도 String
        return template;
    }
    // --- 여기까지 추가 ---
}
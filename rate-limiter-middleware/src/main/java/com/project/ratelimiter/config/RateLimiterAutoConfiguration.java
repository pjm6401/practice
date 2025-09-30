package com.project.ratelimiter.config;

import com.project.ratelimiter.core.RedisTokenBucket;
import com.project.ratelimiter.core.RedisWaitingQueue;
import com.project.ratelimiter.interceptor.RateLimitingInterceptor;
import com.project.ratelimiter.properties.RateLimiterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
// import org.springframework.data.redis.core.RedisTemplate; // 이 임포트 제거
import org.springframework.data.redis.core.StringRedisTemplate; // <-- StringRedisTemplate 임포트 추가
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@AutoConfiguration(after = RedisAutoConfiguration.class)
@EnableConfigurationProperties(RateLimiterProperties.class)
@ConditionalOnProperty(prefix = "rate-limiter", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor // Lombok이 아래 final 필드들을 주입받는 생성자를 자동으로 생성
public class RateLimiterAutoConfiguration implements WebMvcConfigurer {

    private final RateLimiterProperties properties;
    private final StringRedisTemplate stringRedisTemplate; // <-- RedisTemplate<String,String> 대신 StringRedisTemplate으로 변경

    @Bean
    public RedisWaitingQueue redisWaitingQueue() {
        return new RedisWaitingQueue(this.stringRedisTemplate);
    }
    /**
     * RedisTokenBucket Bean 생성
     */
    @Bean
    public RedisTokenBucket redisTokenBucket() { // <-- 파라미터 제거
        // 특정 이름 대신, 클래스에 주입된 StringRedisTemplate 타입을 그대로 사용합니다.
        return new RedisTokenBucket(this.stringRedisTemplate, properties);
    }

    /**
     * RateLimitingInterceptor Bean 생성
     */
    @Bean
    public RateLimitingInterceptor rateLimitingInterceptor(RedisTokenBucket redisTokenBucket) {
        // <-- 3. 생성자에 redisWaitingQueue를 추가하여 전달
        return new RateLimitingInterceptor(
                redisTokenBucket,
                redisWaitingQueue(), // 주입받은 redisWaitingQueue 사용
                properties.getIncludePaths(),
                properties.getExcludePaths()
        );
    }

    /**
     * WebMvcConfigurer를 구현하여 Interceptor를 Spring MVC에 등록
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (properties.isEnabled()) {
            // redisTokenBucket() 메서드를 직접 호출하는 대신, @Bean으로 등록된 redisTokenBucket을 사용합니다.
            // Spring이 자동으로 해당 Bean을 찾아 주입해 줄 것이므로,
            // 여기서는 rateLimitingInterceptor(redisTokenBucket()) 대신 rateLimitingInterceptor를 직접 주입받거나,
            // 아니면 그냥 메서드 호출을 유지하고 Spring이 알아서 Bean을 찾게 두는 방식이 있습니다.
            // 현재 코드 그대로 두어도 Spring이 Bean을 찾아 연결해 줄 가능성이 높습니다.
            RateLimitingInterceptor interceptor = rateLimitingInterceptor(redisTokenBucket());

            registry.addInterceptor(interceptor)
                    .addPathPatterns(properties.getIncludePaths())
                    .excludePathPatterns(properties.getExcludePaths());
            System.out.println("Rate Limiter Interceptor registered for paths: " + Arrays.toString(properties.getIncludePaths()));
            if (properties.getExcludePaths().length > 0) {
                System.out.println("Excluded paths: " + Arrays.toString(properties.getExcludePaths()));
            }
        }
    }

}
package com.project.ratelimiter.core;

import com.project.ratelimiter.properties.RateLimiterProperties;
import org.springframework.data.redis.core.StringRedisTemplate; // <-- StringRedisTemplate 임포트
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component; // <-- @Component 어노테이션 추가
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component // <-- 이 어노테이션을 추가하여 RedisTokenBucket을 Spring Bean으로 등록
public class RedisTokenBucket {

    private final StringRedisTemplate stringRedisTemplate; // <-- 타입 변경
    private final RedisScript<Long> tokenBucketScript;

    private final String keyPrefix;
    private final long capacity;
    private final long refillRate;

    // 생성자 파라미터도 StringRedisTemplate으로 변경
    public RedisTokenBucket(StringRedisTemplate stringRedisTemplate, RateLimiterProperties properties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.keyPrefix = properties.getKeyPrefix();
        this.capacity = properties.getCapacity();
        this.refillRate = properties.getRefillRate();
        this.tokenBucketScript = new DefaultRedisScript<>(getLuaScript(), Long.class);
    }

    public boolean tryConsume() {
        List<String> keys = Arrays.asList(keyPrefix + ":tokens", keyPrefix + ":last_refill");
        List<String> args = Arrays.asList(
                String.valueOf(capacity),
                String.valueOf(refillRate),
                String.valueOf(Instant.now().toEpochMilli())
        );

        // RedisTemplate 대신 StringRedisTemplate 사용 (execute 메서드는 동일하게 작동)
        Long result = stringRedisTemplate.execute(tokenBucketScript, keys, args.toArray());
        return result != null && result == 1L;
    }

    private String getLuaScript() {
        // 이 메서드의 내용을 아래 스크립트로 교체합니다.
        return """
            local tokens_key = KEYS[1]
            local last_refill_key = KEYS[2]
            local capacity = tonumber(ARGV[1])
            local refill_rate = tonumber(ARGV[2]) -- 이제 이 값은 '분당' 리필 개수입니다.
            local now_millis = tonumber(ARGV[3])

            local current_tokens = redis.call("GET", tokens_key)
            if not current_tokens then
                current_tokens = capacity
                redis.call("SET", tokens_key, capacity)
            else
                current_tokens = tonumber(current_tokens)
            end

            local last_refill_time = redis.call("GET", last_refill_key)
            if not last_refill_time then
                last_refill_time = now_millis
                redis.call("SET", last_refill_key, now_millis)
            else
                last_refill_time = tonumber(last_refill_time)
            end

            -- [수정 핵심 1] 경과 시간을 밀리초에서 '분' 단위로 계산합니다. (1000 * 60 = 60000)
            local time_passed_minutes = (now_millis - last_refill_time) / 60000
            if time_passed_minutes > 0 then
                -- [수정 핵심 2] 경과 '분'과 '분당' 리필율을 곱하여 추가할 토큰 수를 계산합니다.
                local tokens_to_add = math.floor(time_passed_minutes * refill_rate)
                if tokens_to_add > 0 then
                    local new_tokens = math.min(capacity, current_tokens + tokens_to_add)
                    redis.call("SET", tokens_key, new_tokens)
                    current_tokens = new_tokens
                end
                -- [수정 핵심 3] 마지막 리필 시간을 현재 시간으로 갱신합니다.
                -- 정확한 계산을 위해 분 단위로 나누어 떨어지는 시간 대신 현재 시간 자체를 저장하는 것이 좋습니다.
                redis.call("SET", last_refill_key, now_millis)
            end

            if current_tokens > 0 then
                redis.call("DECR", tokens_key)
                return 1
            else
                return 0
            end
        """;
    }
}
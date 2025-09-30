package com.project.api.service;

import com.project.ratelimiter.core.RedisWaitingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerService {

    private final RedisWaitingQueue redisWaitingQueue;
    private final StringRedisTemplate redisTemplate;

    /**
     * 10초마다 주기적으로 실행되어 대기열을 처리합니다.
     */
    @Scheduled(fixedRate = 10000)
    public void processQueue() {
        // 처리할 대기자가 없으면 즉시 종료
        if (redisWaitingQueue.isEmpty()) {
            return;
        }

        // 대기열의 가장 앞에서 한 명을 꺼냅니다 (FIFO)
        String ticket = redisWaitingQueue.poll();
        if (ticket == null) {
            return;
        }

        log.info("Processing ticket: {}", ticket);

        // --- 여기에 원래 시간이 오래 걸리는 비즈니스 로직이 들어갑니다 ---
        // (예: 외부 API 호출, DB 대량 작업 등)
        // 현재는 즉시 처리된 것으로 간주합니다.

        // 작업이 완료되었음을 Redis에 기록합니다. (결과 키 = "result:티켓", 값 = "success")
        // 5분 후에는 자동으로 사라지도록 만료 시간(TTL)을 설정합니다.
        String resultKey = "result:" + ticket;
        redisTemplate.opsForValue().set(resultKey, "success", 5, TimeUnit.MINUTES);

        log.info("Finished processing ticket: {}", ticket);
    }
}
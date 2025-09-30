package com.project.api.controller;

// ... 기존 imports ...
import com.project.ratelimiter.core.RedisWaitingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final RedisWaitingQueue redisWaitingQueue;
    private final StringRedisTemplate redisTemplate;

    /**
     * 1. Rate Limiter의 대상이 되는 '진입점' API.
     * 이 API는 실제 작업을 수행하지 않고, 통과 여부만 결정합니다.
     * 인터셉터에 의해 요청이 가로채지므로, 이 메서드는 토큰이 있을 때만 실행됩니다.
     */
    @GetMapping("/process")
    public String processRequest() {
        System.out.println("Request passed through rate limiter immediately.");
        // 즉시 통과된 경우, 바로 실행 권한을 부여.
        return "SUCCESS";
    }

    /**
     * 2. 대기열을 통과한 클라이언트가 호출하는 '실제 실행' API.
     * 대기 페이지에서 상태가 'COMPLETE'가 되면 javascript가 이 API를 호출합니다.
     */
    @PostMapping("/execute")
    public ResponseEntity<String> executeRequest() {
        System.out.println("Execute API called after waiting. Performing actual task...");
        return ResponseEntity.ok("Actual task processed successfully!");
    }

    /**
     * 3. 대기 상태를 확인하는 API (기존과 동일)
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(@RequestParam String ticket) {
        String resultKey = "result:" + ticket;

        // --- 디버깅 로그 추가 ---
        System.out.println("--- Status Check for ticket: "+ticket+" ---");

        // 1. 대기열 확인
        Long rank = redisWaitingQueue.getRank(ticket);
        System.out.println("Checking waiting queue... Rank: "+rank+"" );

        if (rank != null) {
            System.out.println("Ticket found in WAITING queue.");
            return ResponseEntity.ok(Map.of(
                    "status", "WAITING",
                    "rank", rank + 1,
                    "totalWaiters", redisWaitingQueue.size()
            ));
        }

        // 2. 결과 저장소 확인
        String result = redisTemplate.opsForValue().get(resultKey);
        System.out.println("Checking result store... Result for key '"+resultKey+"': "+result+"{}");

        if (result != null) {
            System.out.println("Ticket found in COMPLETE store.");
            redisTemplate.delete(resultKey);
            return ResponseEntity.ok(Map.of("status", "COMPLETE", "result", result));
        }

        // 3. 둘 다 없는 경우
        log.warn("Ticket NOT FOUND in queue or result store. Returning EXPIRED.");
        System.out.println("--------------------------------------");
        return ResponseEntity.ok(Map.of("status", "EXPIRED"));
    }
}
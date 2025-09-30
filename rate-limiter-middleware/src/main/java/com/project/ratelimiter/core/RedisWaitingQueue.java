package com.project.ratelimiter.core;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;


@RequiredArgsConstructor
public class RedisWaitingQueue {

    private final StringRedisTemplate redisTemplate;
    // 대기열로 사용할 Redis Sorted Set의 키
    private static final String WAITING_QUEUE_KEY = "rate-limiter:waiting-queue";

    /**
     * 사용자를 대기열에 추가하고 고유 티켓을 반환합니다.
     * @param userId 사용자 식별자
     * @return 발급된 고유 티켓
     */
    public String add(String userId) {
        // 티켓은 유일성을 보장하기 위해 UUID를 사용합니다.
        // 실제로는 userId와 UUID를 조합하여 'userId:uuid' 형태로 사용할 수도 있습니다.
        String ticket = UUID.randomUUID().toString();

        // score는 현재 시간을 사용하여 먼저 온 요청이 더 낮은 점수를 갖게 합니다.
        long score = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(WAITING_QUEUE_KEY, ticket, score);
        return ticket;
    }

    /**
     * 특정 티켓의 대기 순번(0부터 시작)을 조회합니다.
     * @param ticket 조회할 티켓
     * @return 대기 순번 (0-based rank). 티켓이 없으면 null을 반환합니다.
     */
    public Long getRank(String ticket) {
        return redisTemplate.opsForZSet().rank(WAITING_QUEUE_KEY, ticket);
    }

    /**
     * 대기열의 현재 크기(총 대기자 수)를 반환합니다.
     * @return 대기자 수
     */
    public Long size() {
        return redisTemplate.opsForZSet().zCard(WAITING_QUEUE_KEY);
    }

    /**
     * 대기열이 비어있는지 확인합니다.
     * @return 비어있으면 true, 아니면 false
     */
    public boolean isEmpty() {
        Long queueSize = this.size();
        return queueSize == null || queueSize == 0;
    }

    /**
     * 대기열의 가장 앞에 있는 티켓을 확인만 하고 제거하지는 않습니다.
     * @return 가장 오래된 티켓. 대기열이 비어있으면 null을 반환합니다.
     */
    public String peek() {
        Set<String> tickets = redisTemplate.opsForZSet().range(WAITING_QUEUE_KEY, 0, 0);
        if (tickets == null || tickets.isEmpty()) {
            return null;
        }
        return tickets.iterator().next();
    }

    /**
     * 대기열의 가장 앞에 있는 티켓을 꺼내고(제거하고) 반환합니다.
     * 이 메서드는 토큰이 채워졌을 때 대기자를 처리하는 로직에서 사용됩니다.
     * @return 가장 오래된 티켓. 대기열이 비어있으면 null을 반환합니다.
     */
    public String poll() {
        // ZPOPMIN은 가장 낮은 score를 가진 멤버를 원자적으로 가져오고 삭제합니다. (Redis 5.0 이상)
        // 여기서는 호환성을 위해 range와 remove를 조합합니다.
        Set<String> tickets = redisTemplate.opsForZSet().range(WAITING_QUEUE_KEY, 0, 0);
        if (tickets == null || tickets.isEmpty()) {
            return null;
        }
        String oldestTicket = tickets.iterator().next();
        redisTemplate.opsForZSet().remove(WAITING_QUEUE_KEY, oldestTicket);
        return oldestTicket;
    }
}

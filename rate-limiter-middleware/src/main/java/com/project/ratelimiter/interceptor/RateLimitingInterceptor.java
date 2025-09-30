package com.project.ratelimiter.interceptor;

import com.project.ratelimiter.core.RedisTokenBucket;
import com.project.ratelimiter.core.RedisWaitingQueue; // 1. 대기열 서비스 임포트
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RedisTokenBucket redisTokenBucket;
    private final RedisWaitingQueue redisWaitingQueue; // 2. 대기열 의존성 추가
    private final String[] includePaths;
    private final String[] excludePaths;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // --- 경로 필터링 로직 (기존과 동일) ---
        // 제외 경로 먼저 체크
        for (String pattern : excludePaths) {
            if (pathMatcher.match(pattern, requestURI)) {
                return true; // 제외 경로면 Rate Limiter 적용 안 함
            }
        }

        // 포함 경로 체크
        boolean shouldInclude = false;
        // includePaths가 설정되지 않았거나 `/**` 같은 전체 패턴일 경우를 대비해,
        // 기본적으로 모든 경로를 대상으로 하도록 설정할 수 있습니다.
        // 여기서는 명시적으로 includePaths에 포함된 경우만 처리합니다.
        if (includePaths != null && includePaths.length > 0) {
            for (String pattern : includePaths) {
                if (pathMatcher.match(pattern, requestURI)) {
                    shouldInclude = true;
                    break;
                }
            }
        } else {
            // includePaths가 비어있다면, 모든 경로를 대상으로 처리 (excludePaths는 이미 필터링됨)
            shouldInclude = true;
        }

        if (!shouldInclude) {
            return true; // 포함 경로에 해당하지 않으면 Rate Limiter 적용 안 함
        }
        // --- 경로 필터링 로직 끝 ---


        if (!redisWaitingQueue.isEmpty()) {
            // 대기자가 한 명이라도 있다면, 공정성을 위해 새로운 요청도 바로 대기시킵니다.
            // (토큰이 있더라도 새치기를 허용하지 않습니다.)
            return handleWaiting(request, response);
        }

        // [조건 2] 대기열이 비어있는 경우에만 토큰 소모를 시도합니다.
        if (redisTokenBucket.tryConsume()) {
            // 대기열도 없고, 토큰도 있으므로 요청을 즉시 처리합니다.
            // (시스템이 한가한 상태에서의 첫 3개 요청이 이 경로를 타게 됩니다.)
            return true;
        } else {
            // 대기열은 비어있지만, 토큰이 방금 소진되었습니다.
            // 이 요청이 첫 번째 대기자가 됩니다.
            return handleWaiting(request, response);
        }
    }

    /**
     * 4. 사용자를 대기열에 추가하고, 대기 중이라는 응답을 보내는 헬퍼 메서드
     */
    private boolean handleWaiting(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 실제 서비스에서는 세션이나 JWT 등에서 고유한 사용자 ID를 추출해야 합니다.
        // 여기서는 예시로 클라이언트의 IP 주소를 사용합니다.
        String userId = request.getRemoteAddr();

        // 대기열에 사용자를 추가하고 고유 티켓을 발급받음
        String ticket = redisWaitingQueue.add(userId);
        // 현재 내 대기 순번 확인
        Long rank = redisWaitingQueue.getRank(ticket);

        response.sendRedirect("/wait?ticket=" + ticket);

        return false; // 요청 처리를 여기서 중단
    }
}
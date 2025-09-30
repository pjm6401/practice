package com.project.ratelimiter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limiter") // application.yml에서 rate-limiter.xxx 로 설정 가능
public class RateLimiterProperties {

    private boolean enabled = true; // Rate Limiter 활성화 여부
    private String keyPrefix = "global:rate_limit"; // Redis에 사용될 키의 접두사
    private long capacity = 3; // 토큰 버킷의 최대 용량
    private long refillRate = 3; // 분당 채워지는 토큰 수 (RPS)
    private String[] includePaths = {"/api/process"}; // Rate Limiter를 적용할 경로 패턴 (기본값: 모든 경로)
    private String[] excludePaths = {"/api/status","/wait","/api/execute"}; // Rate Limiter 적용에서 제외할 경로 패턴

    // --- Getter & Setter --- (IDE에서 자동 생성 권장)
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getKeyPrefix() { return keyPrefix; }
    public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }
    public long getCapacity() { return capacity; }
    public void setCapacity(long capacity) { this.capacity = capacity; }
    public long getRefillRate() { return refillRate; }
    public void setRefillRate(long refillRate) { this.refillRate = refillRate; }
    public String[] getIncludePaths() { return includePaths; }
    public void setIncludePaths(String[] includePaths) { this.includePaths = includePaths; }
    public String[] getExcludePaths() { return excludePaths; }
    public void setExcludePaths(String[] excludePaths) { this.excludePaths = excludePaths; }
}

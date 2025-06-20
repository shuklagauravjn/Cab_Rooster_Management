package com.cabrooster.service;

import com.cabrooster.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Value("${bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity:10}")
    private int capacity;
    
    @Value("${bucket4j.filters[0].rate-limits[0].bandwidths[0].time:1}")
    private int time;
    
    @Value("${bucket4j.filters[0].rate-limits[0].bandwidths[0].unit:MINUTES}")
    private String timeUnit;
    
    public void checkRateLimit(String ipAddress) {
        Bucket bucket = cache.computeIfAbsent(ipAddress, this::createNewBucket);
        
        if (!bucket.tryConsume(1)) {
            long secondsToRefill = (long) Math.ceil(
                (1.0 - bucket.getAvailableTokens()) * (60.0 / capacity)
            );
            throw new RateLimitExceededException(
                "Too many requests. Please try again in " + secondsToRefill + " seconds.",
                secondsToRefill
            );
        }
    }
    
    private Bucket createNewBucket(String key) {
        Duration duration = "HOURS".equalsIgnoreCase(timeUnit) ? 
            Duration.ofHours(time) : Duration.ofMinutes(time);
            
        return Bucket4j.builder()
            .addLimit(Bandwidth.classic(
                capacity,
                Refill.intervally(capacity, duration)
            ))
            .build();
    }
}

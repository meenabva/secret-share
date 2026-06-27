package com.example.secret_share.service;

import com.example.secret_share.exception.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimiterService {

    public static final int WINDOW = 3600;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SCRIPT = """
        local key        = KEYS[1]
        local limit      = tonumber(ARGV[1])
        local window     = tonumber(ARGV[2])
        local now        = tonumber(ARGV[3])
        local clearBefore = now - window

        redis.call('ZREMRANGEBYSCORE', key, 0, clearBefore)
        local count = redis.call('ZCARD', key)

        if count < limit then
            redis.call('ZADD', key, now, now)
            redis.call('EXPIRE', key, window)
            return 1
        end
        return 0
        """;

    public void allowCreate(String ip) {
        if(check("rl:create:" + ip, 3)){
            throw new TooManyRequestsException("Too many create requests. Try again later.");
        }
    }

    public void allowRead(String ip)   {
        if(check("rl:read:" + ip, 2)){
            throw new TooManyRequestsException("Too many read requests. Try again later.");
        }
    }

    private boolean check(String key, int limit) {
        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(SCRIPT, Long.class),
                List.of(key),
                String.valueOf(limit),
                String.valueOf(WINDOW),
                String.valueOf(System.currentTimeMillis() / 1000)
        );
        return !Long.valueOf(1L).equals(result);
    }
}

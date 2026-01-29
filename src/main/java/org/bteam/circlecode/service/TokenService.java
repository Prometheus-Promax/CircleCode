package org.bteam.circlecode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    private final String PREFIX = "jwt:valid";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void addToken(String token, long expireSeconds, String userId) {
        redisTemplate.opsForValue().set(PREFIX + token, userId, expireSeconds, TimeUnit.SECONDS);
    }

    public boolean isValid(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }

    public void removeToken(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}

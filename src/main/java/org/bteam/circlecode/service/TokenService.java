package org.bteam.circlecode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    private final String PREFIX = "jwt:valid";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void addToken(String token, long expireSeconds, String userId) {
        redisTemplate.opsForValue().set(PREFIX + userId, token, expireSeconds, TimeUnit.SECONDS);
    }

    public boolean isValid(String token, String userId) {
        String storedToken = redisTemplate.opsForValue().get(PREFIX + userId);
        return Objects.equals(token, storedToken);
    }

    public void removeToken(String userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}

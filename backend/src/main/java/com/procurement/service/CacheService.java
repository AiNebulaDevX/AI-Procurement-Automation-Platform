package com.procurement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }
    
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    public void deletePattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
    
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    public void setHash(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
    
    public Object getHash(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }
    
    public Map<Object, Object> getHashAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    public void deleteHash(String key, String... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }
    
    public void setList(String key, List<Object> values) {
        redisTemplate.opsForList().rightPushAll(key, values);
    }
    
    public List<Object> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }
    
    public void setList(String key, List<Object> values, Duration timeout) {
        redisTemplate.opsForList().rightPushAll(key, values);
        redisTemplate.expire(key, timeout);
    }
    
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }
    
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
    
    public void expire(String key, Duration timeout) {
        redisTemplate.expire(key, timeout);
    }
    
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }
}

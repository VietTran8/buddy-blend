package vn.edu.tdtu.service.impl;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.service.interfaces.RedisService;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl<V> implements RedisService<V> {
    private final RedisTemplate<String, V> redisTemplate;
    private final HashOperations<String, String, V> hashOperations;
    private final SetOperations<String, V> setOperations;

    public RedisServiceImpl(RedisTemplate<String, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.setOperations = redisTemplate.opsForSet();
    }

    @Override
    public void set(String key, String field, V value) {
        hashOperations.put(key, field, value);
    }

    @Override
    public void set(String key, String field, V value, long ttlInSeconds) {
        hashOperations.put(key, field, value);
        redisTemplate.expire(key, ttlInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public V get(String key, String field) {
        return hashOperations.get(key, field);
    }

    @Override
    public void set(String key, V value, long ttlInSeconds) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, ttlInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public V get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void delete(String key, String field) {
        hashOperations.delete(key, field);
    }

    @Override
    public void extendsTtl(String key, long ttlInSeconds) {
        redisTemplate.expire(key, ttlInSeconds, TimeUnit.SECONDS);
    }

    @SafeVarargs
    @Override
    public final void addToSet(String key, V... value) {
        setOperations.add(key, value);
    }

    @Override
    public Set<V> getFromSet(String key) {
        return setOperations.members(key);
    }

    @SafeVarargs
    @Override
    public final void removeFromSet(String key, V... value) {
        setOperations.remove(key, value);
    }

    @Override
    public long getSetSize(String key) {
        return setOperations.size(key);
    }
}

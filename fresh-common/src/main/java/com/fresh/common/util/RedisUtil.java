package com.fresh.common.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    public Long decrement(String key, long delta) {
        return stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    public void hSet(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    public String hGet(String key, String hashKey) {
        Object obj = stringRedisTemplate.opsForHash().get(key, hashKey);
        return obj != null ? obj.toString() : null;
    }

    public Map<Object, Object> hGetAll(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public Boolean hDelete(String key, Object... hashKeys) {
        stringRedisTemplate.opsForHash().delete(key, hashKeys);
        return true;
    }

    public Boolean hHasKey(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public void sAdd(String key, String... values) {
        stringRedisTemplate.opsForSet().add(key, values);
    }

    public Set<String> sMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public Boolean sIsMember(String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    public Long sSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    public Long sRemove(String key, Object... values) {
        return stringRedisTemplate.opsForSet().remove(key, values);
    }

    public void lPush(String key, String value) {
        stringRedisTemplate.opsForList().rightPush(key, value);
    }

    public void lPushAll(String key, Collection<String> values) {
        stringRedisTemplate.opsForList().rightPushAll(key, values);
    }

    public List<String> lRange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    public Long lSize(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    public String lIndex(String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    public String lPop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

}

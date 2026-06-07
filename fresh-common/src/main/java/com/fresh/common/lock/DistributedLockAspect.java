package com.fresh.common.lock;

import com.fresh.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class DistributedLockAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String lockKey = getLockKey(joinPoint, distributedLock);
        String requestId = UUID.randomUUID().toString();
        long expireTime = distributedLock.expire();
        TimeUnit timeUnit = distributedLock.timeUnit();
        long waitTime = distributedLock.waitTime();
        TimeUnit waitUnit = distributedLock.waitUnit();

        boolean locked = tryLock(lockKey, requestId, expireTime, timeUnit, waitTime, waitUnit);
        if (!locked) {
            throw new BusinessException("系统繁忙，请稍后再试");
        }

        try {
            return joinPoint.proceed();
        } finally {
            releaseLock(lockKey, requestId);
        }
    }

    private String getLockKey(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String key = distributedLock.key();
        String prefix = distributedLock.prefix();

        if (key.isEmpty()) {
            key = method.getDeclaringClass().getName() + "." + method.getName();
        }

        return prefix + key;
    }

    private boolean tryLock(String key, String value, long expireTime, TimeUnit timeUnit,
                            long waitTime, TimeUnit waitUnit) throws InterruptedException {
        if (waitTime <= 0) {
            return Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                    .setIfAbsent(key, value, expireTime, timeUnit));
        }

        long waitMillis = waitUnit.toMillis(waitTime);
        long startTime = System.currentTimeMillis();
        while (true) {
            Boolean result = stringRedisTemplate.opsForValue()
                    .setIfAbsent(key, value, expireTime, timeUnit);
            if (Boolean.TRUE.equals(result)) {
                return true;
            }
            if (System.currentTimeMillis() - startTime > waitMillis) {
                return false;
            }
            Thread.sleep(100);
        }
    }

    private void releaseLock(String key, String value) {
        stringRedisTemplate.execute(
                new org.springframework.data.redis.core.script.DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class),
                java.util.Collections.singletonList(key),
                value
        );
    }

}

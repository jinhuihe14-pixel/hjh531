package com.fresh.common.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String key() default "";

    String prefix() default "lock:";

    long expire() default 30;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long waitTime() default 0;

    TimeUnit waitUnit() default TimeUnit.SECONDS;

}

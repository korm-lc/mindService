package org.xaut.voicemindserver.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class TimestampInjectAspect {

    @Before("@annotation(org.xaut.voicemindserver.annotation.InjectTimestamps)")
    public void injectTimestamps(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg == null) continue;

            Class<?> clazz = arg.getClass();

            try {
                Method setCreatedAt = clazz.getMethod("setCreatedAt", LocalDateTime.class);
                Method getCreatedAt = clazz.getMethod("getCreatedAt");

                Method setLastUsedAt = clazz.getMethod("setLastUsedAt", LocalDateTime.class);

                Object createdAtVal = getCreatedAt.invoke(arg);
                LocalDateTime now = LocalDateTime.now();

                // 如果 createdAt 为空，则赋值当前时间
                if (createdAtVal == null) {
                    setCreatedAt.invoke(arg, now);
                }
                // lastUsedAt 每次都更新时间
                setLastUsedAt.invoke(arg, now);

            } catch (NoSuchMethodException e) {
                // 没有对应方法则跳过
            }
        }
    }
}

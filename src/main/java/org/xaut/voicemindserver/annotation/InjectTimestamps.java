package org.xaut.voicemindserver.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)  // 用在方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectTimestamps {
}

package org.xaut.voicemindserver.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xaut.voicemindserver.utils.TokenBucketManager;

@Configuration
public class RateLimiterConfig {
    @Bean
    public TokenBucketManager tokenBucketManager() {
        // 比如：桶容量100，1秒补10个令牌
        return new TokenBucketManager(100, 10, 1000);
    }
}
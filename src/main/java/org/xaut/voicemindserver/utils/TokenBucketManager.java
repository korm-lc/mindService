package org.xaut.voicemindserver.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketManager {

    private final long capacity;
    private final long refillIntervalMillis;
    private final long tokensPerRefill;

    private static class Bucket {
        AtomicLong tokens;
        volatile long lastRefillTimestamp;

        Bucket(long capacity) {
            this.tokens = new AtomicLong(capacity);
            this.lastRefillTimestamp = System.currentTimeMillis();
        }
    }

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    public TokenBucketManager(long capacity, long tokensPerRefill, long refillIntervalMillis) {
        this.capacity = capacity;
        this.tokensPerRefill = tokensPerRefill;
        this.refillIntervalMillis = refillIntervalMillis;
    }

    private void refill(Bucket bucket) {
        long now = System.currentTimeMillis();
        if (now > bucket.lastRefillTimestamp) {
            long intervals = (now - bucket.lastRefillTimestamp) / refillIntervalMillis;
            if (intervals > 0) {
                long newTokens = intervals * tokensPerRefill;
                long currentTokens = bucket.tokens.get();
                long updatedTokens = Math.min(capacity, currentTokens + newTokens);
                bucket.tokens.set(updatedTokens);
                bucket.lastRefillTimestamp += intervals * refillIntervalMillis;
            }
        }
    }

    public boolean tryConsume(String userId) {
        Bucket bucket = userBuckets.computeIfAbsent(userId, k -> new Bucket(capacity));
        synchronized (bucket) {
            refill(bucket);
            long tokens = bucket.tokens.get();
            if (tokens > 0) {
                bucket.tokens.decrementAndGet();
                return true;
            }
            return false;
        }
    }
}

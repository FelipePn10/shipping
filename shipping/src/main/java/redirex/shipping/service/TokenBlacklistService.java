package redirex.shipping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String BLACKLIST_KEY_PREFIX = "blacklist_token:";

    private final RedisTemplate<String, String> redisTemplate;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addToBlacklist(String token, long ttlInSeconds) {
        try {
            String key = buildRedisKey(token);
            redisTemplate.opsForValue().set(key, "revoked", ttlInSeconds, TimeUnit.SECONDS);
            logger.debug("Token blacklisted: {}", token);
        } catch (Exception e) {
            logger.error("Error adding token to blacklist: {}", token, e);
            throw new RedisOperationException("Failed to blacklist token", e);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            Boolean isBlacklisted = redisTemplate.hasKey(buildRedisKey(token));
            return Boolean.TRUE.equals(isBlacklisted);
        } catch (Exception e) {
            logger.error("Error checking token blacklist status: {}", token, e);
            throw new RedisOperationException("Failed to check token blacklist status", e);
        }
    }

    private String buildRedisKey(String token) {
        return BLACKLIST_KEY_PREFIX + token;
    }

    public static class RedisOperationException extends RuntimeException {
        public RedisOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
package ru.naumen.naumenlocalchat.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.concurrent.TimeUnit;

/**
 * Конфигурация кэша Caffeine
 */
@Configuration
public class CacheConfig {

    /**
     * Кэш токенов сброса паролей - живут 20 минут
     */
    @Bean
    public Cache<String, Long> resetPasswordTokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(20, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    /**
     * Кэш токенов подтверждения Email - живут 60 минут
     */
    @Bean
    public Cache<String, Long> emailConfirmTokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }
}
